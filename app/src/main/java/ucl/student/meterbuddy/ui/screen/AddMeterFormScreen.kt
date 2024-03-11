package ucl.student.meterbuddy.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ucl.student.meterbuddy.data.model.enums.MeterType
import ucl.student.meterbuddy.data.model.enums.MeterUnit
import ucl.student.meterbuddy.data.model.entity.Meter
import ucl.student.meterbuddy.viewmodel.MainPageScreenModel
import kotlin.enums.EnumEntries

class AddMeterFormScreen: Screen {

    /**************
     Main function
     **************/

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    override fun Content() {

        var meterName by remember { mutableStateOf("") }
        var meterCost by remember { mutableStateOf("") }
        var selectedType by remember { mutableStateOf(MeterType.ELECTRICITY) }
        var selectedMeterUnit by remember { mutableStateOf(MeterUnit.KILO_WATT_HOUR) }
        var isAdditive by remember { mutableStateOf(true) }
        // var nameFieldRequired by remember { mutableStateOf(false) }
        // var costFieldRequired by remember { mutableStateOf(false) }

        val meterTypes = MeterType.entries
        // val meterUnits = Unit.entries

        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        val navigator = LocalNavigator.current
        val mainPageScreenModel = MainPageScreenModel(LocalContext.current)

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(snackbarHostState) } ,
            topBar = { TopBar(navigator) },

        ) {
            Column(
                modifier = Modifier
                    .padding(it)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                MeterTextField(meterName) { newName -> meterName = newName }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Meter Type")
                MeterTypeOptions(meterTypes, selectedType) { newType -> selectedType = newType }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Unit")
                MeterUnitOptions(selectedType, selectedMeterUnit) { newUnit -> selectedMeterUnit = newUnit }

                Spacer(modifier = Modifier.height(16.dp))

                MeterCostTextField(meterCost) { newCost -> meterCost = newCost}

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Consumption", modifier = Modifier.weight(1f))
                    SwitchButton(isAdditive) { newBoolean -> isAdditive = newBoolean}
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SubmitButton(meterName, meterCost, selectedMeterUnit, selectedType, isAdditive,
                        navigator, snackbarHostState, scope, mainPageScreenModel)
                }
            }
        }
    }

    /******************************
     All the Buttons and their logic
     ******************************/

    @Composable
    fun BackButton(navigator: Navigator?) {
        IconButton(onClick = { navigator?.pop() }) {
            Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = "Back")
        }
    }

    @Composable
    fun TypeSelectorButton(type: MeterType, selectedType: MeterType, onTypeSelected: (MeterType) -> kotlin.Unit) {
        IconToggleButton(modifier = Modifier.padding(3.dp),checked = selectedType == type, onCheckedChange = { onTypeSelected(type) }) {
            Icon(imageVector = ImageVector.vectorResource(id = type.icon.icon), contentDescription = type.icon.iconName)
        }
    }

    @Composable
    fun UnitSelectorButton(meterUnit: MeterUnit, selectedMeterUnit: MeterUnit) {
        // RadioButton handle selection automatically
        RadioButton(
            selected = (meterUnit == selectedMeterUnit),
            onClick = null
        )
    }

    @Composable
    fun SwitchButton(defaultValue: Boolean, onBooleanChange: (Boolean) -> kotlin.Unit) {
        Switch(
            checked = defaultValue,
            onCheckedChange = { newValue -> onBooleanChange(newValue) },
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }

    @Composable
    fun SubmitButton(meterName: String, meterCost: String, selectedMeterUnit: MeterUnit,
                     selectedType: MeterType, isAdditive: Boolean, navigator: Navigator?,
                     snackbarHostState: SnackbarHostState, scope: CoroutineScope, mainPageScreenModel: MainPageScreenModel)
    {
        FilledTonalButton(
            onClick = {
                // val homePageScreenModel = MainPageScreenModel
                if (meterName.isNotBlank() && meterCost.isNotBlank()) {
                    // TODO ( Update the meterID ? )
                    val newMeter = Meter(meterID = 0, meterName = meterName, meterUnit = selectedMeterUnit,
                        meterIcon = selectedType.icon, meterType = selectedType, housingID = 0,
                        meterCost = meterCost.toDoubleOrNull() ?: 0.0, additiveMeter = isAdditive)

                    mainPageScreenModel.addMeter(newMeter)
                    navigator?.pop()
                }
                else {
                    if (meterName.isBlank()) {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Field 'Name' is required",
                                withDismissAction = true,
                            )
                        }
                    }
                    if (meterCost.isBlank()) {
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


    /**********************
     Elements in the TopBar
     **********************/

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TopBar(navigator: Navigator?) {
        TopAppBar(
            title = { Text(text = "Add Reading") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            navigationIcon = { BackButton(navigator) },
            actions = { }
        )
    }


    /******
     Texts
     *****/

    @Composable
    fun MeterTextField(name: String, onNameChange: (String) -> kotlin.Unit) {
        var nameMeter by remember { mutableStateOf(name) }
        TextField(
            value = nameMeter,
            onValueChange = {
                nameMeter = it
                onNameChange(it)
            },
            label = { Text("Meter Name") },
            modifier = Modifier.fillMaxWidth()
        )
    }

    @Composable
    fun MeterCostTextField(cost: String, onNameChange: (String) -> kotlin.Unit) {
        TextField(
            value = cost,
            onValueChange = { newName ->
                onNameChange(newName.takeIf { it.isNotEmpty() } ?: "0.0")
            },
            label = { Text("Cost Per Unit") },
            modifier = Modifier.fillMaxWidth()
        )
    }

    @Composable
    fun DisplayText(text: String) {
        Text(
            text = text,
            modifier = Modifier.padding(start = 8.dp)
        )
    }


    /*******
     Options
     *******/

    @Composable
    @ExperimentalLayoutApi
    fun MeterTypeOptions(meterTypes: EnumEntries<MeterType>, selectedType: MeterType, onTypeSelected: (MeterType) -> kotlin.Unit) {
        FlowRow(Modifier.padding(10.dp),Arrangement.SpaceEvenly) {
            meterTypes.forEach { type ->
                Column(
                    modifier = Modifier
                        .clickable { onTypeSelected(type) }
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TypeSelectorButton(type, selectedType, onTypeSelected)
                    DisplayText(type.type)
                }

            }
            Spacer(modifier = Modifier.height(5.dp))
        }
    }

    @Composable
    @ExperimentalLayoutApi
    fun MeterUnitOptions(selectedType: MeterType, selectedMeterUnit: MeterUnit, onUnitSelected: (MeterUnit) -> kotlin.Unit) {
        FlowColumn(
            Modifier
                .padding(4.dp)
                .height(100.dp),Arrangement.SpaceBetween) {
            selectedType.meterUnits.forEach { unit ->
                Row(
                    modifier = Modifier
                        .clickable { onUnitSelected(unit) }
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    UnitSelectorButton(unit, selectedMeterUnit)
                    DisplayText(unit.symbol)
                }
                Spacer(modifier = Modifier.height(5.dp))
            }
        }
    }
}