package ucl.student.meterbuddy.data.model.enums

import ucl.student.meterbuddy.R

enum class MeterIcon(val icon: Int, val iconName: String) {
    Electricity(R.drawable.electricity_icon, "Electricity"),
    Gas(R.drawable.gas_meter_icon,  "Gas"),
    Water(R.drawable.water_icon, "Water"),
    Heating(R.drawable.gas_meter_icon, "Heating"),
    Other( R.drawable.baseline_home_24, "Other")
}