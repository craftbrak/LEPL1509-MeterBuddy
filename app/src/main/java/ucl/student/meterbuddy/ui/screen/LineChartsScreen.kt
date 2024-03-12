package ucl.student.meterbuddy.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import ucl.student.meterbuddy.R
import ucl.student.meterbuddy.data.model.entity.MeterReading
import ucl.student.meterbuddy.data.model.enums.MeterType
import ucl.student.meterbuddy.data.model.enums.MeterUnit
import ucl.student.meterbuddy.viewmodel.ChartLineModel
import ucl.student.meterbuddy.viewmodel.MainPageScreenModel


class LineChartsScreen: Tab {

    override val options: TabOptions
        @Composable
        get() {
            val title = stringResource(R.string.stat_tab)
            val icon = rememberVectorPainter(ImageVector.vectorResource(id = R.drawable.baseline_query_stats_24))

            return remember {
                TabOptions(
                    index = 1u,
                    title = title,
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        val mainPageScreenModel: MainPageScreenModel = getScreenModel()
        val graphModel = ChartLineModel
        val meters = mainPageScreenModel.state.value.meters

        Scaffold() { innerPadding ->
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
                        val unitOfUser = type.meterUnits.last()
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
                                    MeterUnit.KILO_WATT_HOUR,
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