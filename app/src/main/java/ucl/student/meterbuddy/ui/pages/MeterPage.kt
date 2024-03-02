package ucl.student.meterbuddy.ui.pages

import android.annotation.SuppressLint
import android.graphics.drawable.Icon
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.launch
import ucl.student.meterbuddy.data.model.entity.Meter
import ucl.student.meterbuddy.ui.component.MeterOverviewCard
import ucl.student.meterbuddy.ui.component.MeterReadingCard
import ucl.student.meterbuddy.ui.screen.AddReadingScreen
import java.time.Instant
import java.util.Date

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeterPage(title: String, meter: Meter ) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val navigator = LocalNavigator.currentOrThrow

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                title = { Text(text = title, style = MaterialTheme.typography.headlineLarge) },
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
                IconButton(onClick = { navigator.push(AddReadingScreen(meter.meterID,meter.meterName)) }, modifier = Modifier) {
                    Icon(imageVector = Icons.Outlined.Lock, contentDescription = "Sort")
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(innerPadding)){
            item{
                Card(modifier = Modifier.fillMaxWidth().padding(8.dp).fillMaxHeight(.4f)) {
                    Text(text = "Graphs are not implemented yet but will be here soon", style = MaterialTheme.typography.headlineLarge)
                }
            }
            item{
                Text(text = "Meter Readings", style = MaterialTheme.typography.headlineMedium)
            }
            items(80) {
                MeterReadingCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    onclick = { /*TODO*/ },
                    value = 123.0,
                    date = Date.from(Instant.now()),
                    note = "Test",
                    onEditClick = { /*TODO*/ }
                )
            }
        }
    }


}