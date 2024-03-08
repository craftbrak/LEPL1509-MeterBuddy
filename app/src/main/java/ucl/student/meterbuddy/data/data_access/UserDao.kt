package ucl.student.meterbuddy.data.data_access

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ucl.student.meterbuddy.data.model.entity.Housing
import ucl.student.meterbuddy.data.model.entity.Meter
import ucl.student.meterbuddy.data.model.entity.MeterReading
import ucl.student.meterbuddy.data.model.entity.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeter(meter: Meter)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHousing(housing: Housing)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeterReading(meterReading: MeterReading)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Transaction
    @Query("SELECT H.* FROM Housing as H JOIN HousingUserCrossRef hu on hu.userID JOIN User U on U.userID WHERE U.userID = :userId ")
    suspend fun getHousingWithUserID(userId: Int) : List<Housing>

    @Transaction
    @Query("SELECT U.* FROM User as U JOIN HousingUserCrossRef hu on hu.userID JOIN housing h on h.housingID WHERE h.housingName = :housingName ")
    suspend fun getUsersWithHousingName(housingName: String) : List<User>

    @Query("SELECT * FROM MeterReading WHERE meterID= :meterId  ORDER BY date ASC")
    fun getMeterReadingFromMeterID(meterId: Int): Flow<List<MeterReading>>

    @Query("SELECT * FROM MeterReading WHERE meterID= :meterId ORDER BY date ASC ")
    suspend fun getLastMeterReadingFromMeterID(meterId: Int): MeterReading

    @Transaction
    @Query("SELECT * FROM Meter WHERE housingID= :housingId ")
    fun getMetersFromHousing(housingId: Int): Flow<List<Meter>>

    @Query("Select * FROM METER")
    fun getMeters(): Flow<List<Meter>>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateMeter(meter: Meter)
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateMeterReading(meter: MeterReading)

    @Delete
    suspend fun deleteMeter(meter: Meter)

    @Delete
    suspend fun deleteMeterReading(meterReading: MeterReading)
}