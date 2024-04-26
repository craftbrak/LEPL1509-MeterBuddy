package ucl.student.meterbuddy.ui.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.google.firebase.auth.FirebaseAuth
import ucl.student.meterbuddy.R
import ucl.student.meterbuddy.data.model.enums.Currency
import ucl.student.meterbuddy.data.utils.AuthException
import ucl.student.meterbuddy.data.utils.Resource
import ucl.student.meterbuddy.viewmodel.MainPageScreenModel


class RegisterScreen: Screen {
    private lateinit var auth: FirebaseAuth
    private lateinit var navigator: Navigator
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        var username by remember { mutableStateOf("") }
        var emptyUsername by remember { mutableStateOf(false) }
        var email by remember { mutableStateOf("") }
        var emailFormatError by remember { mutableStateOf(false) }
        var emptyEmail by remember { mutableStateOf(false) }

        var password by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }
        var passwordVisible by remember { mutableStateOf(false) }
        var emptyPassword by remember { mutableStateOf(false) }
        var confirmPasswordVisible by remember { mutableStateOf(false) }
        var passwordMatchError by remember { mutableStateOf(false) }
        var emptyConfirmPassword by remember { mutableStateOf(false) }

        val currencies = Currency.entries.toTypedArray()
        var selectedCurrency by remember { mutableStateOf(currencies[0]) }

        val mainPageScreenModel: MainPageScreenModel = getViewModel<MainPageScreenModel>()
        navigator = LocalNavigator.currentOrThrow
        var expanded by remember { mutableStateOf(false) }

        var errorFirebase by remember { mutableStateOf("") }
        var isLoading by remember { mutableStateOf(false) }

        auth = mainPageScreenModel.auth

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(35.dp))
            Text(text = "Create an account", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(20.dp))
            if (errorFirebase.isNotEmpty()) {
                Text(
                    text = errorFirebase,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = 16.dp),
                    textAlign = TextAlign.Start
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth(),
                isError = emptyUsername || errorFirebase.isNotEmpty(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                )
            )
            if (emptyUsername) {
                Text(
                    text = "Username cannot be empty",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = 16.dp),
                    textAlign = TextAlign.Start
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                isError = emailFormatError && email.isNotEmpty() || emptyEmail || errorFirebase.isNotEmpty(),
                placeholder = { Text("example@example.com", color = MaterialTheme.colorScheme.outline) },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                )
            )
            if (emptyEmail) {
                Text(
                    text = "Email cannot be empty",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = 16.dp),
                    textAlign = TextAlign.Start
                )
            } else if (emailFormatError) {
                Text(
                    text = "Invalid email format",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = 16.dp),
                    textAlign = TextAlign.Start
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val imageVector = if (passwordVisible)
                        ImageVector.vectorResource(id = R.drawable.baseline_visibility)
                    else ImageVector.vectorResource(id = R.drawable.baseline_visibility_off)

                    val description = if (passwordVisible) "Hide password" else "Show password"


                    IconButton(onClick = {passwordVisible = !passwordVisible}) {
                        Icon(imageVector = imageVector, contentDescription = description)
                    }
                },
                isError = emptyPassword || errorFirebase.isNotEmpty(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                )
            )
            if (emptyPassword) {
                Text(
                    text = "Password cannot be empty",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = 16.dp),
                    textAlign = TextAlign.Start
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val imageVector = if (confirmPasswordVisible)
                        ImageVector.vectorResource(id = R.drawable.baseline_visibility)
                    else ImageVector.vectorResource(id = R.drawable.baseline_visibility_off)

                    val description = if (confirmPasswordVisible) "Hide password" else "Show password"


                    IconButton(onClick = {confirmPasswordVisible = !confirmPasswordVisible}) {
                        Icon(imageVector = imageVector, contentDescription = description)
                    }
                },
                isError = passwordMatchError && confirmPassword.isNotEmpty() || emptyConfirmPassword || errorFirebase.isNotEmpty(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                )
            )
            if (emptyConfirmPassword) {
                Text(
                    text = "Password cannot be empty",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = 16.dp),
                    textAlign = TextAlign.Start
                )
            } else if (passwordMatchError) {
                Text(
                    text = "Passwords do not match",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = 16.dp),
                    textAlign = TextAlign.Start
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded}
            ) {
                OutlinedTextField(
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    value = "${selectedCurrency.currencyCode} (${selectedCurrency.symbol})",
                    onValueChange = { },
                    label = { Text("Currency") },
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    }
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.fillMaxWidth()) {
                    currencies.forEachIndexed { index, currency ->
                        DropdownMenuItem(
                            text = { Text(text = "${currency.currencyCode} (${currency.symbol})") },
                            onClick = {
                                selectedCurrency = currencies[index]
                                expanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }

                }
            }
            Spacer(modifier = Modifier.height(30.dp))
            Button(onClick = {
                emptyUsername = username.isEmpty()
                emptyEmail = email.isEmpty()
                emailFormatError = email.contains("@").not() || email.contains(".").not()
                emptyPassword = password.isEmpty()
                emptyConfirmPassword = confirmPassword.isEmpty()
                passwordMatchError = password != confirmPassword
                errorFirebase = ""

                if (!passwordMatchError && !emailFormatError && !emptyEmail && !emptyPassword && !emptyUsername && !emptyConfirmPassword) {
                    isLoading = true
                    val trimmedEmail = email.trim()
                    mainPageScreenModel.registerUser(trimmedEmail, password, username, selectedCurrency)
                }},
                modifier = Modifier.width(350.dp)) {
                if(isLoading){
                    CircularProgressIndicator(
                        modifier = Modifier.size(30.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                } else {
                    Text("Register")
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically){
                Text(text = "Already have an account?", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.width(8.dp))
                TextButton(onClick = { navigator.push(LoginScreen()) }) {
                    Text(text = "Log in")
                }
            }

            LaunchedEffect(key1 = mainPageScreenModel.state.value.currentUser) {
                val currentUserFlow = mainPageScreenModel.state.value.currentUser
                currentUserFlow.collect{
                    when(it){
                        is Resource.Error -> {
                            isLoading = false
                            when(it.error) {
                                AuthException.BAD_CREDENTIALS -> {
                                    Log.i("Bad Cred","bad cred")
//                                    showToast(context = context, message = "Invalid email or password")
                                    errorFirebase = "Invalid email or password"
                                }
                                AuthException.NO_NETWORK -> {
                                    Log.i("No Network","cool")
//                                    showToast(context = context, message = "No network connection")
                                    errorFirebase = "No network connection"
                                }
                                AuthException.NO_CURRENT_USER -> Log.i("No Current User","nobody connected")
                                AuthException.TO_MANY_ATTEMPT -> {
                                    Log.i("LoginScreen","To MAny attempts")
//                                    showToast(context = context, message = "Too many attempt try again later")
                                    errorFirebase = "Too many attempt, try again later"
                                }
                                AuthException.EMAIL_ALREADY_TAKEN -> {
                                    Log.i("EMAIL_ALREADY_TAKEN","EMAIL_ALREADY_TAKEN")
                                    errorFirebase = "The email address is already in use by another account"
                                }
                                AuthException.EMAIL_BAD_FORMATTED -> {
                                    Log.i("EMAIL_BAD_FORMATTED","EMAIL_BAD_FORMATTED")
                                    errorFirebase = "The email address is badly formatted"
                                }
                                AuthException.PASSWORD_TO0_SHORT -> {
                                    Log.i("PASSWORD_TO0_SHORT","PASSWORD_TO0_SHORT")
                                    errorFirebase = "Password should be at least 6 characters"
                                }
                                AuthException.UNKNOWN_ERROR -> {
                                    Log.i("HAAAAAAAAAAAAAAAAAAa","merde")
//                                    showToast(context = context, message = "An unknown error occurred")
                                    errorFirebase = "An unknown error occurred"
                                }
                            }
                        }
                        is Resource.Loading -> Log.i("Loading please wait", "wait")
                        is Resource.Success -> Log.i("Success", "Success")
                    }
                }
            }

        }
    }
}