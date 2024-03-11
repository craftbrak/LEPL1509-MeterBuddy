package ucl.student.meterbuddy.data.model.enums


enum class MeterType (val type: String, val meterUnits: List<MeterUnit>, val icon: MeterIcon) {
    ELECTRICITY("Electricity", listOf(MeterUnit.KILO_WATT_HOUR), MeterIcon.Electricity),
    GAS("Gas", listOf(MeterUnit.KILO_WATT_HOUR, MeterUnit.CUBIC_METER, MeterUnit.LITER, MeterUnit.CUBIC_METER), MeterIcon.Gas),
    WATER("Water", listOf(MeterUnit.LITER, MeterUnit.CUBIC_METER),MeterIcon.Water),
    CAR("Car", listOf(MeterUnit.KILO_WATT_HOUR, MeterUnit.LITER, MeterUnit.KILO_WATT_HOUR), MeterIcon.Other),
    HOT_WATER("Hot Water", listOf(MeterUnit.CENTIMETER, MeterUnit.LITER),MeterIcon.Water),
}