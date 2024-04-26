package ucl.student.meterbuddy.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ucl.student.meterbuddy.data.model.enums.HousingType


@Entity
data class Housing (
    @PrimaryKey(autoGenerate = true) val housingID: Int = 0,
    val housingName: String = "My Home",
    val housingType: HousingType = HousingType.House,
    val housingSurface: Float = 50f,
    val housingNbPersons: Int = 2
)