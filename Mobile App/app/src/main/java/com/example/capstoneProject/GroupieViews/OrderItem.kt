@file:Suppress("DEPRECATION")

package com.example.capstoneProject.GroupieViews

import android.content.Context
import android.widget.TextView
import com.example.capstoneProject.R
import com.example.capstoneProject.Models.Order
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder

class OrderItem(val order: Order, val c: Context): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        val status = viewHolder.itemView.findViewById<TextView>(R.id.statusTextView_rowBookings)!!
        val date = viewHolder.itemView.findViewById<TextView>(R.id.date_rowBookings)!!
        val title = viewHolder.itemView.findViewById<TextView>(R.id.title_rowBookings)!!
        val category = viewHolder.itemView.findViewById<TextView>(R.id.category_rowBookings)!!
        val time = viewHolder.itemView.findViewById<TextView>(R.id.time_rowBookings)!!

        if (order.status == "NEW"){
            val color = c.resources.getColor(R.color.new_order)
            status.setBackgroundColor(color)
        } else if (order.status == "ACCEPTED") {
            val color = c.resources.getColor(R.color.on_going_order)
            status.setBackgroundColor(color)
        } else if (order.status == "COMPLETE"){
            val color = c.resources.getColor(R.color.complete_order)
            status.setBackgroundColor(color)
        }

        status.text = order.status
        date.text = order.date
        title.text = order.title
        category.text = order.category
        time.text = order.time


    }

    override fun getLayout(): Int {
        return R.layout.row_bookings
    }

    override fun getSpanSize(spanCount: Int, position: Int) = spanCount / 2
}