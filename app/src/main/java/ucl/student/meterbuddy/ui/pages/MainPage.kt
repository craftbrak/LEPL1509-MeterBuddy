package ucl.student.meterbuddy.ui.pages

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import ucl.student.meterbuddy.ui.component.MeterOverviewCard
import ucl.student.meterbuddy.ui.screen.MeterDetailsScreen
import ucl.student.meterbuddy.ui.screen.AddMeterFormScreen
import ucl.student.meterbuddy.viewmodel.MainPageScreenModel
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import ucl.student.meterbuddy.data.UserDatabase
import ucl.student.meterbuddy.ui.screen.LineChartsScreen
import ucl.student.meterbuddy.viewmodel.ChartLineModel
import ucl.student.meterbuddy.data.repository.LocalMeterRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage(mainPageScreenModel: MainPageScreenModel) {

    val navigator = LocalNavigator.current
    val scope = rememberCoroutineScope()
    val meterRepository = LocalMeterRepository(UserDatabase.getInstance(LocalContext.current).userDao)

    Scaffold(
        modifier = Modifier.fillMaxSize()
                           .pointerInput(Unit) {
                               // Detect if the user swipe to the left
                               detectDragGestures { _, delta ->
                                    if (delta.x > 0) {
                                        scope.launch {
                                            // Push the screen with all the graphs
                                            navigator?.push(LineChartsScreen(lineModel = ChartLineModel(meterRepository)))
                                        }
                                    }
                                }
                            },
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
        floatingActionButton ={
            ExtendedFloatingActionButton(onClick = { navigator?.push(AddMeterFormScreen(mainPageScreenModel)) }) {
                Icon(imageVector = Icons.Outlined.Add, contentDescription = "add Meter")
                Text("Add Meter")
            }
        }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier
            .fillMaxWidth()
            .padding(innerPadding)) {
            items(mainPageScreenModel.state.value.listMeter) { meter ->
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