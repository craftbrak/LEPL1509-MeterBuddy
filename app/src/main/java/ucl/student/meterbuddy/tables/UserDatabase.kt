package ucl.student.meterbuddy.tables

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ucl.student.meterbuddy.tables.entities.Housing
import ucl.student.meterbuddy.tables.entities.Meter
import ucl.student.meterbuddy.tables.entities.MeterReading
import ucl.student.meterbuddy.tables.entities.MeterType
import ucl.student.meterbuddy.tables.entities.User
import ucl.student.meterbuddy.tables.entities.relations.HousingUserCrossRef

@Database(
    entities = [
        Housing::class,
        Meter::class,
        MeterReading::class,
        User::class,
        MeterType::class,
        HousingUserCrossRef::class
    ],
    version = 1
)

abstract class UserDatabase : RoomDatabase() {

    abstract val userDao : UserDao

    companion object {
        private var INSTANCE_DB: UserDatabase ?= null

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