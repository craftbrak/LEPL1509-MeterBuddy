package ucl.student.meterbuddy.ui.screen

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import ucl.student.meterbuddy.data.UserDatabase
import ucl.student.meterbuddy.data.model.entity.MeterReading
import ucl.student.meterbuddy.data.model.enums.MeterType
import ucl.student.meterbuddy.data.repository.LocalMeterRepository
import ucl.student.meterbuddy.viewmodel.ChartLineModel

object LineChartsScreen: Screen {

    @Composable
    override fun Content() {

        val context = LocalContext.current

        val graphModel = ChartLineModel
        val meterRepository = LocalMeterRepository(UserDatabase.getInstance(context).userDao)

        val meters = meterRepository.getMeters().collectAsState(initial = emptyList()).value

        for (type in MeterType.entries) {
            val metersFiltered = meterRepository.filteredMetersByType(meters, type)
            val readings = mutableListOf<MeterReading>()
            metersFiltered.forEach { meter ->
                readings += meterRepository.getMeterReadings(meter.meterID).collectAsState(initial = emptyList()).value
            }
            graphModel.CreateChartLine(readings, 300, 300)
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}