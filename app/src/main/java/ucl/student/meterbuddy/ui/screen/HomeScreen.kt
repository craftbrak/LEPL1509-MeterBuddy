package ucl.student.meterbuddy.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import ucl.student.meterbuddy.viewmodel.MainPageScreenModel
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ucl.student.meterbuddy.R
import ucl.student.meterbuddy.data.model.entity.Meter
import ucl.student.meterbuddy.ui.component.MeterFormDialog
import ucl.student.meterbuddy.ui.component.MeterOverviewCard

object HomeScreen : Screen {

    lateinit var mainPageScreenModel: MainPageScreenModel

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val context = LocalContext.current
        mainPageScreenModel = rememberScreenModel { MainPageScreenModel(context) }
        val scope = rememberCoroutineScope()
        val snackbarHostState = remember {
            SnackbarHostState()
        }
        val navigator = LocalNavigator.current
        val showBottomSheet = remember { mutableStateOf(false) }
        val showMeterFormDialog = remember { mutableStateOf(false) }
        val showDeleteDialog = remember { mutableStateOf(false) }
        var selectedMeter: Meter? = null
        Scaffold(
            modifier = Modifier.fillMaxWidth(),
            topBar = { TopBar() },
            floatingActionButton = {
                ExtendedFloatingActionButton(onClick = { showMeterFormDialog.value = true }) {
                    Icon(imageVector = Icons.Outlined.Add, contentDescription = "add Meter")
                    Text("Add Meter")
                }
            },
            bottomBar = { BottomTabBar() },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding)
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
    private fun BottomSheet(
        showBottomSheet: Boolean,
        onDismissRequest: () -> Unit,
        onEditClick: () -> Unit,
        onDeleteClick: () -> Unit
    ) {
        if (showBottomSheet)
            ModalBottomSheet(onDismissRequest = onDismissRequest) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(25.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = {
                        onEditClick()
                    }) {
                        Text("Edit")
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
                    }
                    Button(onClick = {
                        onDeleteClick()
                    }) {
                        Text("Delete")
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                    }

                }
                Spacer(modifier = Modifier.heightIn(30.dp))
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

    @Composable
    private fun SwiperToLeft(navigator: Navigator?, scope: CoroutineScope): Modifier {
        return Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures { _, delta ->
                    if (delta.y > 0) {
                        scope.launch {
                            navigator?.push(LineChartsScreen(mainPageScreenModel))
                        }
                    }
                }
            }
    }

    @Composable
    fun BottomTabBar() {
        BottomAppBar(modifier = Modifier.fillMaxWidth()) {
            val navigator = LocalNavigator.current
            val currentScreen = navigator?.lastItem
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                TabButtons(
                    onClick = {
                        if (currentScreen?.javaClass != HomeScreen::class.java)
                            navigator?.pop()
                    },
                    icon = Icons.Default.Home,
                    contentDescription = "Home",
                    currentScreen?.javaClass == HomeScreen::class.java
                )
                TabButtons(
                    onClick = {
                        if (currentScreen != LineChartsScreen(mainPageScreenModel))
                            navigator?.push(LineChartsScreen(mainPageScreenModel))
                    },
                    icon = Icons.Default.ThumbUp,
                    contentDescription = "Stats",
                    currentScreen?.javaClass == LineChartsScreen::class.java
                )

            }
        }
    }

    @Composable
    fun TabButtons(
        onClick: () -> Unit,
        icon: ImageVector,
        contentDescription: String,
        selected: Boolean = false
    ) {
        val color =
            if (selected) MaterialTheme.colorScheme.inversePrimary else MaterialTheme.colorScheme.onSurface
        Column(
            modifier = Modifier
                .clickable { onClick() }
                .padding(horizontal = 10.dp, vertical = 5.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = contentDescription, tint = color)
            Text(
                text = contentDescription,
                style = MaterialTheme.typography.bodySmall,
                color = color
            )
        }
    }
}