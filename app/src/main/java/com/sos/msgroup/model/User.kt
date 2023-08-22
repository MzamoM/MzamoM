package com.sos.msgroup.model
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    var type: String, var ID_Number: String, var firstName: String, var lastName: String,
    var gender: String, var phoneNumber: String, var email: String, var id:String
) : Parcelable {
    constructor(): this ("","","","","","","","")
}