package ucl.student.meterbuddy.ui.screen

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import ucl.student.meterbuddy.data.model.entity.Meter
import ucl.student.meterbuddy.ui.pages.MeterPage

data class MeterDetailsScreen(val meter: Meter): Screen {

    @Composable
    override fun Content() {
        MeterPage(meter)
    }
}