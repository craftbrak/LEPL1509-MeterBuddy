package ucl.student.meterbuddy.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.google.firebase.auth.FirebaseAuth
import ucl.student.meterbuddy.viewmodel.MainPageScreenModel


class RegisterScreen: Screen {
    private lateinit var auth: FirebaseAuth
    private lateinit var navigator: Navigator
    @Composable
    override fun Content() {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        val mainPageScreenModel: MainPageScreenModel = getViewModel<MainPageScreenModel>()
        navigator = LocalNavigator.currentOrThrow

        auth = mainPageScreenModel.auth

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Register")
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { registerUser(email, password) }) {
                Text("Register")
            }
            Button(onClick = { navigator.push(LoginScreen()) }) {
                Text(text = "Login")
            }
        }
    }
    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Registration success, update UI with the signed-in user's information
                    Log.i("Register",task.result.toString())
                    navigator.push(LoginScreen())
                } else {
                    // If registration fails, display a message to the user.
                    // TODO: Handle registration failure
                    Log.e("Register", task.exception?.message.toString())
                }
            }
    }
}