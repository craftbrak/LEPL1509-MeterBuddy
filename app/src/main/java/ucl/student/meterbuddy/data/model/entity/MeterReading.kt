package ucl.student.meterbuddy.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
data class MeterReading (
    @PrimaryKey(autoGenerate = true) var readingID: Int =-1,
    val meterID: Int = -1 ,
    var value: Float =0f,
    var date: LocalDateTime = LocalDateTime.now(),
    var note: String? = null,
)