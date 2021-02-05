package com.example.capstoneProject.models

import android.os.Parcelable
import com.example.capstoneProject.SignUpAcitivity

import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@Parcelize
@IgnoreExtraProperties
class User(var uid: String? = "",
           var firstName: String? = "",
           var lastName: String? = "",
           var emailAddress: String? = "",
           var mobileNumber: String? = "",
           var age: Int? = 18,
           var profileImageUrl: String? = "${SignUpAcitivity.DEFAULT_IMG_URL}",
           var bio: String? = "",
           var verified: String? = "NOT_VERIFIED"

): Parcelable  {



}