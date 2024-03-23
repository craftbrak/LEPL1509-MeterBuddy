package ucl.student.meterbuddy.data.model.enums

import ucl.student.meterbuddy.R

enum class MeterIcon(val icon: Int, val iconName: String) {
    Electricity(R.drawable.electricity_icon, "Electricity"),
    Gas(R.drawable.gas_meter_icon,  "Gas"),
    Water(R.drawable.water_icon, "Water"),
    Heating(R.drawable.baseline_fireplace_24, "Heating"),
    Other( R.drawable.baseline_home_24, "Other"),
    Car( R.drawable.baseline_car, "Car"),
    HotWater(R.drawable.baseline_shower_24, "Hot Water")
}