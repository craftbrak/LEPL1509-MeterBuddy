package ucl.student.meterbuddy.tables.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class Unit {
    // TODO
    kWh, Wh, A, V, gal, L,           // Électricité, Eau
    J, cal, kcal,                    // Énergie
    Celsius, Fahrenheit, Kelvin,     // Température
    cubicMeter, liter, gallon, pint  // Volume
}

enum class Icon {
    // Todo
}


@Entity
data class Meter (
    @PrimaryKey(autoGenerate = false) val meterID: Int,
    val meterName: String,
    val meterUnit: Unit,
    val meterIcon: Icon,
    val meterTypeID: Int,
    val housingID: Int,
    val meterCost: Double
)