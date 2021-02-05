package com.example.capstoneProject.views

import android.widget.ImageView
import android.widget.TextView
import com.example.capstoneProject.R
import com.example.capstoneProject.models.Service
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder

//Class used for the Groupie adapter. The layout file service_row.xml is used.
class ServiceItem(val service: Service): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.title_serviceRow).text = service.title
        viewHolder.itemView.findViewById<TextView>(R.id.descriptionTextView_serviceRow).text = service.description
        viewHolder.itemView.findViewById<TextView>(R.id.priceTextView_serviceRow).text = "₱${service.price.toString()}"
        Picasso.get().load(service.userImageUrl).into(viewHolder.itemView.findViewById<ImageView>(R.id.imageView_serviceRow))
    }
    override fun getLayout(): Int {
        return R.layout.row_service
    }

}
