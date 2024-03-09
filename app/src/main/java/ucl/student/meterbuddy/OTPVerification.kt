package ucl.student.meterbuddy

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
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
    lateinit var otpMobile: TextView
    lateinit var otpEmail: TextView

    var selectedPosition: Int = 0
    var resendEnabled: Boolean = false
    val resendTime: Int = 20

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_verification)

        resendButton = findViewById(R.id.resendCodeButton)
        otp1 = findViewById(R.id.OTP_1)
        otp2 = findViewById(R.id.OTP_2)
        otp3 = findViewById(R.id.OTP_3)
        otp4 = findViewById(R.id.OTP_4)

        otpEmail = findViewById(R.id.OTP_Email)
        otpMobile = findViewById(R.id.OTP_Mobile)

        val verifyButton = findViewById<AppCompatButton>(R.id.verifyButton)

        // Get extras from Register activity through intent
        val email = intent.getStringExtra("email")
        val mobile = intent.getStringExtra("mobile")
        val password = intent.getStringExtra("password")
        val fullName = intent.getStringExtra("fullName")
        val username = intent.getStringExtra("username")

        // Set email and mobile to TextView
        otpEmail.text = email
        otpMobile.text = mobile

        // By default, open keyboard at OTP1
        showKeyboard(otp1)

        // Send code
        var currOTPCode = sendCode()

        // Start resend code timer
        startCountDownTimer()

        resendButton.setOnClickListener {
            // Resend the Code
            sendCode()
            if (resendEnabled) { startCountDownTimer() }
        }

        verifyButton.setOnClickListener {
            val generateOTP_ByUser = otp1.text.toString() + otp2.text.toString() + otp3.text.toString() + otp4.text.toString()

            // Condition : if ((generateOTP_ByUser.length == 4) and (generateOTP_ByUser.toInt() == currOTPCode)) {
            if (generateOTP_ByUser.length == 4) {
                // TODO ( What to do after that ? )
                val mobileTxt = otpMobile.toString()
                val emailTxt = otpEmail.toString()
                val usernameTxt = username.toString()
                val fullNameTxt = fullName.toString()
                val passwordTxt = password.toString()

                // TODO ( Put all these informations into the Database )

                val intent = Intent(this, MainActivity::class.java)
                // Finish all previous activities
                finishAffinity()
                startActivity(intent)
            }
            else {
                // TODO ( Display an error message )
            }
        }
        otp1.addTextChangedListener(textWatcher)
        otp2.addTextChangedListener(textWatcher)
        otp3.addTextChangedListener(textWatcher)
        otp4.addTextChangedListener(textWatcher)
    }

    private fun sendCode(): Int {
        // TODO (Implement this function)
        // Resend code to otpEmail
        val generatedOTPCode = 9999
        return generatedOTPCode
    }

    private fun startCountDownTimer() {

        resendEnabled = false
        resendButton.setTextColor(Color.parseColor("#99000000"))

        val timer = object: CountDownTimer((resendTime * 1000).toLong(), 1000) {

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
        // Show where the user need to click
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
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
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