package ucl.student.meterbuddy.tables.entities

import androidx.room.Entity
import androidx.room.PrimaryKey



@Entity
data class User (
    @PrimaryKey(autoGenerate = true) val userID: Int,
    val userName: String,
    val userCurrency: Currency
)