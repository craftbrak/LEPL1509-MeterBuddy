package ucl.student.meterbuddy.ui.screen

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import kotlinx.coroutines.launch
import ucl.student.meterbuddy.data.model.enums.MeterIcon
import ucl.student.meterbuddy.data.model.enums.MeterType
import ucl.student.meterbuddy.data.model.enums.Unit
import ucl.student.meterbuddy.data.model.entity.Meter
import ucl.student.meterbuddy.viewmodel.MainPageScreenModel

data class AddMeterFormScreen(val homePageScreenModel: MainPageScreenModel): Screen {

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
    @Composable
    override fun Content() {

        var meterName by remember { mutableStateOf("") }
        var selectedType by remember { mutableStateOf(MeterType.ELECTRICITY) }
        var selectedUnit by remember { mutableStateOf(Unit.KILO_WATT_HOUR) }
        var meterCost by remember { mutableStateOf("") }
        var isAdditive by remember { mutableStateOf(true) }
        var nameFieldRequired by remember { mutableStateOf(false) }
        var costFieldRequired by remember { mutableStateOf(false) }
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        val navigator = LocalNavigator.current

        val meterTypes =MeterType.entries

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost ={ SnackbarHost(hostState = snackbarHostState) } ,
            topBar = {
                TopAppBar(
                    title = {
                    Text(text = "Add Reading")
                        },
                    colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                    navigationIcon = {
                    IconButton(onClick = {navigator?.pop()}) {
                        Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = "Back" )
                    }
                }, actions = {

                    }
                    )
            },

        ) {
            Column(
                modifier = Modifier
                    .padding(it)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                TextField(
                    value = meterName,
                    onValueChange = { meterName = it },
                    label = { Text("Meter Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Meter Type")
                FlowRow(Modifier.padding(10.dp),Arrangement.SpaceEvenly) {
                    meterTypes.forEach { type ->
                        Column(
                            modifier = Modifier
                                .clickable { selectedType = type }
                                .padding(horizontal = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            IconToggleButton(modifier = Modifier.padding(3.dp),checked = selectedType == type , onCheckedChange ={selectedType =type}) {
                                Icon(imageVector =ImageVector.vectorResource(id =type.icon.icon ) , contentDescription = type.icon.iconName)
                            }
                            Text(
                                    text = type.type,
                            modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Unit")
                FlowColumn(
                    Modifier
                        .padding(4.dp)
                        .height(100.dp),Arrangement.SpaceBetween) {
                    selectedType.units.forEach { unit ->
                        Row(
                            modifier = Modifier
                                .clickable { selectedUnit = unit }
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (unit == selectedUnit),
                                onClick = null // RadioButton handle selection automatically
                            )
                            Text(
                                text = unit.symbol,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = meterCost,
                    onValueChange = { meterCost = it.takeIf { it.isNotEmpty() } ?: "0.0" },
                    label = { Text("Cost Per Unit") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Consumption", modifier = Modifier.weight(1f))
                    Switch(
                        checked = isAdditive,
                        onCheckedChange = { isAdditive = it },
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FilledTonalButton(
                        onClick = {
                            if (meterName.isNotBlank() && meterCost.isNotBlank()) {
                                val newMeter = Meter(
                                    meterID = 0,
                                    meterName = meterName,
                                    meterUnit = selectedUnit,
                                    meterIcon = MeterIcon.Electricity,
                                    meterType = selectedType,
                                    housingID = 0,
                                    meterCost = meterCost.toDoubleOrNull() ?: 0.0,
                                    additiveMeter = isAdditive
                                )
                                navigator?.pop()
                                homePageScreenModel.addMeter(newMeter)
                            } else {
                                if (!meterName.isNotBlank()) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = "Field 'Name' is required",
                                            withDismissAction = true,

                                            )
                                    }
                                }
                                if (!meterCost.isNotBlank()) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = "Field 'Cost Per Unit' is required",
                                            withDismissAction = true
                                        )
                                    }
                                }
                            }
                        }
                    ) {
                        Icon(imageVector = Icons.Filled.Check, contentDescription ="add" )
                        Text(text = "Confirm")
                    }
                }
            }
        }

    }
}