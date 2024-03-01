package ucl.student.meterbuddy.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ucl.student.meterbuddy.data.data_access.UserDao
import ucl.student.meterbuddy.data.model.entity.Housing
import ucl.student.meterbuddy.data.model.entity.Meter
import ucl.student.meterbuddy.data.model.entity.MeterReading
import ucl.student.meterbuddy.data.model.entity.User
import ucl.student.meterbuddy.data.model.relations.HousingUserCrossRef

@Database(
    entities = [
        Housing::class,
        Meter::class,
        MeterReading::class,
        User::class,
        HousingUserCrossRef::class
    ],

    version = 1
)
@TypeConverters(ucl.student.meterbuddy.data.model.TypeConverters::class)
abstract class UserDatabase : RoomDatabase() {

    abstract val userDao : UserDao

    companion object {
        private var INSTANCE_DB: UserDatabase?= null

        fun getInstance(context: Context) : UserDatabase {
            synchronized(this) {
                return INSTANCE_DB ?: Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    "user_db"
                ).build().also {
                    INSTANCE_DB = it
                }
            }
        }
    }
}