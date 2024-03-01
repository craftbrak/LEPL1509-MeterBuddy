package ucl.student.meterbuddy.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date


@Entity
data class MeterReading (
    @PrimaryKey(autoGenerate = true) val readingID: Int,
    val meterID: Int,
    val value: Double,
    val date: Date,
    val note: String,
    val additiveMeter: Boolean
)