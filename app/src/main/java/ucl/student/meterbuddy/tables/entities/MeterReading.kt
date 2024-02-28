package ucl.student.meterbuddy.tables.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class Date {
    Day,
    Month,
    Year
}

@Entity
data class MeterReading (
    @PrimaryKey(autoGenerate = false) val readingID: Int,
    val meterID: Int,
    val value: Double,
    val date: Date,
    val note: String,
    val additiveMeter: Boolean
)