package ucl.student.meterbuddy.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ucl.student.meterbuddy.data.model.enums.Currency

@Entity
data class User (
    @PrimaryKey(autoGenerate = true) val userID: Int,
    val userName: String = "Default_User",
    val userCurrency: Currency = Currency.EUR
)