package ucl.student.meterbuddy.tables.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class Type {
    // TODO
}

@Entity
data class MeterType (
    @PrimaryKey(autoGenerate = true) val meterTypeID: Int,
    val type: Type
)