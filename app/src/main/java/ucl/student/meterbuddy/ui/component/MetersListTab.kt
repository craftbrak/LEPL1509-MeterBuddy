package ucl.student.meterbuddy.ui.component

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import cafe.adriel.voyager.transitions.FadeTransition
import cafe.adriel.voyager.transitions.ScaleTransition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ucl.student.meterbuddy.R
import ucl.student.meterbuddy.data.model.entity.Meter
import ucl.student.meterbuddy.ui.screen.HomeScreen
import ucl.student.meterbuddy.ui.screen.MeterDetailsScreen
import ucl.student.meterbuddy.viewmodel.MainPageScreenModel

object MetersListTab: Tab {
        override val options: TabOptions
            @Composable
            get() {
                val title = stringResource(R.string.home_tab)
                val icon = rememberVectorPainter(Icons.Default.Home)

                return remember {
                    TabOptions(
                        index = 0u,
                        title = title,
                        icon = icon
                    )
                }
            }

        @Composable
        override fun Content() {

            Navigator(screen = MeterList(MainPageScreenModel(context = LocalContext.current))){
                FadeTransition(navigator = it)
            }



        }

}
data class MeterList(val mainPageScreenModel: MainPageScreenModel): Screen {
    @Composable
    override fun Content() {
        val showMeterFormDialog = remember{ mutableStateOf(false)}
        val snackbarHostState = remember {
            SnackbarHostState()
        }
        val context= LocalContext.current
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(onClick = { showMeterFormDialog.value = true }) {
                    Icon(imageVector = Icons.Outlined.Add, contentDescription = "add Meter")
                    Text("Add Meter")
                }
            },
            topBar = {TopBar() },
            bottomBar = { BottomAppBar {}  }
        ) {
            val navigator = LocalNavigator.current
            val showBottomSheet = remember { mutableStateOf(false) }
            val showDeleteDialog = remember { mutableStateOf(false) }
            var selectedMeter: Meter? = null
            val scope = rememberCoroutineScope()
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(it)
            ) {
                items(mainPageScreenModel.state.value.meters) { meter ->
                    val lastReading =
                        mainPageScreenModel.state.value.lastReading[meter.meterID]?.lastOrNull()?.value
                    MeterOverviewCard(
                        onClick = { navigator?.push(MeterDetailsScreen(meter)) },
                        onLongClick = {
                            showBottomSheet.value = true
                            selectedMeter = meter
                        },
                        modifier = Modifier.padding(10.dp),
                        meterName = meter.meterName,
                        meterIcon = meter.meterIcon,
                        lastReading = lastReading?.toString(),
                        readingUnit = meter.meterUnit.unit,
                        trendIcon = "up",
                        trendValue = 10.0f,
                        monthlyCost = 20.0f,
                        currencySymbol = "£"
                    )
                }
            }

            MeterFormDialog(
                onDismissRequest = { showMeterFormDialog.value = false },
                onConfirmation = { name, unit, icon, type, cost, additive ->
                    if(name.isNotBlank() && cost.isNotBlank()) {
                        val numberCost = cost.toDoubleOrNull()
                        if (numberCost == null) {
                            Toast.makeText(context, "Cost must be a number", Toast.LENGTH_SHORT).show()
                            return@MeterFormDialog
                        }
                        if (numberCost < 0) {
                            Toast.makeText(context, "Cost cannot be negative, if you want to track a production please toggle the consumption off", Toast.LENGTH_LONG)
                                .show()
                            return@MeterFormDialog
                        }
                        val newMeter = Meter(
                            meterID = 0,
                            meterName = name,
                            meterIcon = icon,
                            meterUnit = unit,
                            meterType = type,
                            housingID = 0,
                            meterCost = cost.toDouble(),
                            additiveMeter = additive
                        )
                        mainPageScreenModel.addMeter(newMeter)
                        showMeterFormDialog.value = false
                    } else {
                        if(name.isBlank()) {
                            Toast.makeText(context, "Name cannot be empty", Toast.LENGTH_SHORT).show()
                        }
                        if(cost.isBlank()) {
                            Toast.makeText(context, "Cost cannot be empty", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                },
                showDialog = showMeterFormDialog.value
            )
            if (showDeleteDialog.value) {
                AlertDialog(onDismissRequest = { showDeleteDialog.value = false },
                    confirmButton = {
                        Button(onClick = {
                            showDeleteDialog.value = false
                            mainPageScreenModel.deleteMeter(selectedMeter!!)
                        }) {
                            Text("Delete")
                        }
                    },
                    text = {
                        Text(
                            text = stringResource(id = R.string.delete_meter_validation_text),
                        )
                    },
                    title = { Text(text = stringResource(id = R.string.delete_meter_validation_title)) },
                    icon = {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                    },
                    dismissButton = {
                        Button(onClick = {
                            showDeleteDialog.value = false
                        }) {
                            Text("Cancel")
                        }
                    }
                )
            }
            BottomSheet(
                showBottomSheet = showBottomSheet.value,
                onDismissRequest = { showBottomSheet.value = false },
                onEditClick = {
                    scope.launch {
                        snackbarHostState.showSnackbar("Edit clicked")
                        //Todo: Open Edit Meter Screen Dialog
                    }
                    showBottomSheet.value = false
                },
                onDeleteClick = {
                    showBottomSheet.value = false
                    showDeleteDialog.value = true
                },
            )
        }
    }
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun TopBar() {
        CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
            title = { Text(stringResource(id = R.string.meter_menu)) },
            actions = {

            }
        )
    }
}