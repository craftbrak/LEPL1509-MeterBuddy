package ucl.student.meterbuddy

import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class Login : AppCompatActivity() {

    private var passwordIsShown = false

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)

        val signUpButton = findViewById<TextView>(R.id.signUpButton)

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
            password.setSelection(password.length())
        }

        signUpButton.setOnClickListener {
            // TODO()
        }
    }
}