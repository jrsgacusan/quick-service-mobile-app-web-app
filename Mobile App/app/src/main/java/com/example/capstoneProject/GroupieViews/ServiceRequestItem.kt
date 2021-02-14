package com.example.capstoneProject.GroupieViews

import android.widget.ImageView
import android.widget.TextView
import com.example.capstoneProject.Models.ServiceRequest
import com.example.capstoneProject.Models.User
import com.example.capstoneProject.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder

class ServiceRequestItem(val serviceRequest: ServiceRequest) : Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.title_serviceRowRequest).text = serviceRequest.title!!.toUpperCase()
        viewHolder.itemView.findViewById<TextView>(R.id.descriptionTextView_serviceRowRequest).text = serviceRequest.description
        viewHolder.itemView.findViewById<TextView>(R.id.priceTextView_serviceRowRequest).text = "₱${serviceRequest.price.toString()}"

        viewHolder.itemView.findViewById<TextView>(R.id.categoryTextView_serviceRowRequests).text = serviceRequest.category


        val ref = FirebaseDatabase.getInstance().getReference("users/${serviceRequest.userUid}")
        ref.addListenerForSingleValueEvent(object :  ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userData = snapshot.getValue(User::class.java)
                Picasso.get().load(userData!!.profileImageUrl).into(viewHolder.itemView.findViewById<ImageView>(R.id.imageView_serviceRowRequest))
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

    override fun getLayout(): Int {
        return R.layout.row_service_requests
    }

}