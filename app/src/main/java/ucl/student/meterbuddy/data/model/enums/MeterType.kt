package ucl.student.meterbuddy.data.model.enums

enum class MeterType (val type: String, val units: List<Unit>) {
    ELECTRICITY("Electricity", listOf(Unit.KILO_WATT_HOUR)),
    GAS("Gas", listOf(Unit.KILO_WATT_HOUR, Unit.CUBIC_METER, Unit.LITER, Unit.CUBIC_METER)),
    WATER("Water", listOf(Unit.LITER, Unit.CUBIC_METER)),
    CAR("Car", listOf(Unit.KILO_WATT_HOUR, Unit.LITER, Unit.KILO_WATT_HOUR)),
    HOT_WATER("Hot Water", listOf(Unit.CENTIMETER, Unit.LITER)),
}