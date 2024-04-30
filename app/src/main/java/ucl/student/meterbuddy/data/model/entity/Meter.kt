package ucl.student.meterbuddy.data.model.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import ucl.student.meterbuddy.data.model.enums.MeterIcon
import ucl.student.meterbuddy.data.model.enums.MeterType
import ucl.student.meterbuddy.data.model.enums.MeterUnit

@Entity
@Parcelize
data class Meter (
    @PrimaryKey(autoGenerate = true) val meterID: Int = -1,
    val meterName: String = "",
    val meterUnit: MeterUnit = MeterUnit.KILO_WATT_HOUR,
    val meterIcon: MeterIcon = MeterIcon.Electricity,
    val meterType: MeterType = MeterType.ELECTRICITY,
    val housingID: Int = -1,
    val meterCost: Double = 0.0001,
    val additiveMeter: Boolean =true
) : Parcelable