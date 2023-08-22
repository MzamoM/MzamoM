package com.sos.msgroup.ui.home

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.sos.msgroup.databinding.FragmentHomeBinding


import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.sos.msgroup.R
import com.sos.msgroup.model.HelpNotification
import com.sos.msgroup.model.User
import com.sos.msgroup.notification.FCMSender
import com.sos.msgroup.notification.NotificationMessage
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.util.*


class HomeFragment : Fragment(), LocationListener {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val permissionId = 2

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var database: DatabaseReference
    private lateinit var currentUser: User
    private lateinit var request: HelpNotification
    private lateinit var userCurrentLocation: Location

    private lateinit var locationManager: LocationManager
    private lateinit var tvGpsLocation: TextView
    private val locationPermissionCode = 2


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        initializeDbRef()

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val helpImage: ImageView = binding.imgCallHelp
        val cancelHelpImage: ImageView = binding.imgCancelHelp


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())



        helpImage.setOnClickListener { view ->

            getLocation()

            try {
                if (this::currentUser.isInitialized) {

                    if (this::userCurrentLocation.isInitialized) {
                        sendNewHelpRequest(
                            currentUser.id,
                            currentUser.firstName,
                            currentUser.lastName,
                            userCurrentLocation.latitude.toString(),
                            userCurrentLocation.longitude.toString(),
                            database.push().key.toString(),
                            true,
                            System.currentTimeMillis().toString(),
                            "I need help urgency"
                        )

                        helpImage.visibility = View.GONE
                        cancelHelpImage.visibility = View.VISIBLE
                        displayMessage("Help is on the way", view)

                    } else {
                        displayMessage("We can't pick up your location,move around and try", view)
                    }
                }
            }catch (e:Exception){
                showMsg(e.toString())
            }

        }

        cancelHelpImage.setOnClickListener { view ->

            cancelHelpRequest("Panic canceled", view)

            helpImage.visibility = View.VISIBLE
            cancelHelpImage.visibility = View.GONE
        }

        return root
    }

    private fun getCurrentUserDetails() {

        var myRef: DatabaseReference = database.child("users").child(FirebaseAuth.getInstance()?.uid.toString())
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val user = dataSnapshot.getValue(User::class.java)

                if (user != null) {
                    currentUser = user

                    if (currentUser.type.lowercase() != "customer") {
                        FirebaseMessaging.getInstance().subscribeToTopic("new_help_request")
                    }

                } else {
                    showMsg("Unknown user")
                    return
                }

            }

            override fun onCancelled(error: DatabaseError) {
                showMsg(error.toString())
            }
        })

    }

    private fun initializeDbRef() {
        database = FirebaseDatabase.getInstance().reference

        getCurrentUserDetails()
    }


    @SuppressLint("MissingPermission", "SetTextI18n")
   /* private fun getLocation() {
        try {
            if (checkPermissions()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        userCurrentLocation = location

                        /* if(this::currentUser.isInitialized){
                              sendNewHelpRequest(
                                  currentUser.id,
                                  currentUser.firstName,
                                  currentUser.lastName,
                                  location.latitude.toString(),
                                  location.longitude.toString(),
                                  database.push().key.toString(),
                                  true,
                                  System.currentTimeMillis().toString(),
                                  "I need help urgency"
                              )
                          }*/

                    } else {
                        //current location is null
                        showMsg("Oops, we could not locate you.")
                    }
                }


                /*if (isLocationEnabled()) {
                    mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                        val location: Location? = task.result
                        if (location != null) {
                            userCurrentLocation = location

                        } else {
                            //current location is null
                            showMsg("Ooops, we could not locate you.")
                        }
                    }
                } else {

                    showMsg("Please turn on location")
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)
                }*/
            } else {
                requestPermissions()
            }
        }catch (e:Exception){
            showMsg(e.toString())
        }


    }*/

    private fun isLocationEnabled(): Boolean {
        try {
            val locationManager: LocationManager =
                requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
            )
        }catch (e:Exception){
            return true
        }


    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {

        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
            ), permissionId
        )
    }

    @SuppressLint("MissingSuperCall")
    /*override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }*/

    private fun sendNewHelpRequest(
        userId: String,
        firstName: String,
        lastName: String,
        latitude: String,
        longitude: String,
        requestId: String,
        isActive: Boolean,
        time: String,
        comment: String,
    ) {

        request = HelpNotification(userId, firstName, lastName, latitude, longitude, requestId, isActive, time, comment)

        database.child("requests").child(request.requestId).setValue(request).addOnSuccessListener {
            // Write was successful!
            sendNotificationToAdmin()
        }.addOnFailureListener {
                showMsg(it.toString())
            }

    }

    private fun cancelHelpRequest(message: String, view: View) {
        try{
            if (request !=null){
                if (this::request.isInitialized) {
                    request.isActive = false
                    database.child("requests").child(request.requestId).setValue(request)
                        .addOnSuccessListener {
                            displayMessage("Panic request cancelled", view)
                        }.addOnFailureListener {
                            showMsg(it.toString())
                        }
                } else {
                    displayMessage("On request to cancel", view)
                }
            }

        }catch (e: Exception) {
            displayMessage(e.toString(), view)
        }

    }

    private fun sendNotificationToAdmin() {

        FCMSender().send(java.lang.String.format(
            NotificationMessage.message,
            "new_help_request",
            getString(R.string.notification_description),
            "+27824382247"
        ), object : Callback {
            override fun onFailure(call: Call, e: IOException) {}

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                activity?.runOnUiThread(Runnable {
                    if (response.code == 200) {
                        Toast.makeText(
                            activity, "Notification sent", Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }
        })
    }


    private fun showMsg(msg: String) {
        val toast = Toast.makeText(activity, msg, Toast.LENGTH_LONG)
        toast.show()
    }

    private fun displayMessage(message: String, view: View) {
        if (this::request.isInitialized) {
            Snackbar.make(view, "Hi ${request.firstName} , $message", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        } else {
            Snackbar.make(view, "Hi , $message", Snackbar.LENGTH_LONG).setAction("Action", null)
                .show()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getLocation() {
        try {
            locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if ((ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionCode)
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
        }catch (e:Exception){
            showMsg(e.toString())
        }
    }
    override fun onLocationChanged(location: Location) {
        userCurrentLocation = location
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showMsg("Permission Granted")
            }
            else {
                showMsg("Permission Denied")
            }
        }
    }

}