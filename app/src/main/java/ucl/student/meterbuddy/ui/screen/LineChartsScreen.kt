package ucl.student.meterbuddy.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.sp
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
            val metersFiltered: List<Meter> =
                mainPageScreenModel.state.value.meters.filter { m -> m.meterType == type }
            if (metersFiltered.isNotEmpty())
            {
                var readings_production = mutableListOf<MeterReading>()
                var readings_consumption = mutableListOf<MeterReading>()
                val unitOfUser = metersFiltered.last().meterUnit
                var totalEnergyConsumed = 0.0f
                var totalEnergyProduced = 0.0f
                var totalCostProduced = 0.0
                var totalCostConsumed = 0.0
                var idxFirstReadings_production = 0
                var idxFirstReadings_consumption = 0
                metersFiltered.forEach { meter ->
                    val readingsNotFiltered = mainPageScreenModel.state.value.lastReading[meter.meterID] ?: emptyList()

                    if (meter.additiveMeter) {
                        readings_consumption += mainPageScreenModel.convertUnitReadings(
                            readingsNotFiltered,
                            meter.meterUnit,
                            unitOfUser,
                            meter.meterType
                        )

                        if (readings_consumption.size > idxFirstReadings_consumption)
                        {
                            val currentEnergyConsumed = readings_consumption[idxFirstReadings_consumption].value
                            totalEnergyConsumed += currentEnergyConsumed
                            totalCostConsumed += currentEnergyConsumed.toDouble() * meter.meterCost
                        }

                        idxFirstReadings_consumption = readings_consumption.size
                    }
                    else
                    {
                        readings_production += mainPageScreenModel.convertUnitReadings(
                            readingsNotFiltered,
                            meter.meterUnit,
                            unitOfUser,
                            meter.meterType
                        )

                        if (readings_production.size > idxFirstReadings_production)
                        {
                            val currentEnergyProduced = readings_production[idxFirstReadings_production].value
                            totalEnergyProduced += currentEnergyProduced
                            totalCostProduced += currentEnergyProduced.toDouble() * meter.meterCost
                        }

                        idxFirstReadings_production = readings_production.size
                    }
                }

                if (readings_consumption.size >= 2 || readings_production.size >= 2)
                {
                    val graph: LineChartData?
                    if (readings_consumption.size < 2) {
                        graph = ChartLineModel.createChartLine(
                            readingsConsumption = emptyList(),
                            readingsProduction = readings_production,
                            type = type,
                            meterUnit = unitOfUser,
                            maxWidth = (LocalConfiguration.current.screenWidthDp - 50).dp,
                        )
                    }
                    else if (readings_production.size < 2) {
                        graph = ChartLineModel.createChartLine(
                            readingsConsumption = readings_consumption,
                            readingsProduction = emptyList(),
                            type = type,
                            meterUnit = unitOfUser,
                            maxWidth = (LocalConfiguration.current.screenWidthDp - 50).dp,
                        )
                    }
                    else {
                        graph = ChartLineModel.createChartLine(
                            readingsConsumption = readings_consumption,
                            readingsProduction = readings_production,
                            type = type,
                            meterUnit = unitOfUser,
                            maxWidth = (LocalConfiguration.current.screenWidthDp - 50).dp,
                        )
                    }

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

        if (listMeterTab.isEmpty()) { firstIndicationGraphScreen() }
        else
        {
            Scaffold(
                bottomBar = { BottomAppBar { } }
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
    private fun firstIndicationGraphScreen() {
        Scaffold(
            bottomBar = { BottomAppBar { } },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Welcome to the realm of visualization!", fontWeight = FontWeight.Bold)
                HorizontalDivider(Modifier.padding(20.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Get ready to visualize your energy and water usage like never before!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = "Info",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(16.dp).alignByBaseline()
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Our app intelligently groups your meters by category (electricity, water, etc.), merging those of the same type.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = "Info",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp).alignByBaseline()
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "We distinguish between production lines in green and consumption lines in red on our graphs.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = "Info",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(16.dp).alignByBaseline()
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "You'll also see your total consumption/production and associated cost, calculated considering individual costs before merging similar meters.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                HorizontalDivider(Modifier.padding(20.dp))

                Text(
                    text = "There is no data available for visualization yet. Add at least two readings to one of your meters to start visualizing your energy consumption!",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }
    }


    @Composable
    private fun GraphBox(param : MeterTab){
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
