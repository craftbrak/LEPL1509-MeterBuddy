package ucl.student.meterbuddy

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.appcompat.widget.AppCompatButton

class Register : ComponentActivity() {

    private var passwordIsShown = false
    private var confirmPasswordIsShown = false

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val email = findViewById<EditText>(R.id.email)
        val mobile = findViewById<EditText>(R.id.mobile)
        val password = findViewById<EditText>(R.id.password)
        val confirmPassword = findViewById<EditText>(R.id.confirmPassword)

        val passwordIcon = findViewById<ImageView>(R.id.passwordIcon)
        val confirmPasswordIcon = findViewById<ImageView>(R.id.confirmPasswordIcon)

        val signUpButton = findViewById<AppCompatButton>(R.id.signUpButton)
        val signInButton = findViewById<TextView>(R.id.signInButton)

        signInButton.setOnClickListener {
            // Open Login Activity
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        signUpButton.setOnClickListener {
            val mobileTxt = mobile.text.toString()
            val emailTxt = email.text.toString()

            // Open OTP Verification Activity
            val intent = Intent(this, OTPVerification::class.java)
            intent.putExtra("mobile", mobileTxt)
            intent.putExtra("email", emailTxt)
            startActivity(intent)
        }

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

        confirmPassword.setOnClickListener {
            // Change the Icon and show/hide the password
            if (confirmPasswordIsShown) {
                confirmPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                confirmPasswordIcon.setImageResource(R.drawable.hide_password)
            } else {
                confirmPassword.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                confirmPasswordIcon.setImageResource(R.drawable.show_password)
            }
            confirmPasswordIsShown = !confirmPasswordIsShown

            // Move the cursor at the last character of the password
            password.setSelection(password.length())
        }
    }
}