package ucl.student.meterbuddy.data.repository

import kotlinx.coroutines.flow.Flow
import ucl.student.meterbuddy.data.data_access.UserDao
import ucl.student.meterbuddy.data.model.entity.Housing
import ucl.student.meterbuddy.data.model.entity.Meter
import ucl.student.meterbuddy.data.model.entity.MeterReading
import ucl.student.meterbuddy.data.model.entity.User
import ucl.student.meterbuddy.data.model.enums.MeterType
import ucl.student.meterbuddy.data.utils.DataException
import ucl.student.meterbuddy.data.utils.Resource

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

    override fun getMeterAndReadings(housing: Housing): Flow<Map<Meter, List<MeterReading>>> {
        return userDao.getMeterSAndReadings()
    }

    override suspend fun deleteMeter(meter: Meter) {
        userDao.deleteMeter(meter)
    }

    override fun getHousing(): Flow<Resource<List<Housing>, DataException>> {
        TODO("Not yet implemented")
    }

    override fun addHousing(housing: Housing, user: User) {
        TODO("Not yet implemented")
    }

    override fun updateHousing(housing: Housing) {
        TODO("Not yet implemented")
    }

    override fun deleteHousing(housing: Housing) {
        TODO("Not yet implemented")
    }

    override fun addUserToHousing(housing: Housing, user: User) {
        TODO("Not yet implemented")
    }

    override fun removeUserFromHousing(housing: Housing, user: User) {
        TODO("Not yet implemented")
    }

    override fun getUsers(): Resource<List<User>, DataException> {
        TODO("Not yet implemented")
    }

    override suspend fun getUser(id: String): Resource<User, DataException> {
        TODO("Not yet implemented")
    }

    override fun addUserData(user: User) {
        TODO("Not yet implemented")
    }

    override fun setHomeAndUser(housing: Housing, userId: String) {
        TODO("Not yet implemented")
    }

    override fun setHomeCollection(userId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getHousingMember(housing: Housing): Resource<List<User>, DataException> {
        TODO("Not yet implemented")
    }

    override fun getUsersResource(): Flow<Resource<List<User>, DataException>> {
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

    override fun filteredMetersByType(meters: List<Meter>, type: MeterType): List<Meter> {
        return meters.filter { meter ->
            meter.meterType == type
        }
    }
}