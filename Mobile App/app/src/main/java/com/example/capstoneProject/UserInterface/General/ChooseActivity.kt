package com.example.capstoneProject.UserInterface.General

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.capstoneProject.Models.User
import com.example.capstoneProject.R
import com.example.capstoneProject.UserInterface.Buyer.BuyerActivity
import com.example.capstoneProject.UserInterface.Buyer.bottomNavigationBuyer
import com.example.capstoneProject.UserInterface.Dialogs.VerifyDialog
import com.example.capstoneProject.UserInterface.ServiceProvider.SellerActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChooseActivity : AppCompatActivity() {

    private lateinit var clientBtn: Button
    private lateinit var spBtn: Button
    private lateinit var logoutBtn: ImageButton

    companion object {
        const val CLIENT_VERIFICATION = "CLIENTMODECODE"
        const val SP_VERIFICATION = "NONONONONONO"
        const val TAG = "ThisIsTheTag"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose)

        clientBtn = findViewById(R.id.clientButton)
        spBtn = findViewById(R.id.spButton)
        logoutBtn = findViewById(R.id.logoutAgad)

        logoutBtn.setOnClickListener {
            showLogoutDialog()
        }
        clientBtn.setOnClickListener {
            //Go to buyer's activity
            checkIfVerifiedClient()
        }

        spBtn.setOnClickListener {
            //Go to Service Provider Screen
            checkIfVerifiedSP()

        }
        fetchCurrentUser()

    }

    private fun fetchCurrentUser() {
        if (FirebaseAuth.getInstance().currentUser != null) {
            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    BuyerActivity.currentUser = snapshot.getValue(User::class.java)
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
        }

    }

    private fun checkIfVerifiedClient() {
        val currentUserUid = FirebaseAuth.getInstance().currentUser!!.uid
        if (currentUserUid != null) {
            val ref = FirebaseDatabase.getInstance().getReference("users/$currentUserUid")
            ref.addListenerForSingleValueEvent(
                    object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val user = snapshot.getValue(User::class.java)
                            if (user?.verifiedClient == "NOT_VERIFIED") {
                                val dialog = VerifyDialog(this@ChooseActivity, CLIENT_VERIFICATION)
                                dialog.showDialog()
                            } else if (user?.verifiedClient == "VERIFIED") {
                                //Go to the Service Provider Activity.
                                val intent = Intent(this@ChooseActivity, BuyerActivity::class.java)
                                startActivity(intent)
                            } else if (user?.verifiedClient == "PENDING") {
                                val snackbar = Snackbar.make(clientBtn, "Replace requirements sent.", Snackbar.LENGTH_LONG)
                                snackbar.setAction("REPLACE", View.OnClickListener {
                                    snackbar.dismiss()
                                    val intent = Intent(this@ChooseActivity, SendRequirementActivity::class.java)
                                    intent.putExtra(TAG, CLIENT_VERIFICATION)
                                    startActivity(intent)
                                })
                                snackbar.show()
                            } else if (user?.verifiedServiceProvider == "TRY_AGAIN") {
                                val snackbar = Snackbar.make(clientBtn, "Your request for verification wasn't approved by the Admin.", Snackbar.LENGTH_LONG)
                                snackbar.setAction("RESEND APPLICATION", View.OnClickListener {
                                    snackbar.dismiss()
                                    val intent = Intent(this@ChooseActivity, SendRequirementActivity::class.java)
                                    intent.putExtra(TAG, CLIENT_VERIFICATION)
                                    startActivity(intent)

                                })
                                snackbar.show()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                        }

                    })
        }
    }

    private fun checkIfVerifiedSP() {
        val currentUserUid = FirebaseAuth.getInstance().currentUser!!.uid
        if (currentUserUid != null) {
            val ref = FirebaseDatabase.getInstance().getReference("users/$currentUserUid")
            ref.addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user = snapshot.getValue(User::class.java)
                        if (user?.verifiedServiceProvider == "NOT_VERIFIED") {
                            val dialog = VerifyDialog(this@ChooseActivity, SP_VERIFICATION)
                            dialog.showDialog()
                        } else if (user?.verifiedServiceProvider == "VERIFIED") {
                            //Go to the Service Provider Activity.
                            val intent = Intent(this@ChooseActivity, SellerActivity::class.java)
                            startActivity(intent)
                        } else if (user?.verifiedServiceProvider == "PENDING") {
                            val snackbar = Snackbar.make(clientBtn, "Replace requirements sent.", Snackbar.LENGTH_LONG)
                            snackbar.setAction("REPLACE", View.OnClickListener {
                                snackbar.dismiss()
                                val intent = Intent(this@ChooseActivity, SendRequirementActivity::class.java)
                                intent.putExtra(TAG, SP_VERIFICATION)
                                startActivity(intent)
                            })
                            snackbar.show()
                        } else if (user?.verifiedServiceProvider == "TRY_AGAIN") {
                            val snackbar = Snackbar.make(clientBtn, "Your request for verification wasn't approved by the Admin.", Snackbar.LENGTH_LONG)
                            snackbar.setAction("RESEND APPLICATION", View.OnClickListener {
                                snackbar.dismiss()
                                val intent = Intent(this@ChooseActivity, SendRequirementActivity::class.java)
                                intent.putExtra(TAG, SP_VERIFICATION)
                                startActivity(intent)

                            })
                            snackbar.show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })
        }
    }

    private fun showLogoutDialog() {
        //Dialog before sign out
        val dialogBuilder = AlertDialog.Builder(this)
        // set message of alert dialog
        dialogBuilder.setMessage("Do you want to sign out?")
            // if the dialog is cancelable
            .setCancelable(true)
            // positive button text and action
            .setPositiveButton("Proceed", DialogInterface.OnClickListener { _, _ ->
                var auth = FirebaseAuth.getInstance()
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                BuyerActivity.currentUser = null
                finish()
                Toast.makeText(this, "Signed out", Toast.LENGTH_LONG).show()
            })
            // negative button text and action
            .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, _ ->
                dialog.cancel()
            })
        // create dialog box
        val alert = dialogBuilder.create()
        // set title for alert dialog box
        alert.setTitle("Sign Out")
        // show alert dialog
        alert.show()
    }
}

