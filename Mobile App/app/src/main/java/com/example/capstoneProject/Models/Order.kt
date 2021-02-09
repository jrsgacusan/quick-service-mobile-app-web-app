package com.example.capstoneProject.Models

import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@Parcelize
@IgnoreExtraProperties
class Order(var uid: String? = null,
            var service_booked_uid:String?= null,
            var address: String? = null,
            var date: String? = null,
            var name: String? = null,
            var status: String? = "NEW",
            var time: String? = null,
            var price: Int? = 0,
            var dateOrdered: Long = System.currentTimeMillis(),
            var title: String? = null,
            var category: String? = null,
            var description: String? = null,
            var service_provider_uid: String ? = null,
            var userUid: String? = null,
            var buyerConfirmation: String = "",
            var sellerConfirmation: String = "",
            var reviewed: Boolean = false,
            var modeOfPayment: String? = ""

            ): Parcelable {
}