package ucl.student.meterbuddy.data.repository

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import ucl.student.meterbuddy.data.model.TypeConverters
import ucl.student.meterbuddy.data.model.entity.Housing
import ucl.student.meterbuddy.data.model.entity.Meter
import ucl.student.meterbuddy.data.model.entity.MeterReading
import ucl.student.meterbuddy.data.model.entity.User
import ucl.student.meterbuddy.data.model.enums.MeterType
import ucl.student.meterbuddy.data.utils.DataException
import ucl.student.meterbuddy.data.utils.Resource
import ucl.student.meterbuddy.data.utils.Resource.Success
import java.util.UUID
import javax.inject.Inject

class FireBaseMeterRepository @Inject constructor(private val db: FirebaseFirestore) :
    MeterRepository {
    private var meterCollection: CollectionReference? = null
    private val typeConverters = TypeConverters()
    private var housingCollection: CollectionReference? = null
    private var rootHousingCollection: CollectionReference? = db.collection("housings")

    override fun getMeters(): Flow<List<Meter>> = callbackFlow {
        val sub = meterCollection?.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("MeterRepo", error.toString())
                if (error.code == FirebaseFirestoreException.Code.PERMISSION_DENIED) {
                    throw Exception("FirebaseFirestoreException")
                } else {
                    close(error)
                }
                return@addSnapshotListener
            }

            val meters = snapshot?.documents?.mapNotNull { it.toObject<Meter>() }
            if (meters != null) {
                trySend(meters.sortedBy { it.meterType }).isSuccess
            }
        }
        awaitClose { sub?.remove() }
    }

    override fun getHousing(): Flow<Resource<List<Housing>, DataException>> = callbackFlow {
    val subscription = housingCollection?.addSnapshotListener { snapshot, error ->
        if (error != null) {
            Log.e("MeterRepo", error.toString())
            trySend(Resource.Error(DataException.UNKNONW_ERROR))
        }
        snapshot?.documents?.mapNotNull { document ->
            val housingPath = document.getString("housingPath")
            if (housingPath != null) {
                runBlocking {
                    val housing = getHousingFromPath(housingPath)
                    housing
                }
            } else {
                null
            }
        }?.let {
            if (it.isEmpty()) {
                trySend(Resource.Error(DataException.NO_DATA))
            } else {
                trySend(Success(it))
            }
        }
    }
    awaitClose { subscription?.remove() }
}
    suspend fun getHousingFromPath(housingPath: String): Housing? {
        val document = db.document(housingPath).get().await()
        return if (document.exists()) {
            document.toObject<Housing>()
        } else {
            null
        }
    }
    override suspend fun getHousingMember(housing: Housing): Resource<List<User>, DataException> {
        val users = mutableListOf<User>()
        val result = rootHousingCollection?.document(housing.housingID.toString())?.collection("members")?.get()?.await()
        result?.forEach { document ->
            val userPath = document.getString("userPath")
            if (userPath != null) {
                val user = getUserFromPath(userPath)
                if (user != null) {
                    users.add(user)
                }
            }
        }
        return if (users.isNotEmpty()) {
            Success(users.toList())
        } else {
            Resource.Error(DataException.NO_DATA)
        }
    }
    suspend fun getUserFromPath(userPath: String): User? {
    val document = db.document(userPath).get().await()
    return if (document.exists()) {
        document.toObject<User>()
    } else {
        null
    }
}
    override fun addHousing(housing: Housing, user: User) {
        val house = if (housing.housingID == 0) {
            val uuid = UUID.randomUUID()
            val uniqueHousingId = uuid.hashCode()
            housing.copy(
                housingID = uniqueHousingId
            )
        } else housing
        val housingPath = "housings/${house.housingID}"
        rootHousingCollection?.document(house.housingID.toString())?.set(house)
        housingCollection?.document(house.housingID.toString())?.set(mapOf("housingPath" to housingPath))
        val userPath = "users/${user.userID}"
        rootHousingCollection?.document(house.housingID.toString())?.collection("members")?.document(user.userID.toString())?.set(mapOf("userPath" to userPath))

    }
    override fun updateHousing(housing: Housing) {
        rootHousingCollection?.document(housing.housingID.toString())?.set(housing, SetOptions.merge())
    }

    override fun deleteHousing(housing: Housing) {
        rootHousingCollection?.document(housing.housingID.toString())?.delete()
    }

    override fun addUserToHousing(housing: Housing, user: User) {
        Log.d("AddUserToHousing", user.userID.toString())
        val userPath = "users/${user.userID}"
        rootHousingCollection?.document(housing.housingID.toString())?.collection("members")
            ?.document(user.userID.toString())?.set(mapOf("userPath" to userPath))
        val housingPath = "housings/${housing.housingID}"
        db.collection("users").document(user.userID.toString()).collection("housings")
            .document(housing.housingID.toString()).set(mapOf("housingPath" to housingPath))
    }

    override fun removeUserFromHousing(housing: Housing, user: User) {
        rootHousingCollection?.document(housing.housingID.toString())?.collection("members")
            ?.document(user.userID.toString())?.delete()
        db.collection("users").document(user.userID.toString()).collection("housings")
            .document(housing.housingID.toString()).delete()
    }

    override fun getUsers(): Resource<List<User>, DataException> {
        val users = mutableListOf<User>()
        db.collection("users").get().addOnSuccessListener { result ->
//            Log.wtf("MeterRepo", result.documents.toString())
            for (document in result) {
                val user = document.toObject<User>()
                users.add(user)
            }
        }
        return Success(users.toList())
    }

    override fun getUsersResource(): Flow<Resource<List<User>, DataException>> {
        return callbackFlow {
            val subscription = db.collection("users").addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("MeterRepo", error.toString())
                    when (error.code) {
                        FirebaseFirestoreException.Code.OK, FirebaseFirestoreException.Code.UNKNOWN, FirebaseFirestoreException.Code.CANCELLED, FirebaseFirestoreException.Code.DATA_LOSS, FirebaseFirestoreException.Code.INTERNAL, FirebaseFirestoreException.Code.UNIMPLEMENTED -> {
                            trySend(Resource.Error(DataException.UNKNONW_ERROR))
                        }

                        FirebaseFirestoreException.Code.INVALID_ARGUMENT, FirebaseFirestoreException.Code.OUT_OF_RANGE, FirebaseFirestoreException.Code.FAILED_PRECONDITION, FirebaseFirestoreException.Code.RESOURCE_EXHAUSTED, FirebaseFirestoreException.Code.ABORTED, FirebaseFirestoreException.Code.DEADLINE_EXCEEDED, FirebaseFirestoreException.Code.ALREADY_EXISTS -> {
                            trySend(Resource.Error(DataException.BAD_REQUEST))
                        }

                        FirebaseFirestoreException.Code.NOT_FOUND -> {
                            trySend(Resource.Error(DataException.NO_DATA))
                        }

                        FirebaseFirestoreException.Code.PERMISSION_DENIED, FirebaseFirestoreException.Code.UNAUTHENTICATED -> {
                            trySend(Resource.Error(DataException.UNAUTHORIZED))
                        }

                        FirebaseFirestoreException.Code.UNAVAILABLE -> {
                            trySend(Resource.Error(DataException.NO_NETWORK))
                        }
                    }
                    close(error)
                    return@addSnapshotListener
                }
                snapshot?.documents?.mapNotNull { it.toObject<User>() }?.let {
                    if (it.isEmpty()) trySend(Resource.Error(DataException.NO_DATA))
                    trySend(Success(it))
                }
            }
            awaitClose { subscription.remove() }
        }
    }

    override fun addUserData(user: User) {
        db.collection("users").document(user.userID.toString()).set(user)
    }

    override fun getMeterReadings(id: Int): Flow<List<MeterReading>> = callbackFlow {

            val sub =meterCollection?.document(id.toString())?.collection("readings")
                ?.addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("MeterRepo", error.toString())
                        close(error)
                        return@addSnapshotListener
                    }

                    val readings =
                        snapshot?.documents?.mapNotNull {
                            typeConverters.mapToMeterReading(
                                it.data as Map<String, Any>
                            )
                        }
                    if (readings != null) {
                        trySend(readings.sortedBy { it.date }.reversed()).isSuccess
                    }
                }
        awaitClose {sub?.remove()}
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getMeterAndReadings(housing: Housing): Flow<Map<Meter, List<MeterReading>>> {
        return getMeters().flatMapLatest { meters ->
            combine(meters.map { meter ->
                getMeterReadings(meter.meterID).map { readings ->
                    meter to readings
                }
            }) { pairs ->
                pairs.toMap()
            }
        }
    }

    override suspend fun getUser(id: String): Resource<User, DataException> {
        return try {
            val document = db.collection("users").document(id).get().await()
            if (document.exists()) {
                Success(document.toObject<User>()!!)
            } else {
                Resource.Error(DataException.NO_DATA)
            }
        } catch (e: Exception) {
            Resource.Error(DataException.UNKNONW_ERROR)
        }
    }

    override fun setHomeAndUser(housing: Housing, userId: String) {
        meterCollection = rootHousingCollection?.document(housing.housingID.toString())?.collection("meters")
        meterCollection?.path?.let { Log.d("SetHomeAndUser", it) }
    }

    override fun setHomeCollection(userId: String) {
        housingCollection = db.collection("users").document(userId).collection("housings")
        housingCollection?.path?.let { Log.d("SetHome", it) }
    }

    override suspend fun addMeter(meter: Meter) {
        // Generate a unique UUID
        val uuid = UUID.randomUUID()
        // Convert the UUID to a hashcode
        val uniqueMeterId = uuid.hashCode()

        // Set the meterId to the uniqueMeterId
        val met = meter.copy(
            meterID = uniqueMeterId
        )
        meterCollection?.document(met.meterID.toString())?.set(met)
    }

    override suspend fun updateMeter(meter: Meter) {
        meterCollection?.document(meter.meterID.toString())?.set(meter, SetOptions.merge())
    }

    override suspend fun deleteMeter(meter: Meter) {
        meterCollection?.document(meter.meterID.toString())?.delete()
    }

    override suspend fun addReading(reading: MeterReading) {
        val uuid = UUID.randomUUID()
        // Convert the UUID to a hashcode
        val uniqueReadingId = uuid.hashCode()
        reading.readingID = uniqueReadingId
        meterCollection?.document(reading.meterID.toString())?.collection("readings")
            ?.document(reading.readingID.toString())?.set(typeConverters.meterReadingToMap(reading))
    }

    override suspend fun deleteReading(reading: MeterReading) {
        meterCollection?.document(reading.meterID.toString())?.collection("readings")
            ?.document(reading.readingID.toString())?.delete()
    }

    override suspend fun updateMeterReading(meterReading: MeterReading) {
        meterCollection?.document(meterReading.meterID.toString())?.collection("readings")
            ?.document(meterReading.readingID.toString())
            ?.update(typeConverters.meterReadingToMap(meterReading))
    }

    override fun filteredMetersByType(meters: List<Meter>, type: MeterType): List<Meter> {
        return meters.filter { it.meterType == type }
    }
}