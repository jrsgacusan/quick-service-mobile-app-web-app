package com.example.capstoneProject.GroupieViews

import android.widget.ImageView
import android.widget.TextView
import com.example.capstoneProject.Models.User
import com.example.capstoneProject.Models.UserReview
import com.example.capstoneProject.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder

class ReviewItem(val userReview: UserReview) : Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        val name = viewHolder.itemView.findViewById<TextView>(R.id.nameTextView_reviewsRow)!!
        val rating = viewHolder.itemView.findViewById<TextView>(R.id.ratingTextView_reviewsRow)!!
        val review = viewHolder.itemView.findViewById<TextView>(R.id.reviewTextView_reviewsRow)!!
        val image = viewHolder.itemView.findViewById<ImageView>(R.id.userImage_serviceRow)!!
        val ref = FirebaseDatabase.getInstance().getReference("users/${userReview.userUid}")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val userData = snapshot.getValue(User::class.java)
                Picasso.get().load(userData!!.profileImageUrl).into(image)
                name.text = "${userData.firstName} ${userData.lastName}"
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        rating.text = userReview.rating.toString()
        review.text = userReview.review
    }

    override fun getLayout(): Int {
        return R.layout.row_reviews
    }

}
