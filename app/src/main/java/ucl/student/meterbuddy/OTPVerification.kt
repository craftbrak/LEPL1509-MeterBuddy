package ucl.student.meterbuddy

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.appcompat.widget.AppCompatButton

class OTPVerification : ComponentActivity() {

    lateinit var otp1: EditText
    lateinit var otp2: EditText
    lateinit var otp3: EditText
    lateinit var otp4: EditText

    lateinit var resendButton: TextView

    var selectedPosition: Int = 0
    var resendEnabled: Boolean = false
    val resendTime: Int = 60

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_verification)

        resendButton = findViewById(R.id.resendCodeButton)

        otp1 = findViewById(R.id.OTP_1)
        otp2 = findViewById(R.id.OTP_2)
        otp3 = findViewById(R.id.OTP_3)
        otp4 = findViewById(R.id.OTP_4)

        val otpEmail = findViewById<TextView>(R.id.OTP_Email)
        val otpMobile = findViewById<TextView>(R.id.OTP_Mobile)

        val verifyButton = findViewById<AppCompatButton>(R.id.verifyButton)

        // Get extras from Register activity through intent
        val email = intent.getStringExtra("email")
        val mobile = intent.getStringExtra("mobile")

        // Set email and mobile to TextView
        otpEmail.text = email
        otpMobile.text = mobile

        // By default, open keyboard at OTP1
        showKeyboard(otp1)

        // Start resend code timer
        startCountDownTimer()

        resendButton.setOnClickListener {
            // TODO()
            if (resendEnabled) {
                startCountDownTimer()
            }
            // else { }
        }

        verifyButton.setOnClickListener {
            val generateOTP = otp1.text.toString() + otp2.text.toString() + otp3.text.toString() + otp4.text.toString()
            // if (generateOTP.length == 4) {
            // TODO (Handle verification code)
            // }
        }
        otp1.addTextChangedListener(textWatcher)
        otp2.addTextChangedListener(textWatcher)
        otp3.addTextChangedListener(textWatcher)
        otp4.addTextChangedListener(textWatcher)
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
                showKeyboard(otp3)
            }
            else if (selectedPosition == 2) {
                selectedPosition = 1
                showKeyboard(otp2)
            }
            else if (selectedPosition == 1) {
                selectedPosition = 0
                showKeyboard(otp1)
            }
            return true
        }
        return super.onKeyUp(keyCode, event)
    }

    private val textWatcher = object : TextWatcher {

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
                    showKeyboard(otp2)
                }
                else if (selectedPosition == 1) {
                    selectedPosition = 2
                    showKeyboard(otp3)
                }
                else if (selectedPosition == 2) {
                    selectedPosition = 3
                    showKeyboard(otp4)
                }
            }
        }
    }
}