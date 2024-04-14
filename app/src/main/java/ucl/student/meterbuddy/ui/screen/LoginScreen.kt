package ucl.student.meterbuddy.ui.screen

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import ucl.student.meterbuddy.R
import ucl.student.meterbuddy.data.utils.AuthException
import ucl.student.meterbuddy.data.utils.Resource
import ucl.student.meterbuddy.viewmodel.MainPageScreenModel

class LoginScreen : Screen {

    @Composable
    override fun Content() {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        val mainPageScreenModel: MainPageScreenModel = getViewModel<MainPageScreenModel>()
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current
        var passwordVisible by remember { mutableStateOf(false) }
        var isLoading by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(70.dp))
            Image(
                painter = painterResource(id = R.drawable.meter_budy_logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(130.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(35.dp))
            Text(text = "Welcome", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
//            Spacer(modifier = Modifier.height(15.dp))
            Text(text = "Enter your email address to sign in", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(30.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(20.dp))
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
                }
            )
            Spacer(modifier = Modifier.height(30.dp))
            Button(onClick = {
                isLoading = true
                mainPageScreenModel.loginUser(email, password)
                },
                modifier = Modifier.width(350.dp)) {
                Text("Login")
            }
//            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically){
                Text(text = "Not registered?", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.width(8.dp))
                TextButton(onClick = { navigator.push(RegisterScreen()) }) {
                    Text(text = "Create an account")
                }
            }


            LaunchedEffect(key1 = mainPageScreenModel.state.value.currentUser) {
                val currentUserFlow = mainPageScreenModel.state.value.currentUser
                currentUserFlow.collect{
                    when(it){
                        is Resource.Error -> {
                            isLoading = false
                            when(it.error){
                                AuthException.BAD_CREDENTIALS -> {
                                    Log.i("Bad Cred","bad cred")
                                    showToast(context = context, message = "Invalid email or password")
                                }
                                AuthException.NO_NETWORK -> {
                                    Log.i("No Network","cool")
                                    showToast(context = context, message = "No network connection")
                                }
                                AuthException.UNKNOWN_ERROR -> {
                                    Log.i("HAAAAAAAAAAAAAAAAAAa","merde")
                                    showToast(context = context, message = "An unknown error occurred")
                                }
                                AuthException.NO_CURRENT_USER -> Log.i("No Current User","nobody connected")
                            }
                        }
                        is Resource.Loading -> Log.i("Loading please wait", "wait")
                        is Resource.Success -> Log.i("Success", "Success")
                    }
                }
            }

            if (isLoading) {
                Dialog(onDismissRequest = {isLoading = false}) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier
                        .size(100.dp)
                        .background(Color.White, shape = RoundedCornerShape(8.dp))) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(64.dp),
                            color = MaterialTheme.colorScheme.secondary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
            }
        }
    }

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

}