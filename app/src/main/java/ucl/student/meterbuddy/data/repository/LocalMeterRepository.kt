package ucl.student.meterbuddy.data.repository

import kotlinx.coroutines.flow.Flow
import ucl.student.meterbuddy.data.data_access.UserDao
import ucl.student.meterbuddy.data.model.entity.Meter
import ucl.student.meterbuddy.data.model.entity.MeterReading

class LocalMeterRepository(private val userDao: UserDao): MeterRepository {
    override fun getMeters(): Flow<List<Meter>> {
        return userDao.getMeters()
    }

    override fun getMeterReadings(id: Int): Flow<List<MeterReading>> {
        return userDao.getMeterReadingFromMeterID(id)
    }

    override suspend fun addMeter(meter: Meter) {
        userDao.insertMeter(meter)
    }

    override suspend fun updateMeter(meter: Meter) {
        userDao.updateMeter(meter)
    }

    override suspend fun deleteMeter(id: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun addReading(reading: MeterReading) {
        userDao.insertMeterReading(reading)
    }

    override suspend fun deleteReading(reading: MeterReading) {
        userDao.deleteMeterReading(reading)
    }

    override suspend fun updateMeterReading(meterReading: MeterReading) {
        userDao.updateMeterReading(meterReading)
    }
}