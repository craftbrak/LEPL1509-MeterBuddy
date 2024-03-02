package ucl.student.meterbuddy.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ucl.student.meterbuddy.data.model.enums.MeterIcon
import ucl.student.meterbuddy.data.model.enums.MeterType
import ucl.student.meterbuddy.data.model.enums.Unit

@Entity
data class Meter (
    @PrimaryKey(autoGenerate = true) val meterID: Int,
    val meterName: String,
    val meterUnit: Unit,
    val meterIcon: MeterIcon,
    val meterType: MeterType,
    val housingID: Int,
    val meterCost: Double
)