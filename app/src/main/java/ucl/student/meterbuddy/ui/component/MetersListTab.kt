package ucl.student.meterbuddy.ui.component

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import cafe.adriel.voyager.transitions.FadeTransition
import ucl.student.meterbuddy.R
import ucl.student.meterbuddy.data.model.entity.Housing
import ucl.student.meterbuddy.data.model.entity.Meter
import ucl.student.meterbuddy.data.model.enums.TrendIcon
import ucl.student.meterbuddy.data.utils.DataException
import ucl.student.meterbuddy.data.utils.Resource
import ucl.student.meterbuddy.ui.screen.MeterDetailsScreen
import ucl.student.meterbuddy.viewmodel.MainPageScreenModel
import java.util.Optional

object MetersListTab : Tab {
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

        Navigator(screen = MeterList()) {
            FadeTransition(navigator = it)
        }


    }

}

class MeterList : Screen {
    @Composable
    override fun Content() {
        val context = LocalContext.current
        val showMeterFormDialog = remember { mutableStateOf(false) }
        val showEditFormDialog = remember { mutableStateOf(false) }
        val snackbarHostState = remember {
            SnackbarHostState()
        }
        val mainPageScreenModel: MainPageScreenModel = getViewModel()
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
            topBar = {
                TopBar(
                    onDisconnectClick = { mainPageScreenModel.logout() },
                    onHoushingSelect = { housing -> mainPageScreenModel.selectHousing(housing) },
                    Homes = mainPageScreenModel.state.value.housings,
                    selectedHome = mainPageScreenModel.state.value.selectedHousing
                )
            },
            bottomBar = { BottomAppBar {} }
        ) {
            val navigator = LocalNavigator.current
            val showBottomSheet = remember { mutableStateOf(false) }
            val showDeleteDialog = remember { mutableStateOf(false) }
            val selectedMeter: MutableState<Optional<Meter>> =
                remember { mutableStateOf(Optional.empty()) }
            // val scope = rememberCoroutineScope()
                if (mainPageScreenModel.state.value.meters.isNotEmpty()){
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(it)
                    ) {
                        items(mainPageScreenModel.state.value.meters) { meter ->
                            val readings =
                                mainPageScreenModel.state.value.lastReading[meter.meterID]
                            // val recentReadingValue = readings?.lastOrNull()?.value

                            // val trendValue: Float = if ((readings?.size ?: 0) < 2) { 0.0f }
                            // else {
                            //     val oldReadingValue = readings?.get(readings.size - 2)?.value
                            //  100 * ((recentReadingValue!! / oldReadingValue!!) - 1) // In percent
                            // }

//                    Log.d("TREND", "$recentReadingValue")

                            val recentReadingValue = readings?.firstOrNull()?.value

                            val trendValue: Float = if ((readings?.size ?: 0) < 2) {
                                0.0f
                            } else {
                                val oldReadingValue = readings?.get(1)?.value
                                100 * ((recentReadingValue!! / oldReadingValue!!) - 1) // In percent
                            }

                            val trendIcon: TrendIcon = if (trendValue == 0.0f) {
                                TrendIcon.Flat
                            } else if (trendValue > 0.0f) {
                                TrendIcon.Up
                            } else {
                                TrendIcon.Down
                            }

                            MeterOverviewCard(
                                onClick = { navigator?.push(MeterDetailsScreen(meter)) },
                                onLongClick = {
                                    showBottomSheet.value = true
                                    selectedMeter.value = Optional.of(meter)
                                },
                                modifier = Modifier.padding(10.dp),
                                meterName = meter.meterName,
                                meterIcon = meter.meterIcon,
                                lastReading = recentReadingValue.toString(),
                                readingUnit = meter.meterUnit.unit,
                                trendIcon = trendIcon,
                                trendValue = trendValue,
                                monthlyCost = 20.0f,
                                currencySymbol = "£" //TODO: USE values instead of hardcoded values
                            )
                        }
                    }
                }
                else{
                    Text(text = "Please get started by adding a meter")
                }

            MeterFormDialog(
                onDismissRequest = { showMeterFormDialog.value = false },
                onConfirmation = { name, unit, icon, type, cost, additive ->
                    if (name.isNotBlank() && cost.isNotBlank()) {
                        val numberCost = cost.toDoubleOrNull()
                        if (numberCost == null) {
                            Toast.makeText(context, "Cost must be a number", Toast.LENGTH_SHORT)
                                .show()
                            return@MeterFormDialog
                        }
                        if (numberCost < 0) {
                            Toast.makeText(
                                context,
                                "Cost cannot be negative, if you want to track a production please toggle the consumption off",
                                Toast.LENGTH_LONG
                            )
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
                        if (name.isBlank()) {
                            Toast.makeText(context, "Name cannot be empty", Toast.LENGTH_SHORT)
                                .show()
                        }
                        if (cost.isBlank()) {
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
                            if (selectedMeter.value.isPresent) {
                                mainPageScreenModel.deleteMeter(selectedMeter.value.get())
                            } else {
                                Toast.makeText(
                                    context,
                                    "Error in delete prosses",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
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

            if (selectedMeter.value.isPresent) {
                MeterFormDialog(
                    onDismissRequest = {
                        selectedMeter.value = Optional.empty()
                        showEditFormDialog.value = false
                    },
                    onConfirmation = { name, unit, icon, type, cost, additive ->
                        if (name.isNotBlank() && cost.isNotBlank()) {
                            val numberCost = cost.toDoubleOrNull()
                            if (numberCost == null) {
                                Toast.makeText(context, "Cost must be a number", Toast.LENGTH_SHORT)
                                    .show()
                                return@MeterFormDialog
                            }
                            if (numberCost < 0) {
                                Toast.makeText(
                                    context,
                                    "Cost cannot be negative, if you want to track a production please toggle the consumption off",
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                                return@MeterFormDialog
                            }
                            val newMeter = Meter(
                                meterID = selectedMeter.value.get().meterID,
                                meterName = name,
                                meterIcon = icon,
                                meterUnit = unit,
                                meterType = type,
                                housingID = 0,
                                meterCost = cost.toDouble(),
                                additiveMeter = additive
                            )
                            mainPageScreenModel.updateMeter(newMeter)
                            selectedMeter.value = Optional.empty()
                            showEditFormDialog.value = false
                        } else {
                            if (name.isBlank()) {
                                Toast.makeText(context, "Name cannot be empty", Toast.LENGTH_SHORT)
                                    .show()
                            }
                            if (cost.isBlank()) {
                                Toast.makeText(context, "Cost cannot be empty", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    },
                    showDialog = showEditFormDialog.value,
                    lastMeterName = selectedMeter.value.get().meterName,
                    lastMeterCost = selectedMeter.value.get().meterCost.toString(),
                    lastMeterType = selectedMeter.value.get().meterType,
                    lastMeterUnit = selectedMeter.value.get().meterUnit,
                    lastIsAdditive = selectedMeter.value.get().additiveMeter,
                    edit = true
                )
            }

            BottomSheet(
                showBottomSheet = showBottomSheet.value,
                onDismissRequest = { showBottomSheet.value = false },
                onEditClick = {
                    showBottomSheet.value = false
                    showEditFormDialog.value = true
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
    private fun TopBar(
        onDisconnectClick: () -> Unit,
        onHoushingSelect: (housing: Housing) -> Unit,
        Homes: List<Housing>,
        selectedHome: Resource<Housing, DataException>
    ) {
        var expended by remember {
            mutableStateOf(false)
        }
        CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
            navigationIcon = {
                Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
                    when (selectedHome) {
                        is Resource.Error -> {
                            Text(text = stringResource(R.string.no_housings))
                        }

                        is Resource.Loading -> {
                            Text(text = "Loading...")
                        }

                        is Resource.Success -> {
                            Button(
                                onClick = { expended = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            ) {
                                Icon(
                                    Icons.Outlined.Home,
                                    contentDescription = stringResource(R.string.home_selection)
                                )
                                Spacer(modifier = Modifier.size(5.dp))
                                Text(text = selectedHome.data.housingName)
                                Icon(
                                    Icons.Filled.ArrowDropDown,
                                    contentDescription = stringResource(R.string.home_selection)
                                )
                            }

                            DropdownMenu(
                                expanded = expended,
                                onDismissRequest = { expended = false },
                                modifier = Modifier.padding(20.dp)
                            ) {
                                Homes.forEach { housing ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(
                                                horizontalArrangement = Arrangement.Center,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = ImageVector.vectorResource(
                                                        housing.housingType.icon
                                                    ), contentDescription = housing.housingType.type
                                                )
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Text(text = housing.housingName)
                                            }
                                        },
                                        onClick = { onHoushingSelect(housing)})
                                }
                                DropdownMenuItem(text = {
                                    Row(
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Filled.Add,
                                            contentDescription = stringResource(R.string.add_housing)
                                        )
                                        Spacer(modifier = Modifier.size(5.dp))
                                        Text(text = stringResource(R.string.add_housing))
                                    }
                                }, onClick = { /*TODO: Open Add houising page/popup */ })
                            }
                        }
                    }

                }
            },
            title = { Text(stringResource(id = R.string.meter_menu)) },
            actions = {
                IconButton(onClick = { onDisconnectClick() }) {
                    Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "Logout")
                }
            }
        )
    }
}