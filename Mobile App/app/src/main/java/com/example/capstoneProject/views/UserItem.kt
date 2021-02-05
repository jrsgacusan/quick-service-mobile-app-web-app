package com.example.capstoneProject.views

import android.widget.ImageView
import android.widget.TextView
import com.example.capstoneProject.R
import com.example.capstoneProject.models.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder

//Class used for the Groupie adapter. The layout file service_row.xml is used.
class UserItem(val user: User): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.nameTextView_newMessageRow).text ="${user.firstName} ${user.lastName}"
        Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.findViewById<ImageView>(R.id.imageView_newMessageRow))
    }
    override fun getLayout(): Int {
        return R.layout.row_new_message
    }

}