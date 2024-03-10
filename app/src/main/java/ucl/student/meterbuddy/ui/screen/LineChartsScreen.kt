package ucl.student.meterbuddy.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import ucl.student.meterbuddy.data.model.entity.MeterReading
import ucl.student.meterbuddy.data.model.enums.MeterType
import ucl.student.meterbuddy.data.model.enums.Unit
import ucl.student.meterbuddy.viewmodel.ChartLineModel
import ucl.student.meterbuddy.ui.screen.HomeScreen.BottomTabBar
import ucl.student.meterbuddy.viewmodel.MainPageScreenModel


data class LineChartsScreen(val mainPageScreenModel: MainPageScreenModel): Screen {

    @Composable
    override fun Content() {

        val graphModel = ChartLineModel
        val meters = mainPageScreenModel.state.value.meters

        Scaffold(bottomBar = { BottomTabBar() }) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                if (meters.isEmpty()) {
                    // TODO ( Do something better )
                    Text("You don't have meter.")
                }

                for (type in MeterType.entries) {
                    // TODO ( Conversion of Unit ! Let the choice to the user of the unique unit )
                    val metersFiltered = mainPageScreenModel.filterMetersByType(type)
                    if (metersFiltered.isNotEmpty()) {

                        // TODO ( To change = Make a Button to let the choice to the user )
                        val unitOfUser = type.units.last()
                        val readings = mutableListOf<MeterReading>()
                        metersFiltered.forEach { meter ->
                            val readingsNotFiltered = mainPageScreenModel.getMeterReadings(meter)
                            readings += mainPageScreenModel.convertUnitReadings(
                                readingsNotFiltered,
                                meter.meterUnit,
                                unitOfUser
                            )
                            // TODO ( A enlever ! C'est pour Debug )
                            Text("$readings")
                            // val readings = mainPageScreenModel.getMetersReadings(metersFiltered)
                            if (readings.isNotEmpty()) {
                                graphModel.CreateChartLine(
                                    readings,
                                    type,
                                    Unit.KILO_WATT_HOUR,
                                    1000,
                                    1000
                                )
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                }
            }
        }
        // TODO ( 'meters' is an empty list => BUG )
    }
}