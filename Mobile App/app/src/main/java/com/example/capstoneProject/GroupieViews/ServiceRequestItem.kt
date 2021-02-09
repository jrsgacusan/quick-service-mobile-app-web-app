package com.example.capstoneProject.GroupieViews

import android.widget.ImageView
import android.widget.TextView
import com.example.capstoneProject.R
import com.example.capstoneProject.Models.ServiceRequest
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder

class ServiceRequestItem(val serviceRequest: ServiceRequest): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.title_serviceRowRequest).text = serviceRequest.title!!.toUpperCase()
        viewHolder.itemView.findViewById<TextView>(R.id.descriptionTextView_serviceRowRequest).text = serviceRequest.description
        viewHolder.itemView.findViewById<TextView>(R.id.priceTextView_serviceRowRequest).text = "₱${serviceRequest.price.toString()}"
        Picasso.get().load(serviceRequest.userImage).into(viewHolder.itemView.findViewById<ImageView>(R.id.imageView_serviceRowRequest))
        viewHolder.itemView.findViewById<TextView>(R.id.categoryTextView_serviceRowRequests).text = serviceRequest.category

    }
    override fun getLayout(): Int {
        return R.layout.row_service_requests
    }

}