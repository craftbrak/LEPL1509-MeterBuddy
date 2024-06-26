package ucl.student.meterbuddy.ui.screen

import android.os.Parcelable
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRightAlt
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import co.yml.charts.ui.linechart.model.LineChartData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import ucl.student.meterbuddy.R
import ucl.student.meterbuddy.data.model.entity.Meter
import ucl.student.meterbuddy.data.model.entity.MeterReading
import ucl.student.meterbuddy.data.model.enums.MeterType
import ucl.student.meterbuddy.data.model.enums.MeterUnit
import ucl.student.meterbuddy.ui.component.MeterReadingCard
import ucl.student.meterbuddy.viewmodel.ChartLineModel
import ucl.student.meterbuddy.viewmodel.MeterScreenModel
import java.time.LocalDateTime

@Parcelize
data class MeterDetailsScreen(val meter: Meter) : Screen, Parcelable {

    @Composable
    override fun Content() {
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        val navigator = LocalNavigator.currentOrThrow
//        val context = LocalContext.current
        val meterScreenModel = getScreenModel<MeterScreenModel,MeterScreenModel.Factory>{
            it.create(meter)
        }
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = { AppTopBar(meter.meterName, navigator) },
            floatingActionButton = { AdderButton(meterScreenModel, navigator) },
            bottomBar = { BottomAppBar { } }
        ) { innerPadding ->
            LazyColumn(modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)) {
                item { BoxGraph(meterScreenModel) }
                item { Text(text = "Meter Readings", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(8.dp)) }
                if (meterScreenModel.state.value.readings.isEmpty()) {
                    // item { Text(text = "Create your first reading just here !") }
                    item { AddFirstReadingIndicator(meterScreenModel, navigator)
                    }
                }
                else {
                    items(meterScreenModel.state.value.readings) { reading ->
                        MeterReadingCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            onclick =
                            {
                                navigator.push(AddReadingScreen(meterScreenModel.meter.meterName, reading.date, reading.value,reading.note, true) { value, date, note ->
                                    try {

                                        meterScreenModel.state.value.readings.first { readi -> readi.date == date && readi.readingID != reading.readingID }
                                        return@AddReadingScreen false

                                    } catch (e: NoSuchElementException) {
                                        meterScreenModel.updateReading(
                                            reading.readingID,
                                            value,
                                            date,
                                            note
                                        )

                                    }
                                    return@AddReadingScreen true



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
    }
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun AppTopBar(
        nameMeter: String,
        navigator: Navigator,
    ) {
        val showDialog = remember { mutableStateOf(false) }

        val openDialog = {
            showDialog.value = true
        }

        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                title = { Text(text = "Meter Details") },
                text = {
                    Column {
                        Text(text = "Name: ${meter.meterName}")
                        Text(text = "Unit: ${meter.meterUnit}")
                        Text(text = "Icon: ${meter.meterIcon}")
                        Text(text = "Type: ${meter.meterType}")
                        Text(text = "Cost: ${meter.meterCost}")
                        Text(text = "Additive Meter: ${if (meter.additiveMeter) "Yes" else "No"}"
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = { showDialog.value = false }) {
                        Text("Close")
                    }
                }
            )
        }

        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            title = { Text(text = nameMeter) },
            navigationIcon = {
                IconButton(onClick = { navigator.pop() }) {
                    Icon(imageVector = Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = openDialog) {
                    Icon(imageVector = Icons.Outlined.Info, contentDescription = "Meter Details")
                }
            }
        )
    }

    @Composable
    private fun GraphExample() {
        val readings = listOf(  MeterReading(-1, -1, 10.0f, LocalDateTime.now(), ""),
            MeterReading(-1, -1, 18.0f, LocalDateTime.now(), ""),
            MeterReading(-1, -1, 29.0f, LocalDateTime.now(), ""),
            MeterReading(-1, -1, 32.0f, LocalDateTime.now(), ""),
            MeterReading(-1, -1, 35.0f, LocalDateTime.now(), ""),
            MeterReading(-1, -1, 46.0f, LocalDateTime.now(), ""),
            MeterReading(-1, -1, 51.0f, LocalDateTime.now(), ""),
            MeterReading(-1, -1, 67.0f, LocalDateTime.now(), ""),
            MeterReading(-1, -1, 67.0f, LocalDateTime.now(), "")
        )

        val timeIntervalWeeks = 1L // One week
        var currentDateTime = LocalDateTime.now()

        readings.forEachIndexed { index, reading ->
            reading.date = currentDateTime
            currentDateTime = currentDateTime.plusWeeks(timeIntervalWeeks)
        }

        val meterType = MeterType.ELECTRICITY
        val meterUnit = MeterUnit.KILO_WATT_HOUR

        BoxWithConstraints {
            val maxWidth = maxWidth.value
            val graph: LineChartData?
            if (meter.additiveMeter)
            {
                graph = ChartLineModel.createChartLine(
                    readingsConsumption = readings,
                    readingsProduction = emptyList(),
                    type = meterType,
                    meterUnit = meterUnit,
                    maxWidth = maxWidth.dp
                )
            } else
            {
                graph = ChartLineModel.createChartLine(
                    readingsConsumption = emptyList(),
                    readingsProduction = readings,
                    type = meterType,
                    meterUnit = meterUnit,
                    maxWidth = maxWidth.dp
                )
            }
            ChartLineModel.DisplayChartLine(
                graph = graph!!,
                width = LocalConfiguration.current.screenWidthDp,
                height = 300
            )
        }
    }

    @Composable
    private fun AddFirstReadingIndicator(meterScreenModel: MeterScreenModel, navigator: Navigator) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 32.dp)
                    .background(color = MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Add your first reading",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Capture and track your first reading to start monitoring your usage. Your data will automatically generate a graph for visualization. Here's an example.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    GraphExample()
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                    ) {
                        Text(
                            text = "Click here",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowRightAlt,
                            contentDescription = "Forward Arrow",
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        FloatingActionButton(
                            onClick = {
                                navigator.push(AddReadingScreen(meterScreenModel.meter.meterName) { value, date, note ->
                                    try {

                                        meterScreenModel.state.value.readings.first { readi -> readi.date == date }
                                        return@AddReadingScreen false

                                    } catch (e: NoSuchElementException) {
                                        meterScreenModel.addReading(value, date, note)

                                    }
                                    return@AddReadingScreen true
                                })
                            },
                            containerColor = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier
                                .size(50.dp),
                            shape = CircleShape,
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Add,
                                contentDescription = "Add Reading",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun AdderButton(meterScreenModel: MeterScreenModel, navigator: Navigator) {
        if (meterScreenModel.state.value.readings.isNotEmpty()) {
            FloatingActionButton(
                onClick = { },
                containerColor = MaterialTheme.colorScheme.secondary,
                shape = MaterialTheme.shapes.extraLarge
            ) {
                IconButton(onClick = {
                    navigator.push(AddReadingScreen(meterScreenModel.meter.meterName) { value, date, note ->
                        try {

                            meterScreenModel.state.value.readings.first { readi -> readi.date == date }
                            return@AddReadingScreen false

                        } catch (e: NoSuchElementException) {
                            meterScreenModel.addReading(value, date, note)

                        }
                        return@AddReadingScreen true
                    })
                }, modifier = Modifier) {
                    Icon(imageVector = Icons.Outlined.Add, contentDescription = "Sort")
                }
            }
        }
    }

    @Composable
    private fun BoxGraph(meterScreenModel: MeterScreenModel) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center

        ) {
            Card(modifier = Modifier.padding(16.dp)) {
                Box(
                    modifier = Modifier.padding(10.dp)
                ) {
                    val readings = meterScreenModel.state.value.readings
                    if (readings.size >= 2) {
                        BoxWithConstraints {
                            val maxWidth = maxWidth.value
                            val graph: LineChartData?
                            if (meter.additiveMeter)
                            {
                                graph = ChartLineModel.createChartLine(
                                    readingsConsumption = readings,
                                    readingsProduction = emptyList(),
                                    type = meterScreenModel.meter.meterType,
                                    meterUnit = meterScreenModel.meter.meterUnit,
                                    maxWidth = maxWidth.dp
                                )
                            } else
                            {
                                graph = ChartLineModel.createChartLine(
                                    readingsConsumption = emptyList(),
                                    readingsProduction = readings,
                                    type = meterScreenModel.meter.meterType,
                                    meterUnit = meterScreenModel.meter.meterUnit,
                                    maxWidth = maxWidth.dp
                                )
                            }

                            ChartLineModel.DisplayChartLine(graph = graph!!,
                                width = LocalConfiguration.current.screenWidthDp,
                                height = 300
                            )
                        }
                    } else { Image( painter = painterResource(id = R.drawable.data_pending),
                            contentDescription = "Data Pending",
                            modifier = Modifier
                                .align(Alignment.Center)
                    )}
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
