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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
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
import ucl.student.meterbuddy.viewmodel.MainPageScreenModel


class RegisterScreen: Screen {
    private lateinit var auth: FirebaseAuth
    private lateinit var navigator: Navigator
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

        val currencies = listOf("EUR (€)", "USD ($)", "GBP (£)", "JPY (¥)", "AUD ($)")
        var selectedCurrency by remember { mutableStateOf(currencies[0]) }

        val mainPageScreenModel: MainPageScreenModel = getViewModel<MainPageScreenModel>()
        navigator = LocalNavigator.currentOrThrow
        var expanded by remember { mutableStateOf(false) }
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
            Spacer(modifier = Modifier.height(30.dp))
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth(),
                isError = emptyUsername
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
                isError = emailFormatError && email.isNotEmpty() || emptyEmail
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
                isError = emptyPassword
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
                isError = passwordMatchError && confirmPassword.isNotEmpty() || emptyConfirmPassword
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
            TextField(
                value = selectedCurrency,
                onValueChange = { },
                label = { Text("Select Currency") },
                trailingIcon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.baseline_arrow_drop_down_24),
                        contentDescription = "Dropdown",
                        Modifier.clickable { expanded = !expanded }
                    )
                },
                readOnly = true,  // Make the TextField read-only
                modifier = Modifier.fillMaxWidth().clickable { expanded = true }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                currencies.forEach { currency ->
                    DropdownMenuItem(
                        text = { Text(currency) },
                        onClick = {
                            selectedCurrency = currency
                            expanded = false
                        }
                    )
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

                if (!passwordMatchError && !emailFormatError && !emptyEmail && !emptyPassword && !emptyUsername && !emptyConfirmPassword) {
                    isLoading = true
                    mainPageScreenModel.registerUser(email, password)
                }}, modifier = Modifier.width(350.dp)) {
                Text("Register")
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically){
                Text(text = "Already have an account?", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.width(8.dp))
                TextButton(onClick = { navigator.push(LoginScreen()) }) {
                    Text(text = "Log in")
                }
            }
//            Button(onClick = { navigator.push(LoginScreen()) }) {
//                Text(text = "Login")
//            }
        }
    }
}