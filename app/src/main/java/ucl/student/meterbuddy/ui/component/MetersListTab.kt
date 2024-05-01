package ucl.student.meterbuddy.ui.component

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Add
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import cafe.adriel.voyager.transitions.FadeTransition
import co.yml.charts.common.extensions.isNotNull
import ucl.student.meterbuddy.R
import ucl.student.meterbuddy.data.model.entity.Housing
import ucl.student.meterbuddy.data.model.entity.Meter
import ucl.student.meterbuddy.data.utils.DataException
import ucl.student.meterbuddy.data.utils.Resource
import ucl.student.meterbuddy.ui.screen.MeterDetailsScreen
import ucl.student.meterbuddy.viewmodel.MainPageScreenModel
import java.util.Optional
import kotlin.math.abs

object MetersListTab : Tab {
    private fun readResolve(): Any = MetersListTab
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
    private fun AdderButton(mainPageScreenModel: MainPageScreenModel, showMeterFormDialog:  MutableState<Boolean>) {
        if (mainPageScreenModel.state.value.meters.isNotEmpty()) {
            ExtendedFloatingActionButton(onClick = { showMeterFormDialog.value = true }) {
                Icon(imageVector = Icons.Outlined.Add, contentDescription = "add Meter")
                Text("Add Meter")
            }
        }
    }

    @Composable
    override fun Content() {
        val context = LocalContext.current
        val showMeterFormDialog = remember { mutableStateOf(false) }
        val showEditFormDialog = remember { mutableStateOf(false) }
        val snackbarHostState = remember {
            SnackbarHostState()
        }
        val mainPageScreenModel: MainPageScreenModel = getViewModel()
        val navigator = LocalNavigator.current
        val showBottomSheet = remember { mutableStateOf(false) }
        val showDeleteDialog = remember { mutableStateOf(false) }
        val showDeleteHouseDialog = remember { mutableStateOf(false)}
        val selectedMeter: MutableState<Optional<Meter>> =
            remember { mutableStateOf(Optional.empty()) }
        val editedHousing: MutableState<Housing?> = remember { mutableStateOf(null)}
        val showHousingDialog = remember{ mutableStateOf(false)}
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            floatingActionButton = { AdderButton(mainPageScreenModel, showMeterFormDialog) },
            topBar = {
                TopBar(
                    onDisconnectClick = { mainPageScreenModel.logout() },
                    onHoushingSelect = { housing -> mainPageScreenModel.selectHousing(housing) },
                    homes = mainPageScreenModel.state.value.housings,
                    selectedHome = mainPageScreenModel.state.value.selectedHousing,
                    onAddHomeClick = { showHousingDialog.value = true },
                    onUpdateClick = {
                        showHousingDialog.value = true
                        editedHousing.value = when(mainPageScreenModel.state.value.selectedHousing) {
                            is Resource.Error,is Resource.Loading -> null
                            is Resource.Success -> (mainPageScreenModel.state.value.selectedHousing as Resource.Success<Housing, DataException>).data
                        }
                    }
                )
            },
            bottomBar = { BottomAppBar {} }
        ) {
            if (mainPageScreenModel.state.value.housings.isEmpty()) {
                AddFirstHousingIndicator(
                    it,
                    showMeterFormDialog,
                    showHousingDialog,
                    editedHousing,
                    mainPageScreenModel
                )
            } else {

                // val scope = rememberCoroutineScope()
                if (mainPageScreenModel.state.value.meters.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(it)
                    ) {
                        items(mainPageScreenModel.state.value.meters) { meter ->
                            val readings =
                                mainPageScreenModel.state.value.lastReading[meter.meterID]
                            val recentReadingValue = readings?.firstOrNull()?.value

                            val costTrend: Double
                            val trendValue: Float
                            val costIcon: Int

                            if ((readings?.size ?: 0) < 2) {
                                costTrend = 0.0
                                trendValue = 0.0f
                                if (meter.additiveMeter) {
                                    costIcon = R.drawable.non_additive_icon_24
                                } else {
                                    costIcon = R.drawable.additive_icon
                                }
                            } else {
                                val oldReadingValue = readings?.get(1)?.value
                                trendValue = 100 * ((recentReadingValue!! / oldReadingValue!!) - 1)
                                if ((recentReadingValue - oldReadingValue) > 0.0) {
                                    if (meter.additiveMeter) {
                                        costIcon = R.drawable.non_additive_icon_24
                                    } else {
                                        costIcon = R.drawable.additive_icon
                                    }
                                } else {
                                    if (meter.additiveMeter) {
                                        costIcon = R.drawable.additive_icon
                                    } else {
                                        costIcon = R.drawable.non_additive_icon_24
                                    }
                                }
                                costTrend = abs((recentReadingValue - oldReadingValue)) * meter.meterCost
                            }

                            val trendIcon: Int
                            if (meter.additiveMeter) {
                                trendIcon = if (trendValue == 0.0f) {
                                    R.drawable.flat_icon
                                } else if (trendValue > 0.0f) {
                                    R.drawable.trend_up_red_icon
                                } else {
                                    R.drawable.trend_down_green_icon
                                }
                            } else {
                                trendIcon = if (trendValue == 0.0f) {
                                    R.drawable.flat_icon
                                } else if (trendValue > 0.0f) {
                                    R.drawable.trend_up_green_icon
                                } else {
                                    R.drawable.trend_down_red_icon
                                }
                            }

                            mainPageScreenModel.state.value.currentUserData?.userCurrency?.symbol?.let { it1 ->
                                MeterOverviewCard(
                                    onClick = { navigator?.push(MeterDetailsScreen(meter)) },
                                    onLongClick = {
                                        showBottomSheet.value = true
                                        selectedMeter.value = Optional.of(meter)
                                    },
                                    modifier = Modifier.padding(10.dp),
                                    meterName = meter.meterName,
                                    meterIcon = meter.meterIcon,
                                    lastReading = if (recentReadingValue.isNotNull()) {
                                        recentReadingValue.toString()
                                    } else { null },
                                    readingUnit = meter.meterUnit.unit,
                                    trendIcon = trendIcon,
                                    trendValue = trendValue,
                                    monthlyCost = costTrend,
                                    monthlyCostIcon = costIcon,
                                    currencySymbol = it1,
                                    consumption = meter.additiveMeter
                                )
                            }
                        }
                    }
                } else {
                    AddFirstMeterIndicator(
                        it,
                        showMeterFormDialog,
                        showHousingDialog,
                        editedHousing,
                        mainPageScreenModel
                    )
                }

                HousingDialog(enabled = showHousingDialog.value,
                    onSubmit = { housing ->
                        mainPageScreenModel.saveHousing(housing)
                        showHousingDialog.value = false
                    },
                    onDismissRequest = {
                        showHousingDialog.value = false; editedHousing.value = null
                    },
                    initialData = editedHousing.value,
                    users = mainPageScreenModel.state.value.users,
                    usersOfHousing = editedHousing.value?.let { it1 ->
                        mainPageScreenModel.state.value.housingUsers
                    } ?: emptyList(),
                    onUserDelete = { user ->
                        if (mainPageScreenModel.state.value.housingUsers.size > 1) {
                            mainPageScreenModel.deleteUserFromHousing(
                                user,
                                editedHousing.value!!
                            )
                        } else {
                            //popup de dialogue pour supprimer la maison
                            showDeleteHouseDialog.value = true
                        }
                    },
                    onUserAdd = { user ->
                        Log.wtf("HousingDialog", user.userName)
                        if (user !in mainPageScreenModel.state.value.housingUsers) {
                            mainPageScreenModel.addUserToHousing(
                                user,
                                editedHousing.value!!
                                //TODO: fix crash when add user on new home
                            )
                        }
                    }
                )
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

                if(showDeleteHouseDialog.value) {
                    AlertDialog(onDismissRequest = { showDeleteHouseDialog.value = false },
                        confirmButton = {
                            Button(onClick = {
                                showDeleteHouseDialog.value = false
                                showHousingDialog.value = false
                                if (mainPageScreenModel.state.value.housingUsers.size == 1) {
                                    editedHousing.value?.let { it1 ->
                                        mainPageScreenModel.deleteHousing(
                                            it1, mainPageScreenModel.state.value.housingUsers.get(0))
                                    }
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
                                text = stringResource(id = R.string.delete_house_validation_text),
                            )
                        },
                        title = { Text(text = stringResource(id = R.string.delete_meter_validation_title)) },
                        icon = {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                        },
                        dismissButton = {
                            Button(onClick = {
                                showDeleteHouseDialog.value = false
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
                                    Toast.makeText(
                                        context,
                                        "Cost must be a number",
                                        Toast.LENGTH_SHORT
                                    )
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
                                    Toast.makeText(
                                        context,
                                        "Name cannot be empty",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                                if (cost.isBlank()) {
                                    Toast.makeText(
                                        context,
                                        "Cost cannot be empty",
                                        Toast.LENGTH_SHORT
                                    )
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
    }


    @Composable
    private fun AddFirstHousingIndicator(it: PaddingValues, showMeterFormDialog: MutableState<Boolean>, showHousingDialog: MutableState<Boolean>, editedHousing:  MutableState<Housing?>, mainPageScreenModel: MainPageScreenModel) {
        Column (
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Welcome to MeterBuddy!",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = "Start your journey to effortless meter management.\nAdd your first house with just a tap and begin tracking your energy usage like a pro.\nLet's make managing meters a breeze together!",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable(onClick = { showMeterFormDialog.value = true })
            ) {
                ExtendedFloatingActionButton(onClick = { showMeterFormDialog.value = true }) {
                    Icon(imageVector = Icons.Outlined.Add, contentDescription = "add first House")
                    Text("Add your first House just here!")
                }
            }
        }
    }

    @Composable
    private fun AddFirstMeterIndicator(it: PaddingValues, showMeterFormDialog: MutableState<Boolean>, showHousingDialog: MutableState<Boolean>, editedHousing:  MutableState<Housing?>, mainPageScreenModel: MainPageScreenModel) {
        Column (
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Welcome to your new home!",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = "Here, you can create your own meters, add readings, view trends graphically, and much more!",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable(onClick = { showMeterFormDialog.value = true })
            ) {
                ExtendedFloatingActionButton(onClick = { showMeterFormDialog.value = true }) {
                    Icon(imageVector = Icons.Outlined.Add, contentDescription = "add first Meter")
                    Text("Add your first Meter here!")
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            Row(
                modifier = Modifier.clickable(onClick = {
                    showHousingDialog.value = true
                    editedHousing.value = when(mainPageScreenModel.state.value.selectedHousing) {
                        is Resource.Error -> null
                        is Resource.Loading -> null
                        is Resource.Success -> (mainPageScreenModel.state.value.selectedHousing as Resource.Success<Housing, DataException>).data
                    }
                }),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit your Home",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Edit your home here",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.clickable(onClick = { showHousingDialog.value = true }),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add a new home",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Create a new home here",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }


    @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
    @Composable
    private fun TopBar(
        onDisconnectClick: () -> Unit,
        onHoushingSelect: (housing: Housing) -> Unit,
        homes: List<Housing>,
        selectedHome: Resource<Housing, DataException>,
        onAddHomeClick: () -> Unit,
        onUpdateClick: () -> Unit
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
                IconButton(onClick = { onUpdateClick() }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Settings"
                    )
                }
            },
            title = { Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
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
                            ),
                            modifier = Modifier.combinedClickable(
                                onClick = {
                                    expended = true
                                },
                                onLongClick = {

                                },
                                onDoubleClick = {
                                    onUpdateClick()
                                },
                            )
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = selectedHome.data.housingType.icon),
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
                            modifier = Modifier.padding(20.dp),
                        ) {
                            homes.forEach { housing ->
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
                                    onClick = { onHoushingSelect(housing); expended = false })
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
                            }, onClick = {onAddHomeClick() })
                        }
                    }
                }

            } },
            actions = {
                IconButton(onClick = { onDisconnectClick() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Logout"
                    )
                }
            }
        )
    }
}