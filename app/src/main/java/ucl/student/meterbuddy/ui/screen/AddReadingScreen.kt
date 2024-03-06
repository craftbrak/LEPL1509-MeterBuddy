package ucl.student.meterbuddy.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import java.time.Instant
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerFormatter
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.TextButton
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ucl.student.meterbuddy.viewmodel.MeterScreenModel
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


data class AddReadingScreen(val screenModel :MeterScreenModel,val meterId: Int, val meterName: String, val lastDate: LocalDateTime = LocalDateTime.now(), val lastValue: Float?=null, val edit:Boolean =false,val onSubmit: (value: Float, date: LocalDateTime, note:String?)-> Unit): Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        val navigator = LocalNavigator.currentOrThrow
        var reading by remember { mutableStateOf(lastValue.toString() )  }
        var note by remember { mutableStateOf("") }
        val context = LocalContext.current

        // Date formatting
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = lastDate.toInstant(ZoneOffset.UTC)?.toEpochMilli())
        val showDialog = remember { mutableStateOf(false) }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    title = { Text(text = "${if (!edit)  "Add Reading to" else "Edit Reading of"} $meterName") },
                    navigationIcon = {
                        IconButton(onClick = {navigator.pop()}) {
                            Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = "Back" )
                        }
                    },
                    actions = {
//                        IconButton(onClick = {
//                            scope.launch {
//                                val result = snackbarHostState.showSnackbar(
//                                    message = "Delete not Implemented yet",
//                                    actionLabel = "close"
//                                )
//                                if (result == SnackbarResult.ActionPerformed) {
//                                    Log.w("Snackbar", "Snackbar action performed")
//                                }
//                            }
//                        }) {
//                            Icon(imageVector = Icons.Outlined.Delete, contentDescription = "Delete Meter")
//                        }
                    })
            },
//            floatingActionButton = {
//                FloatingActionButton(
//                    onClick = {
//                        // Handle the click
//                    },
//                    containerColor = MaterialTheme.colorScheme.secondary,
//                    shape = MaterialTheme.shapes.extraLarge
//                ){
//                    IconButton(onClick = { /*TODO*/ }, modifier = Modifier) {
//                        Icon(imageVector = Icons.Outlined.Lock, contentDescription = "Sort")
//                    }
//                }
//            }
        ) { it ->
            Column(
                Modifier
                    .padding(it)
                    .fillMaxSize(), verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(150.dp))

                Spacer(modifier = Modifier.height(20.dp))
                if (showDialog.value) {
                    val confirmEnabled = remember {
                        derivedStateOf { datePickerState.selectedDateMillis != null }
                    }

                    DatePickerDialog(
                        onDismissRequest = { showDialog.value = false },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    showDialog.value = false
                                },
                                enabled = confirmEnabled.value
                            ) {
                                Text("OK")
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = {
                                    showDialog.value = false
                                }
                            ) {
                                Text("Cancel")
                            }
                        }
                    ) {
                        DatePicker(state = datePickerState, dateFormatter = DatePickerFormatter())
                    }
                }

                OutlinedButton(onClick = { showDialog.value= true
                }) {
                    Text(text = LocalDateTime.ofInstant(Instant.ofEpochMilli(datePickerState.selectedDateMillis!!),
                        ZoneId.systemDefault()).format(formatter))
                    Spacer(modifier = Modifier.height(5.dp))
                    Icon(imageVector = Icons.Outlined.Edit, contentDescription = "Edit" )
                }

                OutlinedTextField(value = reading, onValueChange = { reading = it },label = { Text("Reading Value") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                Spacer(modifier = Modifier.height(20.dp))
                OutlinedTextField(value = note, onValueChange = { note = it },label = { Text("Reading note") }, )
//                TextField(value = reading, onValueChange ={ reading=it}){
//                    Text(text = "Reading Value")
//                }
                Spacer(modifier = Modifier.height(20.dp))
                ElevatedButton(onClick = {
                    try {
                        onSubmit(reading.toFloat(),LocalDateTime.ofInstant(Instant.ofEpochMilli(datePickerState.selectedDateMillis!!),
                            ZoneId.systemDefault()),note); navigator.pop()
                    }
                    catch (e:NumberFormatException){
                        scope.launch {
                            val result = snackbarHostState.showSnackbar(
                                message = "Error: ${e.message}",
                                withDismissAction = true
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                Log.w("Snackbar", "Snackbar action performed")
                            }
                        }
                    }
                }) {
                    Text(text = if (!edit) "Add" else "Update")
                }

            }
        }
    }
}