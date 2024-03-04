package ucl.student.meterbuddy.ui.pages

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import ucl.student.meterbuddy.data.model.entity.Meter
import ucl.student.meterbuddy.data.model.enums.MeterIcon
import ucl.student.meterbuddy.data.model.enums.MeterType
import ucl.student.meterbuddy.data.model.enums.Unit
import ucl.student.meterbuddy.ui.component.MeterOverviewCard
import ucl.student.meterbuddy.ui.screen.MeterDetailsScreen
import ucl.student.meterbuddy.viewmodel.MainPageScreenModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage(mainPageScreenModel: MainPageScreenModel) {

    val navigator = LocalNavigator.currentOrThrow

    // TEST BEGIN : MainPageScreenModel
    val meters = mainPageScreenModel.listAllMeters()
    // TEST END : MainPageScreenModel

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
            items(meters) { meter ->
                MeterOverviewCard(
                    onClick = {
                        navigator.push(MeterDetailsScreen(meter))
                    },
                    modifier = Modifier.padding(20.dp),
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