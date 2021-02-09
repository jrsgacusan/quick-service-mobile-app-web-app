package com.example.capstoneProject


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.capstoneProject.Models.User
import com.example.capstoneProject.Models.UserReview
import com.example.capstoneProject.GroupieViews.ReviewItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder

class DisplayReviewsActivity : AppCompatActivity() {
    lateinit var reviewsRecyclerView: RecyclerView
    lateinit var userUid: String
    lateinit var toolBar: Toolbar
    val adapter = GroupAdapter<ViewHolder>()
    lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_reviews)
        //user ID of the user being reviewed. It will be used to filter every reviews, and only get those whose userID is the same with this.
        userUid = intent.getStringExtra("userUid")!!
        //Get the name of the user and put it at the Toolbar title
        getUserNameUsingUserUid()
        //Map everything here
        toolBar = findViewById(R.id.toolbar_activityDisplayReviews)
        textView = findViewById(R.id.textView_activityDisplayReviews)
        toolBar.setNavigationOnClickListener {
            finish()
        }
        reviewsRecyclerView = findViewById(R.id.reviewsRecyclerView_activityDisplayReviews)

        fetchReviews(userUid)


    }

    private fun showOrHide(count: Int) {
        if (count == 0) {
            textView.isVisible = true
        } else {
            textView.isVisible = false
        }
    }

    private fun fetchReviews(uid: String) {
        val reviewsRef = FirebaseDatabase.getInstance().getReference("reviews/$uid")
        reviewsRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach{ review ->
                    val userReview = review.getValue(UserReview::class.java)!!
                    adapter.add(ReviewItem(userReview))
                }
                showOrHide(adapter.itemCount)
                reviewsRecyclerView.adapter = adapter

            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

    }

    private fun getUserNameUsingUserUid() {
        val ref = FirebaseDatabase.getInstance().getReference("/users/$userUid")
        ref.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                toolBar.setTitle("${user!!.firstName} ${user.lastName}")
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
    


}

