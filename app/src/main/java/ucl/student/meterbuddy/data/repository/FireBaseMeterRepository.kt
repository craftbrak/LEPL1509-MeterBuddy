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
import ucl.student.meterbuddy.data.model.entity.Meter
import ucl.student.meterbuddy.data.model.entity.MeterReading
import ucl.student.meterbuddy.data.model.enums.MeterType
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
                    trySend(readings.sortedBy { it.date }).isSuccess
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
        meter.meterID = uniqueMeterId
        meterCollection.document(meter.meterID.toString()).set(meter)
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
        meterCollection.document(meterReading.meterID.toString()).collection("readings").document(meterReading.readingID.toString()).set(meterReading,SetOptions.merge())
    }

    override fun filteredMetersByType(meters: List<Meter>, type: MeterType): List<Meter> {
        return meters.filter { it.meterType == type }
    }
}