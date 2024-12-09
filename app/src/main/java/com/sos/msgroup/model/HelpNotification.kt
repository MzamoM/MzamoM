package com.sos.msgroup.model

import android.os.Parcelable
import android.widget.ImageView
import kotlinx.parcelize.Parcelize

@Parcelize
data class HelpNotification(
    var userId: String,
    var firstName: String,
    var lastName: String,
    var latitude: String,
    var longitude: String,
    val requestId: String,
    var isActive: Boolean,
    var time: String,
    var comment: String,
    var viewed: Boolean,
    var userProfilePic: String,
    var panicType: String

) : Parcelable {
    constructor() : this("", "", "", "", "", "", true, "","",false,"","")
}