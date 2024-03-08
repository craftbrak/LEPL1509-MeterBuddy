package ucl.student.meterbuddy.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
data class MeterReading (
    @PrimaryKey(autoGenerate = true) val readingID: Int,
    val meterID: Int,
    var value: Float,
    var date: LocalDateTime,
    var note: String? = null,
)