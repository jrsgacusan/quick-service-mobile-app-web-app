@file:Suppress("DEPRECATION")

package com.example.capstoneProject.UserInterface.Buyer

import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.capstoneProject.BackgroundProcess.NotificationsWorker
import com.example.capstoneProject.UserInterface.Buyer.BuyerActivities.BottomFragmentCreateOrder
import com.example.capstoneProject.UserInterface.Buyer.BuyerFragments.BuyerManageFragment
import com.example.capstoneProject.UserInterface.Buyer.BuyerFragments.HomeFragment
import com.example.capstoneProject.UserInterface.Buyer.BuyerFragments.ProfileFragment
import com.example.capstoneProject.UserInterface.Buyer.BuyerFragments.SearchFragment
import com.example.capstoneProject.UserInterface.General.LoginActivity
import com.example.capstoneProject.UserInterface.Messages.MessagesActivity
import com.example.capstoneProject.Models.User
import com.example.capstoneProject.UserInterface.General.ProfileSettingsActivity
import com.example.capstoneProject.R
import com.example.capstoneProject.UserInterface.General.ChooseActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


lateinit var bottomNavigationBuyer: BottomNavigationView

class BuyerActivity : AppCompatActivity() {

    private val homeFragment = HomeFragment()
    private val buyerManageFragment = BuyerManageFragment()
    private val searchFragment = SearchFragment()
    private val profileFragment = ProfileFragment()
    private lateinit var menuItem: MenuItem


    companion object {
        var currentUser: User? = null
        const val CANCEL_MESSSAGE_NOTIFICATIONS = "cancelmessagesnotifications"
        const val RANDOM_TAG = "Justsomerandometag"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buyer)



        verifyUserIsLoggedIn()


        bottomNavigationBuyer = findViewById(R.id.bottomNavigation)
        setSupportActionBar(findViewById(R.id.toolBar))
        val wrapper = findViewById<FrameLayout>(R.id.wrapper)
        val showOrHideThisView = findViewById<ConstraintLayout>(R.id.networkConnection)

        if (isConnected()) {
            wrapper.isGone = false
            supportActionBar!!.show()
            bottomNavigationBuyer.isGone = false
            showOrHideThisView.isGone = true
        } else {
            wrapper.isGone = true
            supportActionBar!!.hide()
            bottomNavigationBuyer.isGone = true
            showOrHideThisView.isGone = false
            val snackbar = Snackbar.make(wrapper, "Cannot use the app without internet connection.", Snackbar.LENGTH_INDEFINITE)
            snackbar.setAction("SETTINGS") {
                val intent = Intent(Settings.ACTION_SETTINGS)
                startActivity(intent)
            }
            snackbar.show()
        }


        //Initialize home fragment
        makeCurrentFragment(ProfileFragment())
        //Map the bottom navigation view
        bottomNavigationBuyer.menu.findItem(R.id.profilePage).isChecked = true
        //Onclick listener for the bottom nav, bottom navigation menu have a corresponding id.
        bottomNavigationBuyer.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.homePage -> {
                    makeCurrentFragment(homeFragment)
                }
                R.id.search -> {
                    makeCurrentFragment(searchFragment)
                }
                R.id.notifications -> {
                    makeCurrentFragment(buyerManageFragment)
                }
                R.id.profilePage -> {
                    makeCurrentFragment(profileFragment)
                }
            }
            true
        }
        //image button
        val imageButton = findViewById<ImageButton>(R.id.imageButton_activityBuyer)
        imageButton.setOnClickListener {
            if (isConnected()) {
                wrapper.isGone = false
                supportActionBar!!.show()
                bottomNavigationBuyer.isGone = false
                showOrHideThisView.isGone = true
            }
        }

        try {
            val intentString = intent.getStringExtra(BottomFragmentCreateOrder.TAG)!!
            makeCurrentFragment(buyerManageFragment)
        } catch (e: Exception) {

        }

        backgroundService()


    }

    private fun backgroundService() {
        val notificationsWorkRequest = OneTimeWorkRequestBuilder<NotificationsWorker>()
                .build()
        WorkManager
                .getInstance(applicationContext)
                .enqueue(notificationsWorkRequest)

    }





    //Options Menu on the upper right side
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        menuItem = menu!!.findItem(R.id.addService)
        menuItem.isVisible = false
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.message -> {
                val intent = Intent(this, MessagesActivity::class.java)
                startActivity(intent)

            }
            R.id.search -> {
                makeCurrentFragment(searchFragment)
            }
            R.id.profileSettings -> {
                val intent = Intent(this, ProfileSettingsActivity::class.java)
                intent.putExtra("intent", "buyer")
                startActivity(intent)
            }
            R.id.logOut -> {
                showDialogFun()
            }
            R.id.changeMode -> {
                startActivity(Intent(this, ChooseActivity::class.java))
            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun showDialogFun() {
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
                    currentUser = null
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

    //Function that will change the current fragment.
    private fun makeCurrentFragment(fragment: Fragment) =
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.wrapper, fragment)
                commit()
            }

    fun setActionBarTitle(title: String?) {
        supportActionBar!!.title = title
    }

    private fun verifyUserIsLoggedIn() {
        val user = FirebaseAuth.getInstance().currentUser!!
        if (user == null || !user.isEmailVerified) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

    }

    private fun isConnected(): Boolean {
        var connected = false
        try {
            val cm =
                    applicationContext.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val nInfo = cm.activeNetworkInfo
            connected = nInfo != null && nInfo.isAvailable && nInfo.isConnected
            return connected
        } catch (e: Exception) {
            Log.e("Connectivity Exception", e.message!!)
        }
        return connected
    }
}