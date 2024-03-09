package ucl.student.meterbuddy

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class OTPVerification : AppCompatActivity() {

    // TODO ( Implement the logic )
    private var resendEnabled = false
    private val resendTime = 60

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_otp_verification)

        var OTP1 = findViewById<EditText>(R.id.OTP_1)
        var OTP2 = findViewById<EditText>(R.id.OTP_2)
        var OTP3 = findViewById<EditText>(R.id.OTP_3)
        var OTP4 = findViewById<EditText>(R.id.OTP_4)

        var resendButton = findViewById<TextView>(R.id.resendCodeButton)
        val OTP_Email = findViewById<TextView>(R.id.OTP_Email)
        val OTP_Mobile = findViewById<TextView>(R.id.OTP_Mobile)
    }
}