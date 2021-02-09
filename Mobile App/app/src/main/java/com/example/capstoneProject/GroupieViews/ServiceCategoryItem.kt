package com.example.capstoneProject.GroupieViews


import android.widget.ImageView
import android.widget.TextView
import com.example.capstoneProject.R
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder

class ServiceCategoryItem(val serviceTitle: String, val imageResource: Int) : Item<ViewHolder>() {


    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.title_serviceCategoryRow).text = serviceTitle
        viewHolder.itemView.findViewById<ImageView>(R.id.imageView_serviceCategoryRow).setImageResource(imageResource)
    }

    override fun getLayout(): Int {
        return R.layout.row_service_category
    }

    override fun getSpanSize(spanCount: Int, position: Int) = spanCount / 2
}