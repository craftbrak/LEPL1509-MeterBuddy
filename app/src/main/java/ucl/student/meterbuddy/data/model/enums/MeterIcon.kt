package ucl.student.meterbuddy.data.model.enums

import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import ucl.student.meterbuddy.R

enum class MeterIcon(val icon: Int, val iconName: String) {
    Electricity(R.drawable.baseline_bungalow_24, "Electricity"),
    Gas(R.drawable.baseline_home_24,  "Gas"),
    Water(R.drawable.baseline_home_24, "Water"),
    Heating(R.drawable.baseline_home_24, "Heating"),
    Other( R.drawable.baseline_home_24, "Other")
    //Todo: Make the drawable resources for the icons and add them to the project. Then replace the drawable resources with the actual icons.
}