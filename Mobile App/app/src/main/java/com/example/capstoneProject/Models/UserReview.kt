package com.example.capstoneProject.Models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class UserReview(var uid: String? = "",
                 var fname: String? = "",
                 var lname: String? = "",
                 var userImage: String? = "",
                 var userUid: String? = "",
                 var review: String = "",
                 var rating: Int? =  0) {
}