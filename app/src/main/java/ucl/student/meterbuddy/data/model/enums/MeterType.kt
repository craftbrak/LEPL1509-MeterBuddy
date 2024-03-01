package ucl.student.meterbuddy.data.model.enums

enum class MeterType (val type: String, val units: List<Unit>) {
    ELECTRICITY("Electricity", listOf(Unit.KWH)),
    GAS("Gas", listOf(Unit.KWH, Unit.M3, Unit.L, Unit.M3)),
    WATER("Water", listOf(Unit.L, Unit.M3)),
    CAR("Car", listOf(Unit.KWH, Unit.L, Unit.KWH)),
    HOT_WATER("Hot Water", listOf(Unit.CM, Unit.L)),
}