package com.sos.msgroup.model

import android.os.Parcelable
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
) : Parcelable {
    constructor() : this("", "", "", "", "", "", true, "","")
}