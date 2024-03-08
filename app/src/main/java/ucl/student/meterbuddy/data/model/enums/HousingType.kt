package ucl.student.meterbuddy.data.model.enums

import android.graphics.drawable.Icon
import ucl.student.meterbuddy.R

enum class HousingType(val type: String, val icon: Icon) {
    House("House", Icon.createWithResource("drawable", R.drawable.baseline_home_24)),
    Flat("Flat", Icon.createWithResource("drawable", R.drawable.baseline_apartment_24)),
    Bungalow("Bungalow", Icon.createWithResource("drawable", R.drawable.baseline_home_24)),
    Other("Other", Icon.createWithResource("drawable", R.drawable.baseline_other_houses_24))
}