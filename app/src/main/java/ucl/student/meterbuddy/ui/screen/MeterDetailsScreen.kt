package ucl.student.meterbuddy.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ucl.student.meterbuddy.data.model.entity.Meter
import ucl.student.meterbuddy.ui.component.MeterReadingCard
import ucl.student.meterbuddy.viewmodel.ChartLineModel
import ucl.student.meterbuddy.viewmodel.MeterScreenModel

data class MeterDetailsScreen(val meter: Meter): Screen {

    @Composable
    override fun Content() {
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current
        val meterScreenModel = MeterScreenModel(meter, context)

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = { AppTopBar(meter.meterName, navigator, scope, snackbarHostState) },
            floatingActionButton = { AdderButton(meterScreenModel, navigator) }
        ) { innerPadding ->
            LazyColumn(modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)) {
                item { BoxGraph(meterScreenModel) }
                item { Text(text = "Meter Readings", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(8.dp)) }
                items(meterScreenModel.state.value.readings) { reading ->
                    MeterReadingCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        onclick =
                        {
                            navigator.push(AddReadingScreen(meterScreenModel.meter.meterName, meterScreenModel.context, reading.date, reading.value,reading.note, true) { value, date, note ->
                                meterScreenModel.updateReading(reading.readingID, value, date, note)
                            })
                        },
                        value = reading.value,
                        date = reading.date,
                        note = reading.note,
                        onDeleteClick = { deleteReadingButton(reading.readingID, meterScreenModel, snackbarHostState, scope) }
                    )
                }
            }
        }
    }
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun AppTopBar(nameMeter: String, navigator: Navigator, scope: CoroutineScope, snackbarHostState: SnackbarHostState) {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            title = { Text(text = nameMeter) },
            navigationIcon = {
                IconButton(onClick = {navigator.pop()}) {
                    Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = {
                    scope.launch {
                        val result = snackbarHostState.showSnackbar(
                            message = "Edit Not Implemented Yet",
                            actionLabel = "close"
                        )
                        if (result == SnackbarResult.ActionPerformed) {
                            Log.w("Snackbar", "Snackbar action performed")
                        }
                    }
                }) {
                    Icon(imageVector = Icons.Outlined.Settings, contentDescription = "Delete Meter")
                }
            })
    }

    @Composable
    private fun AdderButton(meterScreenModel: MeterScreenModel, navigator: Navigator) {
        FloatingActionButton(
            // Handle the click
            onClick = { },
            containerColor = MaterialTheme.colorScheme.secondary,
            shape = MaterialTheme.shapes.extraLarge
        ) {
            IconButton(onClick = {
                navigator.push(AddReadingScreen(meterScreenModel.meter.meterName, meterScreenModel.context) { value, date, note ->
                    meterScreenModel.addReading(value, date, note)
                })
            }, modifier = Modifier) {
                Icon(imageVector = Icons.Outlined.Add, contentDescription = "Sort")
            }
        }
    }

    @Composable
    private fun BoxGraph(meterScreenModel: MeterScreenModel) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Card(modifier = Modifier.padding(16.dp)) {
                Box(
                    modifier = Modifier.padding(10.dp)
                ) {
                    val context = LocalContext.current
                    val resources = context.resources
                    val screenWidth = resources.displayMetrics.widthPixels
                    val screenHeight = resources.displayMetrics.heightPixels

                    val readings = meterScreenModel.state.value.readings
                    if (readings.isNotEmpty()) {
                        ChartLineModel.CreateChartLine(
                            readings = readings,
                            type = meterScreenModel.meter.meterType,
                            meterUnit = meterScreenModel.meter.meterUnit,
                            height = screenHeight / 10,
                            width = screenWidth
                        )
                    }
                    else { Text(text = "Need at least one reading of this meter to have a graph.") }
                }
            }
        }
    }

    private fun deleteReadingButton(readingID: Int, meterScreenModel: MeterScreenModel, snackbarHostState: SnackbarHostState, scope: CoroutineScope) {
        scope.launch {
            val result = snackbarHostState.showSnackbar(
                message = "Do you really want to delete this reading?",
                actionLabel = "yes",
                withDismissAction = true
            )
            if (result == SnackbarResult.ActionPerformed) {
                meterScreenModel.deleteReading(readingID)
            }
        }
    }
}


/*
Add a new Feature :

*/