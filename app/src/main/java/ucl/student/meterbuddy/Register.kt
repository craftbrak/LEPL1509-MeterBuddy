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
import androidx.appcompat.widget.AppCompatButton

class Register : ComponentActivity() {

    private var passwordIsShown = false
    private var confirmPasswordIsShown = false

    lateinit var username: EditText
    lateinit var fullName: EditText
    lateinit var email: EditText
    lateinit var mobile: EditText
    lateinit var password: EditText
    lateinit var confirmPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        username = findViewById(R.id.username)
        fullName = findViewById(R.id.fullName)
        email = findViewById(R.id.email)
        mobile = findViewById(R.id.mobile)
        password = findViewById(R.id.password)
        confirmPassword = findViewById(R.id.confirmPassword)

        val passwordIcon = findViewById<ImageView>(R.id.passwordIcon)
        val confirmPasswordIcon = findViewById<ImageView>(R.id.confirmPasswordIcon)

        val signUpButton = findViewById<AppCompatButton>(R.id.signUpButton)
        val signInButton = findViewById<TextView>(R.id.signInButton)

        password.addTextChangedListener(textWatcher)
        confirmPassword.addTextChangedListener(textWatcher)
        email.addTextChangedListener(textWatcher)
        mobile.addTextChangedListener(textWatcher)
        fullName.addTextChangedListener(textWatcher)
        username.addTextChangedListener(textWatcher)

        signInButton.setOnClickListener {
            // Open Login Activity
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        signUpButton.setOnClickListener {
            val mobileTxt = mobile.text.toString()
            val emailTxt = email.text.toString()
            val fullNameTxt = fullName.text.toString()
            val passwordTxt = password.text.toString()
            val confirmPasswordTxt = confirmPassword.text.toString()
            val usernameTxt = username.text.toString()

            if (passwordTxt == confirmPasswordTxt) {
                // Open OTP Verification Activity
                val intent = Intent(this, OTPVerification::class.java)
                // TODO (Check if the mobile and the email are valid)
                intent.putExtra("mobile", mobileTxt)
                intent.putExtra("email", emailTxt)
                intent.putExtra("fullName", fullNameTxt)
                intent.putExtra("password", passwordTxt)
                intent.putExtra("username", usernameTxt)
                startActivity(intent)
            }
        }

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
            password.setSelection(password.length())
        }

        confirmPasswordIcon.setOnClickListener {
            // Change the Icon and show/hide the password
            if (confirmPasswordIsShown) {
                confirmPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                confirmPasswordIcon.setImageResource(R.drawable.show_password)
            } else {
                confirmPassword.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                confirmPasswordIcon.setImageResource(R.drawable.hide_password)
            }
            confirmPasswordIsShown = !confirmPasswordIsShown

            // Move the cursor at the last character of the password
            confirmPassword.setSelection(confirmPassword.length())
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
                    fullName -> fullName.text = it
                    email -> email.text = it
                    mobile-> mobile.text = it
                    password -> password.text = it
                    confirmPassword -> confirmPassword.text = it
                }
            }
        }
    }
}