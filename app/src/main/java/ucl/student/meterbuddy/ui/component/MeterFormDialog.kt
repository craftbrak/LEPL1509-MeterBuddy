package ucl.student.meterbuddy.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import ucl.student.meterbuddy.data.model.enums.MeterIcon
import ucl.student.meterbuddy.data.model.enums.MeterType
import ucl.student.meterbuddy.data.model.enums.Unit
import kotlin.enums.EnumEntries

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MeterFormDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> kotlin.Unit,
    onConfirmation: (name: String, unit: Unit, icon: MeterIcon, type: MeterType, cost: String, additive: Boolean) -> kotlin.Unit,
    showDialog: Boolean
) {
    var meterName by remember { mutableStateOf("") }
    var meterCost by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(MeterType.ELECTRICITY) }
    var selectedUnit by remember { mutableStateOf(Unit.KILO_WATT_HOUR) }
    var isAdditive by remember { mutableStateOf(true) }

    val meterTypes = MeterType.entries

    if (showDialog) {
        Dialog(onDismissRequest = { onDismissRequest() }) {
            Card(
                modifier = modifier.verticalScroll(rememberScrollState()),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "Add a new meter", style = MaterialTheme.typography.headlineSmall)
                    MeterTextField(meterName) { newName -> meterName = newName }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Meter Type")
                    MeterTypeOptions(meterTypes, selectedType) { newType -> selectedType = newType }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Unit")
                    MeterUnitOptions(selectedType, selectedUnit) { newUnit ->
                        selectedUnit = newUnit
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    MeterCostTextField(meterCost) { newCost -> meterCost = newCost }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Consumption", modifier = Modifier.weight(1f))
                        SwitchButton(isAdditive) { newBoolean -> isAdditive = newBoolean }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(
                            onClick = { onDismissRequest() },
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Text("Cancel")

                        }
                        TextButton(
                            onClick = { onConfirmation(meterName, selectedUnit, selectedType.icon, selectedType, meterCost, isAdditive) }, modifier = Modifier.padding(8.dp),
                        ) {
                            Text("Confirm")

                        }
                    }
                }
            }
        }
    }
}

//@Preview(name = "AddMeterFormCard")
//@Composable
//fun PreviewAddMeterFormCard() {
//    MeterBuddyTheme {
//        AddMeterFormCard(Modifier.padding(9.dp))
//    }
//}

@Composable
fun TypeSelectorButton(
    type: MeterType,
    selectedType: MeterType,
    onTypeSelected: (MeterType) -> kotlin.Unit
) {
    IconToggleButton(
        modifier = Modifier.padding(3.dp),
        checked = selectedType == type,
        onCheckedChange = { onTypeSelected(type) }) {
        Icon(
            imageVector = ImageVector.vectorResource(id = type.icon.icon),
            contentDescription = type.icon.iconName
        )
    }
}

@Composable
fun UnitSelectorButton(unit: Unit, selectedUnit: Unit) {
    // RadioButton handle selection automatically
    RadioButton(
        selected = (unit == selectedUnit),
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
fun MeterTypeOptions(
    meterTypes: EnumEntries<MeterType>,
    selectedType: MeterType,
    onTypeSelected: (MeterType) -> kotlin.Unit
) {
    FlowRow(Modifier.padding(10.dp), Arrangement.SpaceEvenly) {
        meterTypes.forEach { type ->
            Column(
                modifier = Modifier
                    .clickable { onTypeSelected(type) }
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TypeSelectorButton(type, selectedType, onTypeSelected)
                DisplayText(type.icon.iconName)
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
    }
}

@Composable
@ExperimentalLayoutApi
fun MeterUnitOptions(
    selectedType: MeterType,
    selectedUnit: Unit,
    onUnitSelected: (Unit) -> kotlin.Unit
) {
    FlowColumn(
        Modifier
            .padding(4.dp)
            .height(100.dp), Arrangement.SpaceBetween
    ) {
        selectedType.units.forEach { unit ->
            Row(
                modifier = Modifier
                    .clickable { onUnitSelected(unit) }
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                UnitSelectorButton(unit, selectedUnit)
                DisplayText(unit.symbol)
            }
            Spacer(modifier = Modifier.height(5.dp))
        }
    }
}