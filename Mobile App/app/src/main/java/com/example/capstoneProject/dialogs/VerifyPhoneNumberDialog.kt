package com.example.capstoneProject.dialogs

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.example.capstoneProject.BuyerActivity
import com.example.capstoneProject.R
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import java.security.AuthProvider
import java.util.concurrent.TimeUnit

class VerifyPhoneNumberDialog (activity: Activity) {
    private val activity: Activity? = activity
    private var dialog: AlertDialog? = null
    private lateinit var progressBar: ProgressBar
    private lateinit var codeEditText: EditText
    private lateinit var numberEditText: EditText
    private lateinit var sendCode: Button
    private lateinit var verifyBtn : Button

    companion object{
        const val TAG = "randomTag"
        var verificationCode = ""
    }

    fun showVerificationBox(){
        val builder = AlertDialog.Builder(activity)
        val inflater = activity!!.layoutInflater
        val view  = inflater.inflate(R.layout.dialog_verify_phone_number, null)
        builder.setView(view)
        builder.setCancelable(false)
        dialog = builder.create()
        dialog!!.show()

        //Map here
        progressBar= view.findViewById(R.id.progressBar_number)!!
        numberEditText = view.findViewById(R.id.number)
        sendCode = view.findViewById(R.id.sendCode)
        codeEditText = view.findViewById<EditText>(R.id.editText_number)!!
        verifyBtn = view.findViewById<Button>(R.id.verifyButton_number)!!
        //variables
        val numberOfTheUser = numberEditText.text.toString()

        progressBar.isGone = true



        sendCode.setOnClickListener {
            sendVerificationToUser(numberOfTheUser)
        }

        verifyBtn.setOnClickListener {
            check()
        }


    }

    private fun check() {
        val code = codeEditText.text.toString()



        if (code.isEmpty() || code.length < 6) {
            codeEditText.error = "Invalid"
            codeEditText.requestFocus()
            return
        }
        progressBar.isGone = false
        verifyCode(code)



    }

    private fun sendVerificationToUser(number: String) {

        if (number.length != 12) {
            numberEditText.error = "Follow this format +63999999999"
            numberEditText.requestFocus()
            return
        }



        val auth = FirebaseAuth.getInstance()
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+63$number")       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(activity!!)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onCodeSent(s: String, p1: PhoneAuthProvider.ForceResendingToken) {
            super.onCodeSent(s, p1)

            verificationCode = s

        }

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {

            Log.d(TAG, "onVerificationCompleted:$credential")
            val code = credential.smsCode!!

            if (code != null){
                progressBar.isGone = false
                verifyCode(code)
            }

        }

        override fun onVerificationFailed(e: FirebaseException) {
            Log.w(TAG, "onVerificationFailed", e)
            Toast.makeText(activity, "${e.message}", Toast.LENGTH_SHORT).show()
        }

    }

    private fun verifyCode(codeByUser: String) {
        val credential: PhoneAuthCredential  = PhoneAuthProvider.getCredential(verificationCode, codeByUser )
        signInTheUser(credential)


    }

    private fun signInTheUser(credential: PhoneAuthCredential) {
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { authResult ->
                if (authResult.isSuccessful) {
                    val intent = Intent(activity, BuyerActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    activity!!.startActivity(intent)

                } else {
                    Toast.makeText(activity, "${authResult.exception!!.message}", Toast.LENGTH_LONG).show()
                }

            }
    }

    fun dismissDialog() {
        dialog!!.dismiss()
    }



}