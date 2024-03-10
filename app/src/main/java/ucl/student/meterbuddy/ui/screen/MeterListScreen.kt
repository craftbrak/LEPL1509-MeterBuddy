package ucl.student.meterbuddy.ui.screen

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ucl.student.meterbuddy.R
import ucl.student.meterbuddy.ui.component.MeterOverviewCard
import ucl.student.meterbuddy.ui.screen.HomeScreen.BottomTabBar
import ucl.student.meterbuddy.viewmodel.MainPageScreenModel

data class MeterListScreen(val mainPageScreenModel: MainPageScreenModel): Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val scope = rememberCoroutineScope()
        Scaffold(
            modifier = Modifier.fillMaxWidth(),
            topBar = { TopBar() },
            floatingActionButton = { AdderButton(navigator) },
            bottomBar = {BottomTabBar()},
        ) { innerPadding ->

            LazyColumn(modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)) {
                items(mainPageScreenModel.state.value.meters) { meter ->
                    val lastReading = mainPageScreenModel.state.value.lastReading[meter.meterID]?.lastOrNull()?.value
                    MeterOverviewCard(
                        onClick = { navigator?.push(MeterDetailsScreen(meter)) },
                        modifier = Modifier.padding(10.dp),
                        meterName = meter.meterName,
                        meterIcon = meter.meterIcon,
                        lastReading = lastReading?.toString() ?: null,
                        readingUnit = meter.meterUnit.unit,
                        trendIcon = "up",
                        trendValue = 10.0f,
                        monthlyCost = 20.0f,
                        currencySymbol = "£"
                    )
                }
            }
        }
    }
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun TopBar() {
        CenterAlignedTopAppBar(
            colors = topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
            title = { Text( stringResource(id = R.string.meter_menu)) },
            actions = {

            }
        )
    }
    @Composable
    private fun AdderButton(navigator: Navigator?) {
        ExtendedFloatingActionButton(onClick = { navigator?.push(AddMeterFormScreen()) }) {
            Icon(imageVector = Icons.Outlined.Add, contentDescription = "add Meter")
            Text("Add Meter")
        }
    }
    @Composable
    fun SwiperToLeft(navigator: Navigator?, scope: CoroutineScope): Modifier {
        return Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures { _, delta ->
                    if (delta.y > 0) {
                        scope.launch {
                            navigator?.push(LineChartsScreen)
                        }
                    }
                }
            }
    }
}