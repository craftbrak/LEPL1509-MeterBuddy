package ucl.student.meterbuddy.data.repository

import kotlinx.coroutines.flow.Flow
import ucl.student.meterbuddy.data.model.entity.Housing
import ucl.student.meterbuddy.data.model.entity.Meter
import ucl.student.meterbuddy.data.model.entity.MeterReading
import ucl.student.meterbuddy.data.model.entity.User
import ucl.student.meterbuddy.data.model.enums.MeterType
import ucl.student.meterbuddy.data.utils.DataException
import ucl.student.meterbuddy.data.utils.Resource

interface MeterRepository {

    fun getMeters() : Flow<List<Meter>>

    fun getMeterReadings(id: Int): Flow<List<MeterReading>>
    fun getMeterAndReadings(housing: Housing): Flow<Map<Meter, List<MeterReading>>>

    suspend fun addMeter(meter: Meter)

    suspend fun updateMeter(meter: Meter)

    suspend fun deleteMeter(meter: Meter)

    suspend fun addReading(reading: MeterReading)

    suspend fun deleteReading(reading: MeterReading)

    suspend fun updateMeterReading(meterReading: MeterReading)

    fun filteredMetersByType(meters: List<Meter>, type: MeterType): List<Meter>

    fun getHousing(): Flow<Resource<List<Housing>, DataException>>
    fun addHousing(housing: Housing, user: User)
    fun updateHousing(housing: Housing)
    fun deleteHousing(housing: Housing)

    fun addUserToHousing(housing: Housing, user: User)
    fun removeUserFromHousing(housing: Housing, user: User)

    fun getUsers(): Resource<List<User>, DataException>
    suspend fun getUser(id: String): Resource<User, DataException>
    fun addUserData(user: User)
    fun setHomeAndUser(housing : Housing, userId: String)
    fun setHomeCollection(userId: String)
    suspend fun getHousingMember(housing: Housing): Resource<List<User>, DataException>
    fun getUsersResource(): Flow<Resource<List<User>, DataException>>
}