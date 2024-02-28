package ucl.student.meterbuddy.tables.entities

enum class Unit(val unit: String, val symbol: String){
    KWH("Kilowatt Hour", "kWh"),
    M3("Cubic Meter", "m³"),
    L("Liter", "L"),
    G("Gallon", "G"),
    GJ("Gigajoule", "Gj"),
    MWh("Megawatt", "MW"),
    H("Hour", "h"),
    CM("Centimeter", "cm"),
    Kg("Kilogram", "Kg"),
    ST("Staire", "St"),
    MB("Megabyte", "Mb"),
}