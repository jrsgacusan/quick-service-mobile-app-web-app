@file:Suppress("DEPRECATION")

package com.example.capstoneProject.GroupieViews


import android.content.Context
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.capstoneProject.Models.Service
import com.example.capstoneProject.R
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder

class ServiceCreatedItem(val service: Service, val c: Context) : Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.categoryTextView_serviceCreatedRow).text = "Category: ${service.category}"
        viewHolder.itemView.findViewById<TextView>(R.id.titleTextView_serviceCreatedRow).text = "Title: ${service.title.toString().toUpperCase()}"
        viewHolder.itemView.findViewById<TextView>(R.id.descriptionTextView_serviceCreatedRow).text = "Description: ${service.description}"
        viewHolder.itemView.findViewById<TextView>(R.id.priceTextView_serviceCreatedRow).text = "Price: Php${service.price}"
        val status = viewHolder.itemView.findViewById<TextView>(R.id.statusTextView_serviceCreatedRow)
        status.text = "${service.status}"
        val cardView = viewHolder.itemView.findViewById<CardView>(R.id.cardView_serviceCreatedRow)
        if (status.text.toString() == "PENDING") {
            val color = c.resources.getColor(R.color.pending)
            cardView.setBackgroundColor(color)
        } else if (status.text.toString() == "ACTIVE") {
            val color = c.resources.getColor(R.color.active)
            cardView.setBackgroundColor(color)
        } else if (status.text.toString() == "PAUSED") {
            val color = c.resources.getColor(R.color.pause)
            cardView.setBackgroundColor(color)

        }


    }

    override fun getLayout(): Int {
        return R.layout.row_service_created
    }


}