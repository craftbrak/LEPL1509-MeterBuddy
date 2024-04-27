package ucl.student.meterbuddy.ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import co.yml.charts.common.extensions.isNotNull
import co.yml.charts.ui.linechart.model.LineChartData
import ucl.student.meterbuddy.R
import ucl.student.meterbuddy.data.model.entity.Meter
import ucl.student.meterbuddy.data.model.entity.MeterReading
import ucl.student.meterbuddy.data.model.enums.MeterType
import ucl.student.meterbuddy.data.model.enums.MeterUnit
import ucl.student.meterbuddy.viewmodel.ChartLineModel
import ucl.student.meterbuddy.viewmodel.MainPageScreenModel
import ucl.student.meterbuddy.viewmodel.MeterScreenModel

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
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Content() {
        val mainPageScreenModel: MainPageScreenModel = getViewModel<MainPageScreenModel>()
        val graphModel = ChartLineModel
        val meters = mainPageScreenModel.state.value.meters
        val graphs: List<LineChartData> = getListGraphs(mainPageScreenModel)

        if (graphs.isEmpty()) { Text("You need at least one meter with two readings.")  }
        else {
            val listMeterTab: List<MeterTab> = getMetersInfo(meters, graphs)
            val pagerState = rememberPagerState(pageCount = { graphs.size })
            TextBox()
            Scaffold { innerPadding ->
                Column(modifier = Modifier.padding(innerPadding)) {

                    Box(modifier = Modifier.fillMaxSize()) {
                        HorizontalPager(
                            state = pagerState

                        ) {index ->
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ){
                                if (listMeterTab.isNotEmpty()){
                                    graphModel.DisplayChartLine(graph = listMeterTab[index].graph, width = 200, height = 300 )
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ){
                                        Text(text = "Total Energy Consumed :")
                                        Text(text = "${listMeterTab[index].totalEnergyConsumed}")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun TextBox() {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .background(color = MaterialTheme.colorScheme.secondary)
                .fillMaxWidth()
                .height(90.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Here are the graphs of the different meters grouped by categories",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Swipe to see more.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    @Composable
    private fun getListGraphs(mainPageScreenModel: MainPageScreenModel): List<LineChartData> {
        val graphs = mutableListOf<LineChartData>()
        for (type in MeterType.entries)
        {
            val metersFiltered = mainPageScreenModel.filterMetersByType(type)

            if (metersFiltered.isNotEmpty())
            {
                val unitOfUser = metersFiltered.last().meterUnit
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

                    BoxWithConstraints {
                        val maxWidth = maxWidth.value
                        val graph = ChartLineModel.createChartLine(readings, type, MeterUnit.KILO_WATT_HOUR, maxWidth.dp)
                        if (graph.isNotNull()) {
                            graphs.add(graph!!)

                        }
                    }
                }
            }
        }

        return graphs
    }

    public data class MeterTab(
        val graph: LineChartData,
        val totalCost: Float,
        val totalEnergyConsumed: Float
    )

    @Composable
    private fun getMetersInfo(meters : List<Meter>, graphs: List<LineChartData>) : List<MeterTab>{
        val list = ArrayList<MeterTab>()
        var count = 0
        if (graphs.isEmpty()) {
            return list
        }
        println("Size of graph : ${graphs.size} Size of meters : ${meters.size}")
        for (meter in meters) {
            val meterScreenModel = getScreenModel<MeterScreenModel,MeterScreenModel.Factory>{
                it.create(meter)
            }
            var readings : List<MeterReading> = meterScreenModel.state.value.readings
            var totalEnergyConsumed = 0f
            for (reading in readings) {
                totalEnergyConsumed += reading.value
            }
            if (count < graphs.size) {
                val meterTab = MeterTab(
                    graph = graphs[count],
                    totalEnergyConsumed = totalEnergyConsumed,
                    totalCost = 0f
                )
                count += 1
                list.add(meterTab)
            }
        }
        return list
    }
}