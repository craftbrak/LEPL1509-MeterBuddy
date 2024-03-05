package ucl.student.meterbuddy.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import ucl.student.meterbuddy.data.model.entity.Meter
import ucl.student.meterbuddy.data.model.entity.MeterReading
import ucl.student.meterbuddy.data.model.enums.MeterIcon
import ucl.student.meterbuddy.data.model.enums.MeterType
import ucl.student.meterbuddy.data.model.enums.Unit
import ucl.student.meterbuddy.ui.component.MeterOverviewCard
import ucl.student.meterbuddy.ui.screen.MeterDetailsScreen
import ucl.student.meterbuddy.ui.screen.AddMeterFormScreen
import ucl.student.meterbuddy.ui.screen.LineChartScreen
import ucl.student.meterbuddy.viewmodel.MainPageScreenModel
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage(mainPageScreenModel: MainPageScreenModel) {

    val navigator = LocalNavigator.current
    val meters = mainPageScreenModel.listAllMeters()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
                title = { Text("Meter Menu") },
                actions = {

                }
            )
        },
        {
            Column(
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.padding(16.dp)
            ) {
                ExtendedFloatingActionButton(
                    onClick = { navigator?.push(AddMeterFormScreen(meters)) },
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Icon(imageVector = Icons.Outlined.Add, contentDescription = "add Meter")
                    Text("Add Meter")
                }

                ExtendedFloatingActionButton(
                    onClick = {
                        // Exemple
                        val meter = Meter(1, "Electricity",
                            Unit.KILO_WATT_HOUR, MeterIcon.Electricity, MeterType.ELECTRICITY,1,23121.23,true)
                        val readingsData = listOf<MeterReading>(
                            MeterReading(1, meter.meterID, 1123.24f, LocalDateTime.now()),
                            MeterReading(2, meter.meterID, 2943.1f, LocalDateTime.now()),
                            MeterReading(3, meter.meterID, 2951.346f, LocalDateTime.now()),
                            MeterReading(4, meter.meterID, 4324.131f, LocalDateTime.now()),
                            MeterReading(5, meter.meterID, 9682.123f, LocalDateTime.now())
                        )
                        navigator?.push(LineChartScreen(readingsData))
                    }
                ) {
                    Icon(imageVector = Icons.Outlined.KeyboardArrowLeft, contentDescription = "Graphics")
                    Text("Graphics")
                }
            }
        }

    ) { innerPadding ->
        LazyColumn(modifier = Modifier
            .fillMaxWidth()
            .padding(innerPadding)) {
            items(meters) { meter ->
                MeterOverviewCard(
                    onClick = {
                        navigator?.push(MeterDetailsScreen(meter))
                    },
                    modifier = Modifier.padding(10.dp),
                    meterName = meter.meterName,
                    meterIcon = meter.meterIcon,
                    lastReading = 1938f,
                    readingUnit = meter.meterUnit.unit,
                    tendanceIcon = "up",
                    tendenceValue = 10.0f,
                    monthlyCost = 20.0f,
                    currencySymbol = "£"
                )
            }
        }
    }
}