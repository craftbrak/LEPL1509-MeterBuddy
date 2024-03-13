package ucl.student.meterbuddy.ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import co.yml.charts.common.extensions.isNotNull
import co.yml.charts.ui.linechart.model.LineChartData
import ucl.student.meterbuddy.R
import ucl.student.meterbuddy.data.model.entity.MeterReading
import ucl.student.meterbuddy.data.model.enums.MeterType
import ucl.student.meterbuddy.data.model.enums.MeterUnit
import ucl.student.meterbuddy.viewmodel.ChartLineModel
import ucl.student.meterbuddy.viewmodel.MainPageScreenModel


@OptIn(ExperimentalFoundationApi::class)
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
        val graphs: List<LineChartData> = getListGraphs(mainPageScreenModel)

        Scaffold { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                if (meters.isEmpty()) {
                    Text("You don't have meter.")
                } else {
                    // TODO ( Make a swipeable slider for the graphs )
                }

                // To remove when the swipeable slider is done
                for (graph in graphs) {
                    graphModel.DisplayChartLine(graph = graph, width = LocalConfiguration.current.screenWidthDp, height = 300)
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }

    @Composable
    private fun getListGraphs(mainPageScreenModel: MainPageScreenModel): List<LineChartData> {

        val graphs = mutableListOf<LineChartData>()
        for (type in MeterType.entries) {
            val metersFiltered = mainPageScreenModel.filterMetersByType(type)

            if (metersFiltered.isNotEmpty()) {
                val unitOfUser = type.meterUnits.last()
                val readings = mutableListOf<MeterReading>()

                metersFiltered.forEach { meter ->
                    val readingsNotFiltered = mainPageScreenModel.getMeterReadings(meter)
                    readings += mainPageScreenModel.convertUnitReadings(
                        readingsNotFiltered,
                        meter.meterUnit,
                        unitOfUser
                    )
                }

                if (readings.isNotEmpty()) {
                    val graph = ChartLineModel.createChartLine(readings, type, MeterUnit.KILO_WATT_HOUR)
                    if (graph.isNotNull()) { graphs.add(graph!!) }
                }
            }
        }
        return graphs
    }
}