package ucl.student.meterbuddy.tables.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class Currency {
    // TODO
}

@Entity
data class User (
    @PrimaryKey(autoGenerate = false) val userID: Int,
    val userName: String,
    val userCurrency: Currency
)