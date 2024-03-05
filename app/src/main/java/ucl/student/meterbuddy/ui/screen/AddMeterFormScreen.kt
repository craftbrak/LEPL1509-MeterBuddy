package ucl.student.meterbuddy.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import ucl.student.meterbuddy.data.model.enums.MeterIcon
import ucl.student.meterbuddy.data.model.enums.MeterType
import ucl.student.meterbuddy.data.model.enums.Unit
import ucl.student.meterbuddy.data.model.entity.Meter

data class AddMeterFormScreen(val meters: MutableList<Meter>): Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {

        var meterName by remember { mutableStateOf("") }
        var selectedType by remember { mutableStateOf(MeterType.ELECTRICITY) }
        var selectedUnit by remember { mutableStateOf(Unit.KILO_WATT_HOUR) }
        var meterCost by remember { mutableStateOf("") }
        var isAdditive by remember { mutableStateOf(true) }
        var nameFieldRequired by remember { mutableStateOf(false) }
        var costFieldRequired by remember { mutableStateOf(false) }

        val navigator = LocalNavigator.current

        val meterTypes = listOf(
            MeterType.ELECTRICITY,
            MeterType.GAS,
            MeterType.WATER,
            MeterType.CAR,
            MeterType.HOT_WATER,
        )
        Scaffold(
            modifier = Modifier.fillMaxSize(),
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
                },)
            }
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
                Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Center) {
                    meterTypes.forEach { type ->
                        Row(
                            modifier = Modifier
                                .clickable { selectedType = type }
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (type == selectedType),
                                onClick = null // RadioButton handle selection automatically
                            )
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
                Column {
                    Unit.values().forEach { unit ->
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
                                text = unit.unit,
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
                    label = { Text("Meter Cost") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Additive Meter", modifier = Modifier.weight(1f))
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
                    Button(
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
                                meters.add(newMeter)
                            } else {
                                if (!meterName.isNotBlank()) { nameFieldRequired = true }
                                if (!meterCost.isNotBlank()) { costFieldRequired = true }
                            }
                        }
                    ) {
                        Text("Confirm")
                    }
                }

                if (nameFieldRequired) {
                    Text(
                        text = "Field 'Name' is required",
                        color = Color.Red,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }

                if (costFieldRequired) {
                    Text(
                        text = "Field 'Cost' is required",
                        color = Color.Red,
                        modifier = Modifier.padding(start = 16.dp)

                    )
                }
            }
        }

    }
}