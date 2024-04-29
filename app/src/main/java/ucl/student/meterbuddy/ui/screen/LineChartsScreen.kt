package ucl.student.meterbuddy.ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import co.yml.charts.ui.linechart.model.LineChartData
import ucl.student.meterbuddy.R
import ucl.student.meterbuddy.data.model.entity.Meter
import ucl.student.meterbuddy.data.model.entity.MeterReading
import ucl.student.meterbuddy.data.model.enums.MeterType
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
        var meters : List<Meter> = mutableListOf<Meter>()
        for( type in MeterType.entries) {
            meters += mainPageScreenModel.filterMetersByType(type = type)
        }
        val graphs: List<LineChartData> = getListGraphs(mainPageScreenModel)

        if (graphs.isEmpty()) { Text("You need at least one meter with two readings.")  }
        else {
            val listMeterTab: List<MeterTab> = getMetersInfo(meters, graphs)


            Scaffold { innerPadding ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Total Energy Consumption", fontWeight = FontWeight.Bold)

                    LazyColumn(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxHeight()

                    ) {

                        items(listMeterTab) { item ->
                            HorizontalDivider(Modifier.padding(20.dp))
                            GraphBox(param = item)

                        }
                    }
                    HorizontalDivider(Modifier.padding(50.dp))
                }

            }

        }
    }
    @Composable
    fun GraphBox(param : MeterTab){
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ){

            if (param != null){
                // PAs envie de crash stp
                Text(text="${param.title}", fontWeight = FontWeight.Bold)
                ChartLineModel.DisplayChartLine(graph = param.graph, width = LocalConfiguration.current.screenWidthDp - 40, height = 300 )
                Column(
                    horizontalAlignment = Alignment.Start,


                    )
                {
                    Row(
                        horizontalArrangement = Arrangement.Absolute.Left
                    ) {
                        Text(text = "Total Energy Consumed :")
                        Text(text = "${param.totalEnergyConsumed}")
                    }
                    Row(
                        horizontalArrangement = Arrangement.Absolute.Left

                    ) {
                        Text(text = "Total Cost :")
                        Text(text = "${param.totalCost}")
                    }
                }
            }
        }
    }
    @Composable
    fun TextBox(s: String) {
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
                    println(readings.size)
                    val graph = ChartLineModel.createChartLine(
                        readings = readings,
                        type = type,
                        meterUnit = unitOfUser,
                        maxWidth = (LocalConfiguration.current.screenWidthDp - 50).dp,
                    )
                    graphs.add(graph!!)
                }
            }
        }
        println(graphs)
        return graphs
    }

    data class MeterTab(
        val graph: LineChartData,
        val title : String,
        val totalCost: Double,
        val totalEnergyConsumed: Float
    )

    @Composable
    private fun getMetersInfo(meters : List<Meter>, graphs: List<LineChartData>) : List<MeterTab>{
        val list = ArrayList<MeterTab>()
        var count = 0
        if (graphs.isEmpty()) {
            return list
        }
        println("Meters : $meters")
        for (meter in meters) {
            val meterScreenModel = getScreenModel<MeterScreenModel,MeterScreenModel.Factory>{
                it.create(meter)
            }
            var readings : List<MeterReading> = meterScreenModel.state.value.readings
            var totalEnergyConsumed = 0f
            for (reading in readings) {
                totalEnergyConsumed += reading.value
            }
            println("Count $count  < Graph.sizes : ${graphs.size}")
            if (count < graphs.size) {
                val meterTab = MeterTab(
                    graph = graphs[count],
                    title = meter.meterType.type,
                    totalEnergyConsumed = totalEnergyConsumed,
                    totalCost = totalEnergyConsumed.toDouble() * meter.meterCost
                )
                count += 1
                list.add(meterTab)
            }
        }
        println("MetersTab : $list")
        return list
    }
}