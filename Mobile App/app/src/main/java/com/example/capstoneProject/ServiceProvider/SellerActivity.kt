package com.example.capstoneProject.ServiceProvider

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.capstoneProject.Buyer.BuyerActivity
import com.example.capstoneProject.LoginActivity
import com.example.capstoneProject.Messages.MessagesActivity
import com.example.capstoneProject.Models.Order
import com.example.capstoneProject.ProfileSettingsActivity
import com.example.capstoneProject.R
import com.example.capstoneProject.ServiceProvider.seller_activities.CreateServicesActivity
import com.example.capstoneProject.ServiceProvider.seller_fragments.SellerManageFragment
import com.example.capstoneProject.ServiceProvider.seller_fragments.SellerNotificationsFragment
import com.example.capstoneProject.ServiceProvider.seller_fragments.SellerProfileFragment
import com.example.capstoneProject.ServiceProvider.seller_fragments.SellerServicesFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

lateinit var bottomNavigationSeller : BottomNavigationView
class SellerActivity : AppCompatActivity() {

    private val sellerManage = SellerManageFragment()
    private val sellerNotifications = SellerNotificationsFragment()
    private val sellerProfile = SellerProfileFragment()
    private val sellerServices = SellerServicesFragment()
    lateinit var menuItem: MenuItem
    lateinit var menuToHide: MenuItem





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seller)




        val toolBar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolBar)




        //Action bar at the top
        setSupportActionBar(toolBar)
        //supportActionBar!!.setDisplayShowTitleEnabled(false) //Removes the title

        //Initialize Profile page of the seller fragment
        makeCurrentFragment(SellerProfileFragment())

        //Map the bottom navigation view
        bottomNavigationSeller = findViewById(R.id.bottomNavigation)
        //Onclick listener for the bottom nav, bottom navigation menu have a corresponding id.
        bottomNavigationSeller.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.Seller_servicesPage -> {
                    makeCurrentFragment(sellerServices)
                }
                R.id.Seller_manageOrdersPage -> {
                    makeCurrentFragment(sellerManage)
                }
                R.id.Seller_notificationsPage -> {
                    makeCurrentFragment(sellerNotifications)
                }
                R.id.Seller_profilePage -> {
                    makeCurrentFragment(sellerProfile)
                }
            }
            true
        }

        fetchTotalJobsCompleted()

    }

    private fun fetchTotalJobsCompleted() {
        val currentUserUid = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = FirebaseDatabase.getInstance().getReference("booked_to/$currentUserUid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var totalCount: Int = 0
                snapshot.children.forEach {bookings ->
                    val order = bookings.getValue(Order::class.java)!!
                    if (order.status == "COMPLETED") {
                        totalCount += 1
                    }
                }
                val userSellerInfoRef = FirebaseDatabase.getInstance().getReference("user_seller_info/$currentUserUid")
                userSellerInfoRef.child("totalJobsFinished").setValue(totalCount)
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        menuItem = menu!!.findItem(R.id.search)
        menuToHide = menu.findItem(R.id.addService)
        menuItem.isVisible = false
        menuToHide.isVisible = false


        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.message -> {
                val intent = Intent(this, MessagesActivity::class.java)
                startActivity(intent)

            }
            R.id.addService -> {
                val intent = Intent(this, CreateServicesActivity::class.java)
                startActivity(intent)
            }
            R.id.profileSettings -> {
                val intent = Intent(this, ProfileSettingsActivity::class.java)
                intent.putExtra("intent", "seller")
                startActivity(intent)

            }
            R.id.logOut -> {
                //Dialog before sign out
                val dialogBuilder = AlertDialog.Builder(this)
                // set message of alert dialog
                dialogBuilder.setMessage("Do you want to sign out?")
                        // if the dialog is cancelable
                        .setCancelable(true)
                        // positive button text and action
                        .setPositiveButton("Proceed") { _, _ ->
                            FirebaseAuth.getInstance().signOut()
                            val intent = Intent(this, LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            BuyerActivity.currentUser = null
                            finish()
                            Toast.makeText(this, "Signed out", Toast.LENGTH_LONG).show()
                        }
                        // negative button text and action
                        .setNegativeButton("Cancel") { dialog, _ ->
                            dialog.cancel()
                        }
                // create dialog box
                val alert = dialogBuilder.create()
                // set title for alert dialog box
                alert.setTitle("Sign Out")
                // show alert dialog
                alert.show()


            }

        }
        return super.onOptionsItemSelected(item)
    }


    private fun makeCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply{
            replace(R.id.wrapper, fragment)
            commit()
        }

    fun setActionBarTitle(title: String?) {
        supportActionBar!!.title = title
    }





}