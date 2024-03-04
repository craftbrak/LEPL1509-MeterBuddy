package ucl.student.meterbuddy.data.model.enums

import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import ucl.student.meterbuddy.R

enum class MeterIcon(val icon: Int, val iconName: String) {
    Electricity(R.drawable.electricity_icon, "Electricity"),
    Gas(R.drawable.gas_meter_icon,  "Gas"),
    Water(R.drawable.water_icon, "Water"),
    Heating(R.drawable.gas_meter_icon, "Heating"),
    Other( R.drawable.baseline_home_24, "Other")
    //Todo: Make the drawable resources for the icons and add them to the project. Then replace the drawable resources with the actual icons.
}