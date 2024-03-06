package ucl.student.meterbuddy.ui.pages

import android.annotation.SuppressLint
import android.content.res.Resources
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
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
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.launch
import ucl.student.meterbuddy.data.UserDatabase
import ucl.student.meterbuddy.data.repository.LocalMeterRepository
import ucl.student.meterbuddy.ui.component.MeterReadingCard
import ucl.student.meterbuddy.ui.screen.AddReadingScreen
import ucl.student.meterbuddy.viewmodel.ChartLineModel
import ucl.student.meterbuddy.viewmodel.MeterScreenModel


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeterPage(meterScreenModel: MeterScreenModel) {

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val navigator = LocalNavigator.currentOrThrow
    val meterRepository = LocalMeterRepository(UserDatabase.getInstance(LocalContext.current).userDao)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                title = { Text(text = meterScreenModel.meter.meterName) },
                navigationIcon = {
                    IconButton(onClick = {navigator.pop()}) {
                        Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        scope.launch {
                            val result = snackbarHostState.showSnackbar(
                                message = "Delete not Implemented yet",
                                actionLabel = "close"
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                Log.w("Snackbar", "Snackbar action performed")
                            }
                        }
                    }) {
                        Icon(imageVector = Icons.Outlined.Delete, contentDescription = "Delete Meter")
                    }
                })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // Handle the click
                },
                containerColor = MaterialTheme.colorScheme.secondary,
                shape = MaterialTheme.shapes.extraLarge
            ){
                IconButton(onClick = {
                    navigator.push(AddReadingScreen(meterScreenModel,meterScreenModel.meter.meterID,meterScreenModel.meter.meterName
                    ) { value, date, note ->
                        meterScreenModel.addReading(value,date,note)
                    })
                    }, modifier = Modifier) {
                    Icon(imageVector = Icons.Outlined.Add, contentDescription = "Sort")
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)){
            item{
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Card(modifier = Modifier.padding(16.dp)) {
                        Box(
                            modifier = Modifier.padding(70.dp)
                        ) {

                            // TODO(Mettre le graphique parfaitement dans la Box
                            //  + Changer la fonction createChartLine() pour
                            //  pouvoir positionner le graphique où on veut)

                            val context = LocalContext.current
                            val resources: Resources = context.resources
                            val screenWidth: Int = resources.displayMetrics.widthPixels
                            val screenHeight: Int = resources.displayMetrics.heightPixels

                            // TODO(Condition pour vérifier que la liste des readings est pas vide)
                            if (meterRepository.getMeterReadings(meterScreenModel.meter.meterID).collectAsState(initial = emptyList()).value.isNotEmpty()) {
                                ChartLineModel(meterRepository).createChartLine(
                                    meter = meterScreenModel.meter,
                                    height = screenHeight / 10,
                                    width = screenWidth
                                )
                            } else { Text(text = "Need at least one reading of this meter to have a graph.") }
                        }
                    }
                }
            }
            item{
                Text(text = "Meter Readings", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(8.dp))
            }
            items(meterScreenModel.state.value.readings) {reading->
                MeterReadingCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    onclick =
                    {
                     navigator.push(AddReadingScreen(meterScreenModel,meterScreenModel.meter.meterID,meterScreenModel.meter.meterName,reading.date,reading.value, true
                    ) { value, date, note ->
                        meterScreenModel.updateReading(reading.readingID, value, date, note)
                    })
                    },
                    value = reading.value,
                    date = reading.date,
                    note = "id: ${reading.readingID}",
                    onDeleteClick = {
                        scope.launch {
                            val result = snackbarHostState.showSnackbar(
                                message = "Do you really want to delete this reading?",
                                actionLabel = "yes",
                                withDismissAction = true
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                meterScreenModel.deleteReading(reading.readingID)
                            }
                        }
                    })
            }
        }
    }
}