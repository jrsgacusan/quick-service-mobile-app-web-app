package com.example.capstoneProject.UserInterface.Dialogs

import android.app.Activity
import android.app.AlertDialog
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.capstoneProject.R
import com.google.firebase.auth.FirebaseAuth

class ResetPassword(activity: Activity) {
    private val activity: Activity? = activity
    private var dialog: AlertDialog? = null


    fun startLoadingAnimation() {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity!!.layoutInflater
        val v = inflater.inflate(R.layout.dialog_reset_password, null)!!
        builder.setView(v)
        builder.setCancelable(true)

        dialog = builder.create()
        dialog!!.show()


        val buttonButton = v.findViewById<Button>(R.id.button_reset)!!
        val emailEditText = v.findViewById<EditText>(R.id.email_Reset)!!


        buttonButton.setOnClickListener {
            if (emailEditText.text.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailEditText.text.toString()).matches()) {
                emailEditText.requestFocus()
                emailEditText.error = "Invalid Email"
            } else {
                var emailAddress = emailEditText.text.toString()
                FirebaseAuth.getInstance().sendPasswordResetEmail(emailAddress)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d("TAG", "Email sent.")
                                dismissDialog()
                            } else {
                                Toast.makeText(activity, "${task.exception!!.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
            }
        }

    }


    fun dismissDialog() {
        dialog!!.dismiss()
    }

}