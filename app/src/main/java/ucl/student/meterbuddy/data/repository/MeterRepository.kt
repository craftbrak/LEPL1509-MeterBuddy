package ucl.student.meterbuddy.data.repository

import kotlinx.coroutines.flow.Flow
import ucl.student.meterbuddy.data.model.entity.Meter
import ucl.student.meterbuddy.data.model.entity.MeterReading
import ucl.student.meterbuddy.data.model.enums.MeterType

interface MeterRepository {

    fun getMeters() : Flow<List<Meter>>

    fun getMeterReadings(id: Int): Flow<List<MeterReading>>
    fun getMeterAndReadings(): Flow<Map<Meter, List<MeterReading>>>

    suspend fun addMeter(meter: Meter)

    suspend fun updateMeter(meter: Meter)

    suspend fun deleteMeter(meter: Meter)

    suspend fun addReading(reading: MeterReading)

    suspend fun deleteReading(reading: MeterReading)

    suspend fun updateMeterReading(meterReading: MeterReading)

    fun filteredMetersByType(meters: List<Meter>, type: MeterType): List<Meter>
}