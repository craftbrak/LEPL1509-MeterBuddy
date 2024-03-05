package ucl.student.meterbuddy.data.model.enums

enum class MeterType (val type: String, val units: List<Unit>, val icon: MeterIcon) {
    ELECTRICITY("Electricity", listOf(Unit.KILO_WATT_HOUR), MeterIcon.Electricity),
    GAS("Gas", listOf(Unit.KILO_WATT_HOUR, Unit.CUBIC_METER, Unit.LITER, Unit.CUBIC_METER), MeterIcon.Gas),
    WATER("Water", listOf(Unit.LITER, Unit.CUBIC_METER),MeterIcon.Water),
    CAR("Car", listOf(Unit.KILO_WATT_HOUR, Unit.LITER, Unit.KILO_WATT_HOUR), MeterIcon.Other),
    HOT_WATER("Hot Water", listOf(Unit.CENTIMETER, Unit.LITER),MeterIcon.Water),
}