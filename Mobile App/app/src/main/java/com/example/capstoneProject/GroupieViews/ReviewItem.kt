package com.example.capstoneProject.GroupieViews

import android.widget.ImageView
import android.widget.TextView
import com.example.capstoneProject.R
import com.example.capstoneProject.Models.UserReview
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder

class ReviewItem(val userReview: UserReview): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        val name = viewHolder.itemView.findViewById<TextView>(R.id.nameTextView_reviewsRow)!!
        val rating = viewHolder.itemView.findViewById<TextView>(R.id.ratingTextView_reviewsRow)!!
        val review = viewHolder.itemView.findViewById<TextView>(R.id.reviewTextView_reviewsRow)!!
        val image =  viewHolder.itemView.findViewById<ImageView>(R.id.userImage_serviceRow)!!
        Picasso.get().load(userReview.userImage).into(image)
        name.text = "${userReview.fname} ${userReview.lname}"
        rating.text = userReview.rating.toString()
        review.text = userReview.review
    }
    override fun getLayout(): Int {
        return R.layout.row_reviews
    }

}
