package com.sos.msgroup.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class SecurityGuard (
    var userID: String,
    var latitude: String,
    var longitude: String
    ) : Parcelable {
    constructor(): this ("","","")
}