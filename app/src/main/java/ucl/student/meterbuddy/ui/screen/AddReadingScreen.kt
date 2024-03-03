package ucl.student.meterbuddy.ui.screen

import android.icu.text.SimpleDateFormat
import android.os.Build
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import kotlinx.coroutines.launch
import java.time.Instant
import androidx.annotation.RequiresApi
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import java.util.Calendar
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
data class AddReadingScreen(val meterId: Int, val meterName: String, val lastDate: Long = Instant.now().epochSecond, val lastValue: Double?=null): Screen {

    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        val navigator = LocalNavigator.currentOrThrow
        var inputValue: Double = lastValue ?: 0.0
        var date by remember { mutableStateOf(Date.from(Instant.ofEpochSecond(lastDate))) }
        var reading by remember { mutableStateOf(lastValue.toString()) }
        var note by remember { mutableStateOf("") }
        var selectedDate by remember { mutableStateOf("") }
        val context = LocalContext.current
        // Date formatting
        val dateFormat = SimpleDateFormat("EEE, dd/MM/yyyy", Locale.getDefault())

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    title = { Text(text = "Add Reading to $meterName", style = MaterialTheme.typography.headlineLarge) },
                    navigationIcon = {
                        IconButton(onClick = {navigator.pop()}) {
                            Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = "Back")
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
        ) {
            Column(Modifier.padding(it)) {
                OutlinedButton(onClick = { showDatePicker(context,dateFormat,"") {
                    selectedDate = it
                }
                }) {
                    val ddate= if (date.toString().isNotEmpty()) date else "Select Date"
                    Text(text = ddate.toString())
                }
//                DatePicker(state = date,)
                OutlinedTextField(
                    value = reading,
                    onValueChange = { reading = it },
                    label = { Text("Reading") }
                )
//                TextField(value = inputValue.toString(), onValueChange ={ inputValue=it.toDouble()})
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    fun showDatePicker(
        context: android.content.Context,
        dateFormat: SimpleDateFormat,
        currentDate: String,
        onDateSelected: (String) -> Unit
    ) {
//        val calendar = Calendar.getInstance()
//
//        // If we already have a date, parse it and set it on the calendar
//        if (currentDate.isNotEmpty()) {
//            calendar.time = dateFormat.parse(currentDate) ?: Date()
//        }
//
//        val year = calendar.get(Calendar.YEAR)
//        val month = calendar.get(Calendar.MONTH)
//        val day = calendar.get(Calendar.DAY_OF_MONTH)
//
//        val datePickerDialog = DatePickerDialog(
//            context, { _, year, monthOfYear, dayOfMonth ->
//                // Update the calendar with the new date selected by the user
//                calendar.set(year, monthOfYear, dayOfMonth)
//                val dateString = dateFormat.format(calendar.time)
//                onDateSelected(dateString)
//            }, year, month, day
//        )
//
//        datePickerDialog.show()
    }

}
