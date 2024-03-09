package ucl.student.meterbuddy

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

class Register : AppCompatActivity() {

    // TODO ( Implement the logic )
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        val email = findViewById<EditText>(R.id.email)
        val mobile = findViewById<EditText>(R.id.mobile)
        val password = findViewById<EditText>(R.id.password)
        val confirmPassword = findViewById<EditText>(R.id.confirmPassword)

        val passwordIcon = findViewById<ImageView>(R.id.passwordIcon)
        val confirmPasswordIcon = findViewById<ImageView>(R.id.confirmPasswordIcon)

        val signUpButton = findViewById<AppCompatButton>(R.id.signUpButton)

        val signInButton = findViewById<TextView>(R.id.signInButton)
    }
}