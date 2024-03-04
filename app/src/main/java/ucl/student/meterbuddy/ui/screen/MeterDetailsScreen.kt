package ucl.student.meterbuddy.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import ucl.student.meterbuddy.data.model.entity.Meter
import ucl.student.meterbuddy.data.model.enums.MeterIcon
import ucl.student.meterbuddy.data.model.enums.Unit
import ucl.student.meterbuddy.ui.pages.MeterPage
import ucl.student.meterbuddy.viewmodel.MeterScreenModel

data class MeterDetailsScreen(val meter: Meter): Screen {
    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel { MeterScreenModel(meter) }
        MeterPage(screenModel)
    }
}
