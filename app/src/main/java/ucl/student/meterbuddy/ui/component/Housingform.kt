package ucl.student.meterbuddy.ui.component

import android.util.Log
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import co.yml.charts.common.extensions.isNotNull
import kotlinx.coroutines.launch
import ucl.student.meterbuddy.data.model.entity.Housing
import ucl.student.meterbuddy.data.model.entity.User
import ucl.student.meterbuddy.data.model.enums.HousingType

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HousingFrom(
    modifier: Modifier = Modifier,
    onSubmit: (Housing) -> Unit,
    initialData: Housing? = null,
    usersOfHousing:List<User> = emptyList(),
    users:List<User> = emptyList(),
    onUserRemove:(User)->Unit = {},
    onUserAdd:(User)->Unit = {}
) {
    var housingName by remember { mutableStateOf(initialData?.housingName ?: "") }
    var housingType by remember { mutableStateOf(initialData?.housingType ?: HousingType.House) }
    var housingSurface by remember { mutableStateOf(initialData?.housingSurface?.toString() ?: "") }
    var housingNbPersons by remember { mutableIntStateOf(initialData?.housingNbPersons ?: 0) }
    val housingId by remember { mutableStateOf(initialData?.housingID ?: 0) }
    var showUserSelect by remember { mutableStateOf(false) }
    var userSearch by remember { mutableStateOf("") }
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
            onValueChange = {
                if (it.chars().filter { c -> c.toChar() =='.' || c.toChar() ==',' }.count() <2){
                    housingSurface = it
                }
                            },
            label = { Text("Surface (m²)") },
            placeholder = { Text("350") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,

                // Hide the keyboard action button
                imeAction = androidx.compose.ui.text.input.ImeAction.None
            ),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))
//        OutlinedTextField(
//            value = housingNbPersons.toString(),
//            onValueChange = { housingNbPersons = it.toInt() },
//            label = { Text("Number of Persons") },
//            placeholder = { Text("2") },
//            modifier = Modifier.fillMaxWidth()
//        )
        Row (verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly){
            Text(text = "Number Of Persons: ", style = MaterialTheme.typography.labelLarge)
            CounterField(counterValue = housingNbPersons.toString(),
                modifier = Modifier
                    .padding(3.dp)
                    .width(150.dp),
                onMinusChange = {
                    if (housingNbPersons > 0) {
                        housingNbPersons--
                    }
                },
                onAddChange = {housingNbPersons++})
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            SingleChoiceSegmentedButtonRow {
                HousingType.entries.forEachIndexed { index, houseType ->
                    SegmentedButton(
                        selected = housingType == houseType,
                        onClick = { housingType = houseType },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = HousingType.entries.size
                        )) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = houseType.icon),
                                contentDescription = houseType.name
                            )
                            Text(
                                text = houseType.type,
                                style = MaterialTheme.typography.labelSmall,
                                overflow = TextOverflow.Ellipsis,
                                softWrap = false
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        if (initialData.isNotNull()){
            ElevatedCard {
                FlowRow(
                    Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(8.dp), horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    usersOfHousing.forEach {
                        InputChip(onClick = { onUserRemove(it) }, selected = false, label = {
                            Text(text = it.userName)
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = Icons.Filled.Close.name
                            )
                        })
                    }
                    FilledTonalButton(onClick = { showUserSelect = true }) {
                        Text(text = "Add Member")
                    }
                }
            }
        }
        if (showUserSelect){
            Dialog(onDismissRequest = { showUserSelect = false }) {
                Card (Modifier.padding(15.dp)) {
                    Column (modifier= Modifier.padding(15.dp),horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center){
                        Text("Add Member To $housingName")
                        val tooltipState = remember { mutableStateOf(TooltipState()) }
                        val scope = rememberCoroutineScope()
                        TooltipBox(
                            positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                            tooltip = {
                                PlainTooltip {
                                    Text("Search for a user to add to the housing")
                                }
                            },
                            state = tooltipState.value,
                            content = {
                                OutlinedTextField(
                                    userSearch,
                                    { userSearch = it },
                                    label = { Text("Search") },
                                    placeholder = { Text("username") },
                                    leadingIcon = {
                                        IconButton(
                                            onClick = {
                                                scope.launch {
                                                    tooltipState.value.show(
                                                        MutatePriority.Default
                                                    )
                                                }
                                            },
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Search,
                                                contentDescription = Icons.Filled.Search.name
                                            )
                                        }

                                    },
                                    trailingIcon = {
                                        IconButton(onClick = { userSearch = "" }) {
                                            Icon(
                                                imageVector = Icons.Filled.Close,
                                                contentDescription = Icons.Filled.Close.name
                                            )
                                        }
                                    },
                                    singleLine = true
                                )


                            })
                        FlowRow(modifier = Modifier.heightIn(50.dp,500.dp)) {
                            users.filter { u ->
                                u.userName.contains(
                                    userSearch,
                                    ignoreCase = true
                                )
                            }.forEach{ user ->
                                if (user !in usersOfHousing && userSearch.isNotBlank()) {
                                    FilterChip(selected = user in usersOfHousing,
                                        onClick = { onUserAdd(user) },
                                        label = { Text(text = user.userName) })
                                }
                            }
                        }
                        Button(onClick = { showUserSelect = false }) {
                            Text(text = "Done")
                        }
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
            housingSurface.replace(',','.')
            onSubmit(
                Housing(
                    housingID = housingId,
                    housingName = housingName,
                    housingType = housingType,
                    housingSurface = housingSurface.toFloatOrNull() ?: 0f,
                    housingNbPersons = housingNbPersons
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
    val users = listOf(User(userID = 0, userName = "a"),User(userID = 0, userName = "craftbrak"),User(userID = 0, userName = "craftbrak"),User(userID = 0, userName = "tinyhuman"),User(userID = 0, userName = "TheHeavyBackPack"),User(userID = 0, userName = "tinyhuman"),User(userID = 0, userName = "TheHeavyBackPack"),User(userID = 0, userName = "tinyhuman"),User(userID = 0, userName = "TheHeavyBackPack"),User(userID = 0, userName = "tinyhuman"),User(userID = 0, userName = "TheHeavyBackPack"),User(userID = 0, userName = "tinyhuman"),User(userID = 0, userName = "TheHeavyBackPack"),User(userID = 0, userName = "tinyhuman"),User(userID = 0, userName = "TheHeavyBackPack"),User(userID = 0, userName = "tinyhuman"),User(userID = 0, userName = "TheHeavyBackPack"),User(userID = 0, userName = "tinyhuman"),User(userID = 0, userName = "TheHeavyBackPack"),User(userID = 0, userName = "tinyhuman"),User(userID = 0, userName = "TheHeavyBackPack"))
    val usersOfHousing = listOf(User(userID = 0, userName = "a"),User(userID = 0, userName = "craftbrak"),User(userID = 0, userName = "craftbrak"),User(userID = 0, userName = "tinyhuman"),User(userID = 0, userName = "TheHeavyBackPack"))
    HousingFrom(Modifier.fillMaxWidth(), {}, usersOfHousing = usersOfHousing, users = users )
}