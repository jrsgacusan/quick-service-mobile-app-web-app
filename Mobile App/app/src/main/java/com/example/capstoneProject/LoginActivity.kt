package com.example.capstoneProject


import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.capstoneProject.dialogs.ResetPassword
import com.example.capstoneProject.models.User
import com.google.android.gms.tasks.RuntimeExecutionException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class LoginActivity : AppCompatActivity() {

    lateinit var emailEditText: EditText
    lateinit var passwordEditText: EditText
    lateinit var logInBtn: Button

    companion object {
        var currentUser: User? =  null
    }


    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        //Map the views from the layout file
        logInBtn = findViewById<Button>(R.id.logInBtn)
        val signUpTextView = findViewById<TextView>(R.id.signUpTextView)
        val forgotPass = findViewById<TextView>(R.id.forgotPass)
        emailEditText = findViewById(R.id.firstNameEditText_profileSettings)
        passwordEditText = findViewById(R.id.passwordEditText)
        //Log in button
        logInBtn.setOnClickListener {
            doLogin()
        }
        //Sign up text view that is clickable
        signUpTextView.setOnClickListener {
            numberOrEmail()
        }
        forgotPass.setOnClickListener {
            val dialog = ResetPassword(this)
            dialog.startLoadingAnimation()
        }

        checkIfAnAccountIsLoggedIn()
        buttonAlphaEnabledListener()


    }

    private fun numberOrEmail() {
        val intent = Intent(this, SignUpAcitivity::class.java )
        startActivity(intent)
    }

    private fun buttonAlphaEnabledListener() {
        if (emailEditText.text.toString().isEmpty() || passwordEditText.text.toString().isEmpty()){
            logInBtn.setAlpha(0.4f)
            logInBtn.isEnabled = false
        } else {
            logInBtn.setAlpha(1f)
            logInBtn.isEnabled = true
        }
        emailEditText.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (emailEditText.text.toString().isEmpty() || passwordEditText.text.toString().isEmpty()){
                    logInBtn.setAlpha(0.4f)
                    logInBtn.isEnabled = false
                } else {
                    logInBtn.setAlpha(1f)
                    logInBtn.isEnabled = true
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        passwordEditText.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (emailEditText.text.toString().isEmpty() || passwordEditText.text.toString().isEmpty()){
                    logInBtn.setAlpha(0.4f)
                    logInBtn.isEnabled = false
                } else {
                    logInBtn.setAlpha(1f)
                    logInBtn.isEnabled = true
                }
            }
            override fun afterTextChanged(s: Editable?) {

            }

        })

    }

    private fun checkIfAnAccountIsLoggedIn() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null){
        } else {
            updateUI(user)
        }
    }

    //Updates the UI if there is an account that is logged in.
    private fun updateUI (currentUser: FirebaseUser?){
        if (currentUser!= null) {
            if(currentUser.isEmailVerified){
                val intent = Intent(this, BuyerActivity::class.java )
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                Toast.makeText(baseContext, "Signed in",
                    Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(baseContext, "Please verify your email address.",
                    Toast.LENGTH_LONG).show()
                FirebaseAuth.getInstance().signOut()
            }
        } else {
            Toast.makeText(baseContext, "Incorrect email or password.",
                Toast.LENGTH_LONG).show()
        }
    }
    //log in function. Checks first if the views are filled. If yes, check first if the account info is correct. If correct, checks if it is verified.
    //If everything is okay, call the Update UI function to finally go to the next activity.
    fun doLogin(){
        if (emailEditText.text.toString().isEmpty()){
            emailEditText.error = "Please enter email"
            emailEditText.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailEditText.text.toString()).matches()){
            emailEditText.error = "Please enter valid email"
            emailEditText.requestFocus()
            return
        }

        if (passwordEditText.text.toString().isEmpty()){
            passwordEditText.error = "Please enter password"
            passwordEditText.requestFocus()
            return
        }

        auth.signInWithEmailAndPassword(emailEditText.text.toString(), passwordEditText.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    updateUI(null)
                }
            }
    }
}