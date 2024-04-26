package ucl.student.meterbuddy.data.model.enums

import ucl.student.meterbuddy.R

enum class HousingType(val type: String, val icon: Int) {
    House("House",  R.drawable.baseline_home_24),
    Flat("Flat", R.drawable.baseline_apartment_24),
    Bungalow("Bungalow", R.drawable.baseline_home_24),
    Other("Other", R.drawable.baseline_other_houses_24)
}