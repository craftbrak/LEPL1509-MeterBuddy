package ucl.student.meterbuddy.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import co.yml.charts.common.extensions.isNotNull
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ucl.student.meterbuddy.R
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

data class AddReadingScreen(
    val nameMeter: String,
    val lastDate: LocalDateTime = LocalDateTime.now(),
    val lastValue: Float? = null,
    val lastNote: String? = null,
    val edit: Boolean = false,
    val onSubmit: (value: Float, date: LocalDateTime, note: String?) -> Boolean
) : Screen {
    /*************
    Main function
     *************/

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {

        val hostState: SnackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        val navigator = LocalNavigator.currentOrThrow

        var reading by remember { mutableStateOf(lastValue.toString()) }
        var note by remember { mutableStateOf(lastNote ?: "") }

        val showDialog = remember { mutableStateOf(false) }
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = lastDate.toInstant(ZoneOffset.UTC)?.toEpochMilli()
        )
        val showSameDateError = remember { mutableStateOf(false) }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(hostState = hostState) },
            topBar = { TopBar(navigator) },
            floatingActionButton = { LockButton() }
        ) { it ->
            Column(
                Modifier
                    .padding(it)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(150.dp))

                Spacer(modifier = Modifier.height(20.dp))

                if (showDialog.value) {
                    DisplayDateDialog(showDialog, datePickerState)
                }

                EditDateButton(showDialog, datePickerState, formatter, showSameDateError)
                if (showSameDateError.value) {
                    Text(
                        text = "A reading for this date already exists",
                        color = MaterialTheme.colorScheme.error
                    )
                }
                OutlinedTextField(
                    value = if (reading != "null") { reading } else { "" },
                    onValueChange = { reading = it },
                    label = { Text("Reading Value") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    placeholder = { if (reading == "null") {Text("0.0")} }
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Reading note") })

                Spacer(modifier = Modifier.height(20.dp))

                ConfirmReadingButton(
                    reading,
                    note,
                    hostState,
                    scope,
                    datePickerState,
                    navigator,
                    showSameDateError
                )
            }
        }
    }

    /***********************
    Elements in the TopBar
     **********************/

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TopBar(navigator: Navigator) {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            title = {
                Text(
                    text = "${
                        if (edit) stringResource(id = R.string.edit_reading) else stringResource(
                            id = R.string.add_reading
                        )
                    } $nameMeter"
                )
            },
            navigationIcon = { BackButton(navigator) },
            actions = { }
        )
    }

    /******************************
    All the Buttons and their logic
     ******************************/

    @Composable
    fun BackButton(navigator: Navigator?) {
        IconButton(onClick = { navigator?.pop() }) {
            Icon(imageVector = Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
        }
    }

    @Composable
    fun LockButton() {
        // TODO( To Implement ! Why a Lock ? )
        FloatingActionButton(
            onClick = { },
            containerColor = MaterialTheme.colorScheme.secondary,
            shape = MaterialTheme.shapes.extraLarge
        ) {
            IconButton(onClick = { /*TODO*/ }, modifier = Modifier) {
                Icon(imageVector = Icons.Outlined.Lock, contentDescription = "Sort")
            }
        }
    }

    @Composable
    fun DismissDateButton(showDialog: MutableState<Boolean>) {
        TextButton(
            onClick = { showDialog.value = false }
        ) {
            Text(stringResource(id = R.string.cancel))
        }
    }

    @Composable
    fun ConfirmDateButton(showDialog: MutableState<Boolean>, confirmEnabled: State<Boolean>) {
        TextButton(
            onClick = { showDialog.value = false },
            enabled = confirmEnabled.value
        ) {
            Text(stringResource(id = R.string.confirm))
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun EditDateButton(
        showDialog: MutableState<Boolean>,
        datePickerState: DatePickerState,
        formatter: DateTimeFormatter,
        showSameDateError: MutableState<Boolean>
    ) {
        OutlinedButton(onClick = { showDialog.value = true; showSameDateError.value = false }) {
            val selectedDateMillis = datePickerState.selectedDateMillis
            if (selectedDateMillis.isNotNull()) {
                Text(
                    text = LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(datePickerState.selectedDateMillis!!),
                        ZoneId.systemDefault()
                    ).format(formatter)
                )
            } else {
                Text(text = "Select a date")
            }
            Spacer(modifier = Modifier.height(5.dp))
            Icon(imageVector = Icons.Outlined.Edit, contentDescription = "Edit")
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ConfirmReadingButton(
        reading: String,
        note: String,
        snackBarHostState: SnackbarHostState,
        scope: CoroutineScope,
        datePickerState: DatePickerState,
        navigator: Navigator,
        showSameDateError: MutableState<Boolean>
    ) {
        ElevatedButton(onClick = {
            try {
                if (onSubmit(
                    reading.toFloat(), LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(datePickerState.selectedDateMillis!!),
                        ZoneId.systemDefault()
                    ), note
                    )
                ) {
                    navigator.pop()
                } else {
                    showSameDateError.value = true
                }
            } catch (e: NumberFormatException) {
                scope.launch {
                    val result = snackBarHostState.showSnackbar(
                        message = "Error: ${e.message}",
                        withDismissAction = true
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        Log.w("Snack bar", "Snack bar action performed")
                    }
                }
            }
        }) {
            Text(text = if (edit) stringResource(id = R.string.update) else stringResource(id = R.string.add))
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DisplayDateDialog(showDialog: MutableState<Boolean>, datePickerState: DatePickerState) {

        val confirmEnabled =
            remember { derivedStateOf { datePickerState.selectedDateMillis != null } }

        DatePickerDialog(
            onDismissRequest = { showDialog.value = false },
            confirmButton = { ConfirmDateButton(showDialog, confirmEnabled) },
            dismissButton = { DismissDateButton(showDialog) }
        ) {
            DatePicker(state = datePickerState, dateFormatter = DatePickerDefaults.dateFormatter())
        }
    }
}