package com.example.capstoneProject.Handlers

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class UserSellerInfoHandler {

    var database: FirebaseDatabase
    var userSellerInfoRef: DatabaseReference

    init {
        database = FirebaseDatabase.getInstance()
        userSellerInfoRef = database.getReference("user_seller_info")
    }


}