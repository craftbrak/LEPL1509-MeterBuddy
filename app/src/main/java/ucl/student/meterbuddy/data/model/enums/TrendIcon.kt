package ucl.student.meterbuddy.data.model.enums

import ucl.student.meterbuddy.R

enum class TrendIcon(val icon: Int, val iconName: String) {
    Up(R.drawable.trend_up_icon, "Up"),
    Down(R.drawable.trend_down_icon, "Down"),
    Flat(R.drawable.trend_flat_icon, "Flat")
}