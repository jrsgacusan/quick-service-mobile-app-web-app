package com.example.capstoneProject.GroupieViews

import android.widget.ImageView
import android.widget.TextView
import com.example.capstoneProject.R
import com.example.capstoneProject.Models.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import java.util.*

class ChatFromItem(val text: String, val user: User, val timeStamp: Long) : Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.textView_chatFromRow).text = text
        viewHolder.itemView.findViewById<TextView>(R.id.timeStamp_chatFromRow).text = convertLongToDate(timeStamp)
        //load user image
        val targetImageView =  viewHolder.itemView.findViewById<ImageView>(R.id.imageView_chatFromRow)
        Picasso.get().load(user.profileImageUrl).into(targetImageView)


    }

    override fun getLayout(): Int {
        return R.layout.row_chat_from
    }

    fun convertLongToDate(long: Long): String{
        val resultdate = Date(long)
        return resultdate.toString()
    }

}