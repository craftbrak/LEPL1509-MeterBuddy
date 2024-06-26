package ucl.student.meterbuddy.data.model.enums


enum class MeterUnit(val unit: String, val symbol: String) {
    KILO_WATT_HOUR("Kilowatt Hour", "kWh"),
    KILO_METER("Kilometer", "Km"),
    CUBIC_METER("Cubic Meter", "m³"),
    LITER("Liter", "L"),
    GALLON("Gallon", "G"),
    GIGA_JOULE("Gigajoule", "Gj"),
    MEGA_WATT_HOUR("Megawatt", "MW"),
    HOUR("Hour", "h"),
    CENTIMETER("Centimeter", "cm"),
    KILOGRAM("Kilogram", "Kg"),
    STAIR("Stair", "St"),
    MEGABYTE("Megabyte", "Mb"),
}