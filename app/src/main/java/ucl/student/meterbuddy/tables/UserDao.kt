package ucl.student.meterbuddy.tables

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import ucl.student.meterbuddy.tables.entities.Housing
import ucl.student.meterbuddy.tables.entities.Meter
import ucl.student.meterbuddy.tables.entities.MeterReading
import ucl.student.meterbuddy.tables.entities.MeterType
import ucl.student.meterbuddy.tables.entities.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeter(meter: Meter)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHousing(housing: Housing)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeterType(meterType: MeterType)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeterReading(meterReading: MeterReading)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Transaction
    @Query("SELECT H.* FROM Housing as H JOIN HousingUserCrossRef hu on hu.userID JOIN User U on U.userID WHERE U.userID = :userId ")
    suspend fun getHousingWithUserName(userId: Int) : List<Housing>

    @Transaction
    @Query("SELECT U.* FROM User as U JOIN HousingUserCrossRef hu on hu.userID JOIN housing h on h.housingID WHERE h.housingName = :housingName ")
    suspend fun getUsersWithHousingName(housingName: String) : List<User>
}