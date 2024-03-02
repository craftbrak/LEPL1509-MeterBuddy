package ucl.student.meterbuddy.ui.pages

import android.annotation.SuppressLint
import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.launch
import ucl.student.meterbuddy.data.model.entity.Meter
import ucl.student.meterbuddy.ui.component.MeterOverviewCard

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeterPage(title: String, meter: Meter ) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val navigator = LocalNavigator.currentOrThrow

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                title = { Text(text = title, style = MaterialTheme.typography.headlineLarge) },
                navigationIcon = {
                    IconButton(onClick = {navigator.pop()}) {
                        Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = "Sort")
                    }
                },
                actions = {
//                    IconButton(onClick = {}) {
//                        Icon(imageVector = Icons.Outlined.Delete, contentDescription = "Sort")
//                    }
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
                IconButton(onClick = { /*TODO*/ }, modifier = Modifier) {
                    Icon(imageVector = Icons.Outlined.Lock, contentDescription = "Sort")
                }
            }
        }
    ) { innerpading ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(innerpading) ){
            MeterOverviewCard(
                meterName = meter.meterName,
                meterIcon = meter.meterIcon,
                lastReading = 123.0f,
                readingUnit = "kWh",
                tendanceIcon = "up",
                tendenceValue = 12.0f,
                monthlyCost = 12.0f,
                currencySymbol = "€"
            )
            Text(text = "Hello World")
            Text(text = "Hello World")
            Text(text = "Hello World")
            Text(text = "Hello World")
        }
    }
}