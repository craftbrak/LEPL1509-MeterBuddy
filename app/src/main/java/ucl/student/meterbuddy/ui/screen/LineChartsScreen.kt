package ucl.student.meterbuddy.ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
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
import ucl.student.meterbuddy.data.repository.MeterRepository
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
        val infos : List<MeterTab> = getMetersInfo(meters,graphs)
        val pagerState = rememberPagerState(pageCount = {
            graphs.size
        })
        Scaffold { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                if (meters.isEmpty()) {
                    Text("You don't have a meter.")
                } else {
                    // TODO ( Make a swipeable slider for the graphs )
                    Box(modifier = Modifier.fillMaxSize()){
                        HorizontalPager(
                            state = pagerState

                        ){index ->
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ){
                                graphModel.DisplayChartLine(graph = infos[index].graph, width = 200, height = 300 )
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ){
                                    Text(text = "Total Energy Consumed :")
                                    Text(text = "${infos[index].totalEnergyConsumed}")
                                }
                            }
                        }

                    }
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
    public data class MeterTab(
        val graph: LineChartData,
        val totalCost: Float,
        val totalEnergyConsumed: Float
    )

    @Composable
    private fun getMetersInfo(meters : List<Meter>, graphs: List<LineChartData>) : List<MeterTab>{
        val list = ArrayList<MeterTab>()
        var count : Int = 0
        if(graphs.isEmpty()){
            return list
        }
        println("Size of graph : ${graphs.size} Size of meters : ${meters.size}")
        for(meter in meters){
            val meterScreenModel = getScreenModel<MeterScreenModel,MeterScreenModel.Factory>{
                it.create(meter)
            }
            var readings : List<MeterReading> = meterScreenModel.state.value.readings
            var totalEnergyConsumed : Float = 0f
            for(reading in readings){
                totalEnergyConsumed += reading.value
            }
            if(count < graphs.size) {
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