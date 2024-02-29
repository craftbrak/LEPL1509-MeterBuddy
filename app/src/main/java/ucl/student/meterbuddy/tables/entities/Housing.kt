package ucl.student.meterbuddy.tables.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

// Type of the housing
enum class TypeHousing {
    // TODO
    House, Appartement
}

@Entity
data class Housing (
    @PrimaryKey(autoGenerate = true) val housingID: Int,
    val housingName: String,
    val housingType: TypeHousing,
    val housingSurface: Int,
    val housingNbPersons: Int
)