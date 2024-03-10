package ucl.student.meterbuddy

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity

class Login : ComponentActivity() {

    private var passwordIsShown = false

    lateinit var username: EditText
    lateinit var password: EditText

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        username = findViewById(R.id.username)
        password = findViewById(R.id.password)

        val signInButton = findViewById<TextView>(R.id.signInButton)
        val signUpButton = findViewById<TextView>(R.id.signUpButton)

        val passwordIcon = findViewById<ImageView>(R.id.passwordIcon)

        passwordIcon.setOnClickListener {
            // Change the Icon and show/hide the password
            if (passwordIsShown) {
                password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                passwordIcon.setImageResource(R.drawable.show_password)
            } else {
                password.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                passwordIcon.setImageResource(R.drawable.hide_password)
            }
            passwordIsShown = !passwordIsShown

            // Move the cursor at the last character of the password
            // TODO (Hide completely the password)
            password.setSelection(password.length())
        }

        signUpButton.setOnClickListener {
            // Open 'Register' Activity
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

        signInButton.setOnClickListener {
            // TODO (Check into Database if the username/password is Ok)
            val personValidated = true
            if (personValidated) {
                // Open 'MainActivity' Activity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)

                // End the activity (No 'back' button)
                finish()
            } else {
                // TODO (Display an error message)
            }
        }
    }

    private val textWatcher = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            s?.let {
                when (it) {
                    username.text -> username.text = it
                    password -> password.text = it
                }
            }
        }
    }
}