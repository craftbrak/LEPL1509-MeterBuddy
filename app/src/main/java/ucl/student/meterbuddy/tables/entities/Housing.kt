package ucl.student.meterbuddy.tables.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Housing (
    @PrimaryKey(autoGenerate = false) val housingID: Int,
    val housingName: String,
    val housingType: HousingType,
    val housingSurface: Int,
    val housingNbPersons: Int
)