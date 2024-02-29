package ucl.student.meterbuddy.tables.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity
data class Meter (
    @PrimaryKey(autoGenerate = true) val meterID: Int,
    val meterName: String,
    val meterUnit: Unit,
    val meterIcon: MeterIcon,
    val meterTypeID: Int,
    val housingID: Int,
    val meterCost: Double
)