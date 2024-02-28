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
    @Query("SELECT * FROM user WHERE userName = :userName")
    suspend fun getHousingWithUserName(userName: String) : List<Housing>

    @Transaction
    @Query("SELECT * FROM housing WHERE housingName = :housingName")
    suspend fun getUsersWithHousingName(housingName: String) : List<User>
}