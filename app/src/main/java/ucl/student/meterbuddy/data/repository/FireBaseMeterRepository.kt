package ucl.student.meterbuddy.data.repository

import com.google.firebase.firestore.FirebaseFirestore
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
    private val meterCollection = db.collection("meters")
    private val typeConverters = TypeConverters()


    override fun getMeters(): Flow<List<Meter>> = callbackFlow {
        val subscription = meterCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val meters = snapshot?.documents?.mapNotNull { it.toObject<Meter>() }
            if (meters != null) {
                trySend(meters.sortedBy { it.meterType }).isSuccess
            }
        }

        awaitClose { subscription.remove() }
    }

    override fun getHousing(): Flow<List<Resource<Housing, DataException>>> = callbackFlow {
        val subscription = db.collection("housings").addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(listOf(Resource.Error<Housing, DataException>(DataException.UNKNONW_ERROR)))
            }
            snapshot?.documents?.mapNotNull {
                it.toObject<Housing>()
            }?.map { housing -> Success<Housing, DataException>(housing) }?.let {
                trySend(
                    it
                ).isSuccess
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
        db.collection("housings").document(house.housingID.toString()).set(house)
    }

    override fun updateHousing(housing: Housing) {
        db.collection("housings").document(housing.housingID.toString()).set(housing, SetOptions.merge())
    }

    override fun deleteHousing(housing: Housing) {
        db.collection("housings").document(housing.housingID.toString()).delete()
    }

    override fun addUserToHousing(housing: Housing, user: User) {
        db.collection("housings").document(housing.housingID.toString()).collection("members").document(user.userID.toString()).set(user)
        db.collection("users").document(user.userID.toString()).collection("housings").document(housing.housingID.toString()).set(housing)
    }

    override fun removeUserFromHousing(housing: Housing, user: User) {
        db.collection("housings").document(housing.housingID.toString()).collection("members").document(user.userID.toString()).delete()
    }

    override fun getUsers(): List<Resource<User,DataException>> {
        val users = mutableListOf<Resource<User,DataException>>()
        db.collection("users").get().addOnSuccessListener { result ->
            for (document in result) {
                val user = document.toObject<User>()
                users.add(Success(user))
            }
        }
        return users
    }
    override fun addUserData(user: User){
        db.collection("users").document(user.userID.toString()).set(user)
    }
    override fun getMeterReadings(id: Int): Flow<List<MeterReading>> = callbackFlow {
        val subscription = meterCollection.document(id.toString()).collection("readings")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
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
    override fun getMeterAndReadings(): Flow<Map<Meter, List<MeterReading>>> {
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