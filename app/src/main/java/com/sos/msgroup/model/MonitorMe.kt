package com.sos.msgroup.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MonitorMe(
    var userId: String,
    var plateNumber: String,
    var model: String,
    var make: String,
    var description: String,
    var employeeNumber: String,
    var latitude: String,
    var longitude: String,
    val monitorMeId: String,
    var time: String,
    var monitorMeType: String,

    ) : Parcelable {
    constructor() : this("", "", "", "", "", "", "", "","","","")
}
