package ucl.student.meterbuddy.ui.pages

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ucl.student.meterbuddy.ui.component.MeterOverviewCard

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(colors = topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ), title = { Text("Meter Menu") })
    }) {
        innerPadding -> 
        LazyColumn(modifier = Modifier.fillMaxWidth().padding(innerPadding)) {
            item {
                MeterOverviewCard(
                    modifier = Modifier.padding(20.dp),
                    meterName = "Electricity",
                    meterIcon = Icons.Outlined.ThumbUp,
                    lastReading = 100.0f,
                    readingUnit = "kWh",
                    tendanceIcon = "up",
                    tendenceValue = 10.0f,
                    monthlyCost = 20.0f,
                    currencySymbol = "£"
                )
            }
            item {
                MeterOverviewCard(
                    modifier = Modifier.padding(20.dp),
                    meterName = "Electricity",
                    meterIcon = Icons.Outlined.ThumbUp,
                    lastReading = 100.0f,
                    readingUnit = "kWh",
                    tendanceIcon = "up",
                    tendenceValue = 10.0f,
                    monthlyCost = 20.0f,
                    currencySymbol = "£"
                )
            }
            item {
                MeterOverviewCard(
                    modifier = Modifier.padding(20.dp),
                    meterName = "Electricity",
                    meterIcon = Icons.Outlined.ThumbUp,
                    lastReading = 100.0f,
                    readingUnit = "kWh",
                    tendanceIcon = "up",
                    tendenceValue = 10.0f,
                    monthlyCost = 20.0f,
                    currencySymbol = "£"
                )
            }
            item {
                MeterOverviewCard(
                    modifier = Modifier.padding(20.dp),
                    meterName = "Electricity",
                    meterIcon = Icons.Outlined.ThumbUp,
                    lastReading = 100.0f,
                    readingUnit = "kWh",
                    tendanceIcon = "up",
                    tendenceValue = 10.0f,
                    monthlyCost = 20.0f,
                    currencySymbol = "£"
                )
            }
            item {
                MeterOverviewCard(
                    modifier = Modifier.padding(20.dp),
                    meterName = "Electricity",
                    meterIcon = Icons.Outlined.ThumbUp,
                    lastReading = 100.0f,
                    readingUnit = "kWh",
                    tendanceIcon = "up",
                    tendenceValue = 10.0f,
                    monthlyCost = 20.0f,
                    currencySymbol = "£"
                )
            }
        }
    }
}