@file:Suppress("DEPRECATION")

package com.example.capstoneProject.buyer_activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.ceylonlabs.imageviewpopup.ImagePopup
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.example.capstoneProject.DisplayReviewsActivity
import com.example.capstoneProject.R
import com.example.capstoneProject.messages_activities.ChatLogActivity
import com.example.capstoneProject.models.Service
import com.example.capstoneProject.models.User
import com.example.capstoneProject.models.UserSellerInfo
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import org.w3c.dom.Text
import java.lang.ArithmeticException


class DisplaySpecificServiceActivity : AppCompatActivity() {
    private lateinit var toolBar: Toolbar

    private lateinit var userUid: String
    private lateinit var serviceUid: String
    //user -> this is for the first card view where all informations are from the model User
    private lateinit var profileImageView: ImageView
    private lateinit var nameTextView: TextView

    //for the service details
    private lateinit var serviceTitleTextView: TextView
    private lateinit var serviceDescriptionTextView: TextView
    private lateinit var serviceCategoryTextView: TextView
    private lateinit var priceTextView: TextView
    private lateinit var createOrderButton: Button

    //Uer review buttoon
    private lateinit var userReviewButton: Button

    //floating action button
    private lateinit var floatingActionButton: FloatingActionButton

    //showProfileFrafment
    private lateinit var showProfileImageBtn: ImageButton
    private lateinit var service: Service

    //
    private lateinit var userRating: TextView
    private lateinit var totalJobs: TextView

    companion object {
        var serviceToBeOrdered: Service? = null
        var userUidForFragment: String? = null
        var viewOnlyMode: Boolean = false
    }





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_specific_service)
        //intents
        service = intent.getParcelableExtra<Service>("service")!!
        serviceToBeOrdered = service
        userUid = service!!.userUid!!
        serviceUid = service.uid!!
        //Map everything here
        userReviewButton = findViewById(R.id.viewReviewButton_activityDisplaySpecificService)
        toolBar = findViewById(R.id.toolBar_displaySpecificService)
        showProfileImageBtn = findViewById(R.id.showFragment_activityDIsplaySpecificService)
        serviceTitleTextView = findViewById(R.id.serviceTitle_activityDisplaySpecificService)
        serviceDescriptionTextView = findViewById(R.id.serviceDescription_activityDisplaySpecificService)
        serviceCategoryTextView = findViewById(R.id.serviceCategory_dispalySpecificService )
        priceTextView = findViewById(R.id.price_activityDisplaySpecificService)
        createOrderButton = findViewById(R.id.createOrderButton_activityDisplaySpecificService)
        profileImageView = findViewById(R.id.profileImageView_acitivityDisplaySpecificActivity)
        nameTextView = findViewById(R.id.name_activityDisplaySpecificService)
        floatingActionButton = findViewById(R.id.messageSellerFloatingActionButton_activityDisplaySpecificService)
        userRating = findViewById(R.id.userRating_acitivityDisplaySpecificService)
        totalJobs= findViewById(R.id.jobsAccomplished_activityDisplaySpecificService)


        userReviewButton.setOnClickListener {
            val intent = Intent(this, DisplayReviewsActivity::class.java)
            intent.putExtra("userUid", userUid )
            startActivity(intent)
        }
        toolBar.setNavigationOnClickListener {
            finish()
        }
        floatingActionButton.setOnClickListener{
            goToChatLogActivity()
        }
        createOrderButton.setOnClickListener {
            var bottomFragment = BottomFragmentCreateOrder()
            bottomFragment.show(supportFragmentManager, "TAG")
        }
        //show Profile infos
        showProfileImageBtn.setOnClickListener{
            userUidForFragment = userUid
            val profileFragment = BottomFragmentShowProfile()
            profileFragment.show(supportFragmentManager, "TAG")

        }
        fetchUserData()
        fetchService()
        imageSlider()
        checkIfViewMode()
        showUserReviewRatingAndJobsFinished()



    }

    private fun showUserReviewRatingAndJobsFinished() {
        val servicePrivderUid = service.userUid!!
        val ref = FirebaseDatabase.getInstance().getReference("user_seller_info/$servicePrivderUid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userData = snapshot.getValue(UserSellerInfo::class.java)!!
                if (userData.count ==0) {
                    userRating.text = "No ratings yet."
                } else {
                    val ttlrtng = userData.totalRating!!.toDouble()
                    val ttlcnt = userData.count!!.toDouble()
                    val rating = (ttlrtng/ttlcnt)
                    val solution = Math.round(rating * 10.0) / 10.0
                    userRating.text = "$solution/5"
                }
                totalJobs.text = "Jobs Completed: ${userData.totalJobsFinished}"

            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun checkIfViewMode() {
        if (viewOnlyMode) {
            createOrderButton.isEnabled = false
            floatingActionButton.isEnabled = false
            val snackbar = Snackbar.make(findViewById(R.id.container_activityDisplaySpecificService), "View-Only Mode", Snackbar.LENGTH_INDEFINITE)
            snackbar.show()

            return
        }
        if (!viewOnlyMode) {
            createOrderButton.isEnabled = true
            floatingActionButton.isEnabled = true
        }
    }

    private fun imageSlider() {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        val imageSlider: ImageSlider = findViewById(R.id.imageSlider_activityDisplaySpecificService)
        val remoteImages: ArrayList<SlideModel> =  ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("/Sliders/$serviceUid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach{
                    remoteImages.add( SlideModel(it.child("url").value.toString(),"",ScaleTypes.CENTER_CROP))
                }
                imageSlider.setImageList(remoteImages, ScaleTypes.CENTER_CROP)
                imageSlider.stopSliding()
                imageSlider.setItemClickListener(object : ItemClickListener{
                    override fun onItemSelected(position: Int) {
//                        var showPictureFragment = ShowPictureFragment()
//                        showPictureFragment.show(supportFragmentManager, "TAG")
//                        ShowPictureFragment.serviceUid = serviceUid
                        Log.d("POSITIONTRYLANG", "$position")
                        val url = remoteImages[position].imageUrl!!
                        val imagePopup = ImagePopup(this@DisplaySpecificServiceActivity)
                        imagePopup.initiatePopupWithPicasso(url)
                        imagePopup.viewPopup()
                    }

                })
            }
            override fun onCancelled(error: DatabaseError) {

            }

        })
        imageSlider.stopSliding()

    }

    private fun goToChatLogActivity() {
        val intent = Intent (applicationContext, ChatLogActivity::class.java)
        val ref = FirebaseDatabase.getInstance().getReference("/users/$userUid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                intent.putExtra("user", user)
                startActivity(intent)
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }


    private fun fetchService() {
        val ref = FirebaseDatabase.getInstance().getReference("/services/${userUid}/${serviceUid}")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val service = p0.getValue(Service::class.java)
                if (service != null) {
                    serviceTitleTextView.text = "${service.title?.toUpperCase()}"
                    serviceDescriptionTextView.text = "${service.description}"
                    serviceCategoryTextView.text = "Category: ${service.category}"
                    priceTextView.text = "₱${service.price}"
                    createOrderButton.text = createOrderButton.text.toString() + " (PHP${service.price})"
                }

            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }





    private fun fetchUserData() {
        val ref = FirebaseDatabase.getInstance().getReference("/users/${userUid}")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val user = p0.getValue(User::class.java)
                //Picasso is a caching manager for the image
                Picasso.get().load(user!!.profileImageUrl).into(profileImageView)
                nameTextView.text = "${user.firstName.toString()} ${user.lastName.toString()}, ${user.age}"
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }
}
