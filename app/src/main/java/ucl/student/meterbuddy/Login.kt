package ucl.student.meterbuddy

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import org.w3c.dom.Text

class Login : ComponentActivity() {

    private var passwordIsShown = false

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)

        val signInButton = findViewById<TextView>(R.id.signInButton)
        val signUpButton = findViewById<TextView>(R.id.signUpButton)
        val signInWithGoogle = findViewById<RelativeLayout>(R.id.signInWithGoogle)
        val forgotPasswordButton = findViewById<TextView>(R.id.forgotPasswordButton)

        val passwordIcon = findViewById<ImageView>(R.id.passwordIcon)

        passwordIcon.setOnClickListener {
            // Change the Icon and show/hide the password
            if (passwordIsShown) {
                password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                passwordIcon.setImageResource(R.drawable.hide_password)
            } else {
                password.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                passwordIcon.setImageResource(R.drawable.show_password)
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

        signInWithGoogle.setOnClickListener {
            // Todo()
        }

        forgotPasswordButton.setOnClickListener {
            // Todo()
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
}