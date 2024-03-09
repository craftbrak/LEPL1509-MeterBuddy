package ucl.student.meterbuddy

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.text.TextWatcher as TextWatcher1

class OTPVerification : AppCompatActivity() {

    private val textWatcher = object : TextWatcher1 {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // TODO()
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // TODO()
        }

        override fun afterTextChanged(s: Editable?) {
            if (s?.length!! > 0) {
                if (selectedPosition == 0) {
                    selectedPosition = 1
                    showKeyboard(OTP2)
                }
                else if (selectedPosition == 1) {
                    selectedPosition = 2
                    showKeyboard(OTP3)
                }
                else if (selectedPosition == 2) {
                    selectedPosition = 3
                    showKeyboard(OTP4)
                }
            }
        }
    }

    var resendButton = findViewById<TextView>(R.id.resendCodeButton)

    var OTP1 = findViewById<EditText>(R.id.OTP_1)
    var OTP2 = findViewById<EditText>(R.id.OTP_2)
    var OTP3 = findViewById<EditText>(R.id.OTP_3)
    var OTP4 = findViewById<EditText>(R.id.OTP_4)


    private var resendEnabled = false
    private val resendTime = 60
    private var selectedPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_otp_verification)

        val OTP_Email = findViewById<TextView>(R.id.OTP_Email)
        val OTP_Mobile = findViewById<TextView>(R.id.OTP_Mobile)

        val verifyButton = findViewById<Button>(R.id.verifyButton)

        // Get extras from Register activity through intent
        val email = intent.getStringExtra("email")
        val mobile = intent.getStringExtra("mobile")

        // Set email and mobile to TextView
        OTP_Email.text = email
        OTP_Mobile.text = mobile

        OTP1.addTextChangedListener(textWatcher)
        OTP2.addTextChangedListener(textWatcher)
        OTP3.addTextChangedListener(textWatcher)
        OTP4.addTextChangedListener(textWatcher)

        // By default, open keyboard at OTP1
        showKeyboard(OTP1)

        // Start resend code timer
        startCountDownTimer()

        resendButton.setOnClickListener {
            // TODO()
            if (resendEnabled) {
                startCountDownTimer()

            } else {

            }
        }

        verifyButton.setOnClickListener {
            val generateOTP = OTP1.text.toString() + OTP2.text.toString() + OTP3.text.toString() + OTP4.text.toString()
            if (generateOTP.length == 4) {
                // TODO (Handle verification code)
            }
        }
    }

    private fun startCountDownTimer() {

        resendEnabled = false
        resendButton.setTextColor(Color.parseColor("#99000000"))

        val timer = object: CountDownTimer(1000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                resendButton.text = "Resend code (" + (millisUntilFinished / 1000) + ")"
            }

            @SuppressLint("ResourceAsColor")
            override fun onFinish() {
                resendEnabled = true
                resendButton.text = "Resend code"
                resendButton.setTextColor(R.color.primary)
            }
        }
        timer.start()

    }

    private fun showKeyboard(otp: EditText) {
        otp.requestFocus()
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(otp, InputMethodManager.SHOW_IMPLICIT)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {

        if (keyCode == KeyEvent.KEYCODE_DEL) {
            if (selectedPosition == 3) {
                selectedPosition = 2
                showKeyboard(OTP3)
            }
            else if (selectedPosition == 2) {
                selectedPosition = 1
                showKeyboard(OTP2)
            }
            else if (selectedPosition == 1) {
                selectedPosition = 0
                showKeyboard(OTP1)
            }

            return true
        }
        return super.onKeyUp(keyCode, event)
    }
}