package ucl.student.meterbuddy.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ucl.student.meterbuddy.data.model.enums.HousingType


@Entity
data class Housing (
    @PrimaryKey(autoGenerate = true) val housingID: Int,
    val housingName: String,
    val housingType: HousingType,
    val housingSurface: Int,
    val housingNbPersons: Int
)