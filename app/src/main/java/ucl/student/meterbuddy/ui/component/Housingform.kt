package ucl.student.meterbuddy.ui.component

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ucl.student.meterbuddy.data.model.entity.Housing
import ucl.student.meterbuddy.data.model.enums.HousingType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HousingFrom(
    modifier: Modifier = Modifier,
    onSubmit: (Housing) -> Unit,
    initialData: Housing? = null
) {
    var housingName by remember { mutableStateOf(initialData?.housingName ?: "") }
    var housingType by remember { mutableStateOf(initialData?.housingType ?: HousingType.House) }
    var housingSurface by remember { mutableStateOf(initialData?.housingSurface?.toString() ?: "") }
    var housingNbPersons by remember { mutableStateOf(initialData?.housingNbPersons?.toString() ?: "") }
    val housingId by remember { mutableStateOf(initialData?.housingID ?: 0) }
    var expanded by remember { mutableStateOf(false) }
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        OutlinedTextField(
            value = housingName,
            onValueChange = { housingName = it },
            label = { Text("Housing Name") },
            placeholder = { Text("My Home") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Log.wtf("Housing Form", initialData?.housingName)

        OutlinedTextField(
            value = housingSurface,
            onValueChange = { housingSurface = it },
            label = { Text("Surface (M³)") },
            placeholder = { Text("350") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = housingNbPersons,
            onValueChange = { housingNbPersons = it },
            label = { Text("Number of Persons") },
            placeholder = { Text("2") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        MultiChoiceSegmentedButtonRow {
            HousingType.entries.forEachIndexed{index, houseType ->
                SegmentedButton(checked = housingType == houseType , onCheckedChange = { bool -> housingType = houseType } , shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = HousingType.entries.size
                ) ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = ImageVector.vectorResource(id = houseType.icon), contentDescription = houseType.name)
                        Text(text = houseType.type, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

        }

    //        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
    //            OutlinedTextField(
    //                modifier = Modifier
    //                    .menuAnchor()
    //                    .fillMaxWidth(),
    //                value = housingType.name,
    //                onValueChange = { },
    //                label = { Text("Housing Type") },
    //                readOnly = true,
    //                trailingIcon = {
    //                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
    //                }
    //            )
    //            ExposedDropdownMenu(
    //                expanded = expanded, // Dropdown menus are not yet supported in Compose as of 1.1.1
    //                onDismissRequest = { expanded =false},
    //                modifier = Modifier.fillMaxWidth()
    //            ) {
    //                HousingType.entries.forEach {
    //                    DropdownMenuItem(text = {
    //                        Row(verticalAlignment = Alignment.CenterVertically) {
    //                            Icon(imageVector = ImageVector.vectorResource(id = it.icon), contentDescription = it.name)
    //                            Text(text = it.type)
    //                        }
    //
    //                    }, onClick = { housingType = it ; expanded = false })
    //                }
    //            }
    //        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            onSubmit(
                Housing(
                    housingID = housingId,
                    housingName = housingName,
                    housingType = housingType,
                    housingSurface = housingSurface.toIntOrNull() ?: 0,
                    housingNbPersons = housingNbPersons.toIntOrNull() ?: 0
                )
            )
        },
            modifier = Modifier.fillMaxWidth(.5f)) {
            Text(if (initialData == null) "Add" else "Update")
        }
    }
}

@Preview(name = "HousingHome")
@Composable
private fun PreviewHousingHome() {
    HousingFrom(Modifier.fillMaxWidth(), {})
}