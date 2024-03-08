package ucl.student.meterbuddy.ui.screen

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import ucl.student.meterbuddy.data.model.enums.MeterType
import ucl.student.meterbuddy.viewmodel.ChartLineModel
import ucl.student.meterbuddy.viewmodel.MainPageScreenModel

object LineChartsScreen: Screen {

    @Composable
    override fun Content() {

        val context = LocalContext.current

        val graphModel = ChartLineModel
        val mainPageScreenModel = MainPageScreenModel(context)

        for (type in MeterType.entries) {
            val metersFiltered = mainPageScreenModel.filterMetersByType(type)
            val readings = mainPageScreenModel.getMetersReadings(metersFiltered)
            graphModel.CreateChartLine(readings, 300, 300)
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}