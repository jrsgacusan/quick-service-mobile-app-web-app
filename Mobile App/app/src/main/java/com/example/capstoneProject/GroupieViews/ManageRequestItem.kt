package com.example.capstoneProject.GroupieViews



import android.widget.ImageView
import android.widget.TextView
import com.example.capstoneProject.Models.ServiceRequest
import com.example.capstoneProject.R
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import java.util.*
import kotlin.collections.ArrayList


class ManageRequestItem(val request: ServiceRequest) : Item<ViewHolder>() {


    override fun bind(viewHolder: ViewHolder, position: Int) {

        val category =  viewHolder.itemView.findViewById<TextView>(R.id.category_resuest)
        val title =  viewHolder.itemView.findViewById<TextView>(R.id.title_request)
        val price = viewHolder.itemView.findViewById<TextView>(R.id.price_request)

        category.text = request.category
        title.text = request.title!!.toUpperCase()
        price.text = "₱${request.price}"


    }

    override fun getLayout(): Int {
        return R.layout.row_manage_request
    }
}