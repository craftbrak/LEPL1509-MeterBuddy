package ucl.student.meterbuddy.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import co.yml.charts.ui.linechart.model.LineChartData
import ucl.student.meterbuddy.R
import ucl.student.meterbuddy.data.model.entity.Meter
import ucl.student.meterbuddy.data.model.entity.MeterReading
import ucl.student.meterbuddy.data.model.enums.MeterType
import ucl.student.meterbuddy.data.model.enums.MeterUnit
import ucl.student.meterbuddy.viewmodel.ChartLineModel
import ucl.student.meterbuddy.viewmodel.MainPageScreenModel
import kotlin.math.abs

class LineChartsScreen: Tab {

    data class MeterTab(
        val graph: LineChartData,
        val title: String,
        val unit: MeterUnit,
        val unitCost: String,
        val deltaTotalCost: Double,
        val deltaTotalEnergy: Float
    )

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
        val mainPageScreenModel: MainPageScreenModel = getViewModel<MainPageScreenModel>()
        val listMeterTab: MutableList<MeterTab> = mutableListOf()
        for (type in MeterType.entries) {
            val metersFiltered : List<Meter> = mainPageScreenModel.filterMetersByType(type)
            if (metersFiltered.isNotEmpty())
            {
                val readings = mutableListOf<MeterReading>()
                val unitOfUser = metersFiltered.last().meterUnit
                var totalEnergyConsumed = 0.0f
                var totalEnergyProduced = 0.0f
                var totalCostProduced = 0.0
                var totalCostConsumed = 0.0
                var idxFirstReadings = 0
                metersFiltered.forEach { meter ->
                    val readingsNotFiltered = mainPageScreenModel.getMeterReadings(meter)
                    println(meter)
                    println(readingsNotFiltered)
                    readings += mainPageScreenModel.convertUnitReadings(
                        readingsNotFiltered,
                        meter.meterUnit,
                        unitOfUser,
                        meter.meterType
                    )

                    if (readings.size > idxFirstReadings)
                    {
                        if (meter.additiveMeter)
                        {
                            val currentEnergyConsumed = readings[idxFirstReadings].value
                            totalEnergyConsumed += currentEnergyConsumed
                            totalCostConsumed += currentEnergyConsumed.toDouble() * meter.meterCost
                        }
                        else
                        {
                            val currentEnergyProduced = readings[idxFirstReadings].value
                            totalEnergyProduced += currentEnergyProduced
                            totalCostProduced += currentEnergyProduced.toDouble() * meter.meterCost
                        }
                        idxFirstReadings = readings.size
                    }
                }

                if (readings.size >= 2)
                {
                    val graph = ChartLineModel.createChartLine(
                        readings = readings,
                        type = type,
                        meterUnit = unitOfUser,
                        maxWidth = (LocalConfiguration.current.screenWidthDp - 50).dp,
                    )

                    val title: String
                    if (type == MeterType.ELECTRICITY)    { title = "Electricity" }
                    else if (type == MeterType.GAS)       { title = "Gas" }
                    else if (type == MeterType.HOT_WATER) { title = "Hot water" }
                    else if (type == MeterType.WATER)     { title = "Water" }
                    else if (type == MeterType.CAR)       { title = "Car" }
                    else                                  { title = "Other" }

                    listMeterTab += MeterTab(
                        graph = graph!!,
                        title = title,
                        unit = unitOfUser,
                        unitCost = mainPageScreenModel.state.value.currentUserData?.userCurrency?.symbol!!,
                        deltaTotalCost = totalCostConsumed - totalCostProduced,
                        deltaTotalEnergy = totalEnergyConsumed - totalEnergyProduced
                    )
                }
            }
        }

        if (listMeterTab.isEmpty()) { Text("You need at least one meter with two readings.")  }
        else
        {
            Scaffold(
                bottomBar = {
                    BottomAppBar { }
                }
            ) { innerPadding ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Total Energy Consumption by category", fontWeight = FontWeight.Bold)

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
            Text(text="Type : ${param.title}", fontWeight = FontWeight.Bold)
            ChartLineModel.DisplayChartLine(graph = param.graph, width = LocalConfiguration.current.screenWidthDp - 40, height = 300 )
            Column(
                horizontalAlignment = Alignment.Start,
                )
            {
                Row(
                    horizontalArrangement = Arrangement.Absolute.Left
                ) {
                    val energy: Float
                    if (param.deltaTotalEnergy > 0.0f) {
                        Text(text = "Total Energy Consumed : ")
                        energy = param.deltaTotalEnergy
                    } else {
                        Text(text = "Total Energy Produced : ")
                        energy = abs(param.deltaTotalEnergy)
                    }
                    Text(text = String.format("%.2f", energy))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = param.unit.symbol)
                }
                Row(
                    horizontalArrangement = Arrangement.Absolute.Left
                ) {
                    val costEnergy: Double
                    if (param.deltaTotalCost > 0.0) {
                        Text(text = "Total Cost of Consumption : ")
                        costEnergy = param.deltaTotalCost
                    } else {
                        Text(text = "Total Cost of Production : ")
                        costEnergy = abs(param.deltaTotalCost)
                    }
                    Text(text = String.format("%.2f", costEnergy))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = param.unitCost)
                }
            }
        }
    }
}