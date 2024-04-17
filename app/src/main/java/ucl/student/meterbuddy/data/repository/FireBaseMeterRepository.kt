package ucl.student.meterbuddy.data.repository

import android.util.Log
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
    private var meterCollection = db.collection("meters")
    private val typeConverters = TypeConverters()
    private var housingCollection = db.collection("housings")


    override fun getMeters(): Flow<List<Meter>> = callbackFlow {
        val subscription = meterCollection.addSnapshotListener { snapshot, error ->
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

        awaitClose { subscription.remove() }
    }

    override fun getHousing(): Flow<Resource<List<Housing>, DataException>> = callbackFlow {
        val subscription = housingCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("MeterRepo", error.toString())
                trySend(Resource.Error(DataException.UNKNONW_ERROR))
            }
            snapshot?.documents?.mapNotNull {
                it.toObject<Housing>()
            }?.let {
                if (it.isEmpty()) {
                    trySend(Resource.Error(DataException.NO_DATA))
                } else {
                    trySend(Success(it)).isSuccess
                }
            }
        }
        awaitClose{ subscription.remove()}

    }

    override fun addHousing(housing: Housing) {
        val house = if (housing.housingID == 0){
            val uuid = UUID.randomUUID()
            val uniqueHousingId = uuid.hashCode()
            housing.copy(
                housingID = uniqueHousingId
            )
        }else housing
        housingCollection.document(house.housingID.toString()).set(house)
    }

    override fun updateHousing(housing: Housing) {
        housingCollection.document(housing.housingID.toString()).set(housing, SetOptions.merge())
    }

    override fun deleteHousing(housing: Housing) {
        housingCollection.document(housing.housingID.toString()).delete()
    }

    override fun addUserToHousing(housing: Housing, user: User) {
        housingCollection.document(housing.housingID.toString()).collection("members").document(user.userID.toString()).set(user)
        db.collection("users").document(user.userID.toString()).collection("housings").document(housing.housingID.toString()).set(housing)
    }

    override fun removeUserFromHousing(housing: Housing, user: User) {
        db.collection("housings").document(housing.housingID.toString()).collection("members").document(user.userID.toString()).delete()
    }

    override fun getUsers(): Resource<List<User>,DataException> {
        val users = mutableListOf<User>()
        db.collection("users").get().addOnSuccessListener { result ->
            for (document in result) {
                val user = document.toObject<User>()
                users.add(user)
            }
        }
        return Success(users.toList())
    }
    override fun addUserData(user: User){
        db.collection("users").document(user.userID.toString()).set(user)
    }
    override fun getMeterReadings(id: Int): Flow<List<MeterReading>> = callbackFlow {
        val subscription = meterCollection.document(id.toString()).collection("readings")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("MeterRepo", error.toString())
                    close(error)
                    return@addSnapshotListener
                }

                val readings =
                    snapshot?.documents?.mapNotNull { typeConverters.mapToMeterReading(it.data as Map<String, Any>) }
                if (readings != null) {
                    trySend(readings.sortedBy { it.date }.reversed()).isSuccess
                }
            }
                awaitClose { subscription.remove() }
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

    override fun getUser(id: String): Resource<User, DataException> {
        var user: Resource<User, DataException> = Resource.Error(DataException.NO_DATA)
        db.collection("users").document(id).get().addOnSuccessListener { result ->
            user = Success(result.toObject<User>()!!)
        }
        return user
    }

    override fun setHomeAndUser(housing: Housing, userId: String) {
        meterCollection = db.collection("users").document(userId).collection("housings").document(housing.housingID.toString()).collection("meters")
        Log.d("SetHomeAndUser",meterCollection.path)
    }

    override fun setHomeCollection(userId: String) {
        housingCollection = db.collection("users").document(userId).collection("housings")
        Log.d("SetHome",housingCollection.path)
    }

    override suspend fun addMeter(meter: Meter) {
        // Generate a unique UUID
        val uuid = UUID.randomUUID()
        // Convert the UUID to a hashcode
        val uniqueMeterId = uuid.hashCode()

        // Set the meterId to the uniqueMeterId
        val met= meter.copy(
            meterID = uniqueMeterId
        )
        meterCollection.document(met.meterID.toString()).set(met)
    }

    override suspend fun updateMeter(meter: Meter) {
        meterCollection.document(meter.meterID.toString()).set(meter, SetOptions.merge())
    }

    override suspend fun deleteMeter(meter: Meter) {
        meterCollection.document(meter.meterID.toString()).delete()
    }

    override suspend fun addReading(reading: MeterReading) {
        val uuid = UUID.randomUUID()
        // Convert the UUID to a hashcode
        val uniqueReadingId = uuid.hashCode()
        reading.readingID = uniqueReadingId
        meterCollection.document(reading.meterID.toString()).collection("readings")
            .document(reading.readingID.toString()).set(typeConverters.meterReadingToMap(reading))
    }

    override suspend fun deleteReading(reading: MeterReading) {
        meterCollection.document(reading.meterID.toString()).collection("readings")
            .document(reading.readingID.toString()).delete()
    }

    override suspend fun updateMeterReading(meterReading: MeterReading) {
        meterCollection.document(meterReading.meterID.toString()).collection("readings").document(meterReading.readingID.toString()).update(typeConverters.meterReadingToMap(meterReading))
    }

    override fun filteredMetersByType(meters: List<Meter>, type: MeterType): List<Meter> {
        return meters.filter { it.meterType == type }
    }
}