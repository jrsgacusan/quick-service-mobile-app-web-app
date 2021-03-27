package com.example.capstoneProject.Models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class RequestMessage (
    var uid: String? = "",
    var fromId: String? = "",
    var toId: String? = "",
    var text: String? = "",
    var timeStamp: Long? = 0,
    var requestUid: String? = "")