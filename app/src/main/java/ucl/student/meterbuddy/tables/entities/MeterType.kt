package ucl.student.meterbuddy.tables.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class Type (val type: String) {
    ELECTRICITY("Electricity"),
    GAS("Gas"),
    WATER("Water")
}

@Entity
data class MeterType (
    @PrimaryKey(autoGenerate = false) val meterTypeID: Int,
    val type: Type
)