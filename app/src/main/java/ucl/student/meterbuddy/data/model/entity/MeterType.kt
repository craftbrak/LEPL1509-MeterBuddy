package ucl.student.meterbuddy.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class Type (val type: String) {
    ELECTRICITY("Electricity"),
    GAS("Gas"),
    WATER("Water")
}

@Entity
data class MeterType (
    @PrimaryKey(autoGenerate = true) val meterTypeID: Int,
    val type: Type
)