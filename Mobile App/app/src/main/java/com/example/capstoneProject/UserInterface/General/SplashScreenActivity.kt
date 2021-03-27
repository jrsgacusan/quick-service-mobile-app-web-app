package com.example.capstoneProject.UserInterface.General

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.capstoneProject.UserInterface.Buyer.BuyerActivity
import com.example.capstoneProject.R
import com.google.firebase.auth.FirebaseAuth

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        var imageView = findViewById<ImageView>(R.id.iv_note)

        imageView.alpha = 0f
        imageView.animate().setDuration(5000).alpha(1f).withEndAction {
            val currentUser = FirebaseAuth.getInstance().currentUser
            //no user logged in
            if (currentUser == null) {
                val i = Intent(this, LoginActivity::class.java)
                startActivity(i)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
                // THere is a logged in user
            } else {
                val i = Intent(this, ChooseActivity::class.java)
                startActivity(i)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }

        }


    }
}