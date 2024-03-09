package ucl.student.meterbuddy

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class Login : AppCompatActivity() {

    // TODO ( Implement the logic )
    private var passwordIsShown = false

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)

        val signUpButton = findViewById<TextView>(R.id.signUpButton)

        val passwordIcon = findViewById<ImageView>(R.id.passwordIcon)
    }
}