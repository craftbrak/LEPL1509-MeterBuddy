package ucl.student.meterbuddy.data.model.enums

import android.graphics.drawable.Icon
import ucl.student.meterbuddy.R

enum class MeterIcon(val icon: Icon, val iconName: String) {
    Electricity(Icon.createWithResource("drawable", R.drawable.baseline_home_24), "Electricity"),
    Gas(Icon.createWithResource("drawable", R.drawable.baseline_home_24),  "Gas"),
    Water(Icon.createWithResource("drawable", R.drawable.baseline_home_24), "Water"),
    Heating(Icon.createWithResource("drawable", R.drawable.baseline_home_24), "Heating"),
    Other(Icon.createWithResource("drawable", R.drawable.baseline_home_24), "Other")
    //Todo: Make the drawable resources for the icons and add them to the project. Then replace the drawable resources with the actual icons.
}