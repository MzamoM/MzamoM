package com.sos.msgroup.model
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    var type: String, var ID_Number: String, var firstName: String, var lastName: String,
    var gender: String, var phoneNumber: String, var email: String, var id:String, var profileImage:String,
    var userCaptured: Boolean, var password:String,var nextKinFirstName:String,var nextKinLastName:String,var nextKinPhone:String
) : Parcelable {
    constructor(): this ("","","","","","","","","",false,"","","","")
}