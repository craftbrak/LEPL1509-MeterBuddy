package ucl.student.meterbuddy.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import ucl.student.meterbuddy.data.model.entity.Meter
import ucl.student.meterbuddy.ui.pages.MeterPage
import ucl.student.meterbuddy.viewmodel.MeterScreenModel

data class MeterDetailsScreen(val meter: Meter): Screen {

    @Composable
    override fun Content() {
        val context = LocalContext.current
        val screenModel = rememberScreenModel { MeterScreenModel(meter,context) }
        MeterPage(screenModel)
    }
}