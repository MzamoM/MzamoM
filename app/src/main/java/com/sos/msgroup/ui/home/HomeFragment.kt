package com.sos.msgroup.ui.home

import android.annotation.SuppressLint
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.size
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sos.msgroup.R
import com.sos.msgroup.databinding.FragmentHomeBinding
import com.sos.msgroup.model.HelpNotification
import com.sos.msgroup.model.User
import com.sos.msgroup.notification.MyFirebaseMessagingService
import java.util.*


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    private val binding get() = _binding!!

    private lateinit var database: DatabaseReference
    private lateinit var currentUser: User
    private lateinit var request: HelpNotification
    private lateinit var userCurrentLocation: Location
    private val locationPermissionCode = 2

    // inside a basic activity
    private var locationManager: LocationManager? = null

    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        initializeDbRef()
       // activity?.let { MobileAds.initialize(it){} }

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val helpImage: ImageView = binding.imgCallHelp
        val cancelHelpImage: ImageView = binding.imgCancelHelp

        loadBanner()


        //initialize location provides
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        locationManager = requireActivity().getSystemService(LOCATION_SERVICE) as LocationManager?

        requestLocation()

        helpImage.setOnClickListener { view ->

            try {
                if (this::currentUser.isInitialized && currentUser != null) {

                    if (this::userCurrentLocation.isInitialized && userCurrentLocation != null) {
                        startPosting(helpImage, cancelHelpImage, view)
                    } else {

                        requestLocation()

                        if(this::userCurrentLocation.isInitialized && userCurrentLocation != null){
                            startPosting(helpImage, cancelHelpImage, view)

                        }else{
                            mFusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                                if (location != null) {
                                    userCurrentLocation = location
                                    startPosting(helpImage, cancelHelpImage, view)
                                } else {
                                    displayMessage(
                                        "We can't pick up your location,move around and try", view
                                    )
                                }
                            }
                        }


                    }
                }
            } catch (e: Exception) {
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

    private fun loadBanner() {
        // This is an ad unit ID for a test ad. Replace with your own banner ad unit ID.
      //  binding.adView.adUnitId = "ca-app-pub-3940256099942544/9214589741"
       // binding.adView.size

        // Create an ad request.
        val adRequest = AdRequest.Builder().build()

        // Start loading the ad in the background.
        binding.adView.loadAd(adRequest)
    }


    private fun requestLocation() {
        try {
            // Request location updates
            locationManager?.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener
            )
        } catch (ex: SecurityException) {
            showMsg(ex.toString())
        }
    }

    private fun startPosting(helpImage: ImageView, cancelHelpImage: ImageView, view: View) {

        helpImage.visibility = View.GONE
        cancelHelpImage.visibility = View.VISIBLE
        displayMessage("Help is on the way", view)

        sendNewHelpRequest(
            currentUser.id,
            currentUser.firstName,
            currentUser.lastName,
            userCurrentLocation.latitude.toString(),
            userCurrentLocation.longitude.toString(),
            database.push().key.toString(),
            true,
            System.currentTimeMillis().toString(),
            "I need help urgent",
            currentUser.profileImage
        )

    }

    private fun getCurrentUserDetails() {

        var myRef: DatabaseReference =  database.child("users").child(FirebaseAuth.getInstance()?.uid.toString())
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val user = dataSnapshot.getValue(User::class.java)

                if (user != null) {
                    currentUser = user

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
        userProlePic: String,
    ) {
        request = HelpNotification(
            userId, firstName, lastName, latitude, longitude, requestId, isActive, time, comment,false,userProlePic
        )
        database.child("requests").child(request.requestId).setValue(request).addOnSuccessListener {
            postNotification()
        }.addOnFailureListener {
            showMsg(it.toString())
        }
    }

    private fun cancelHelpRequest(message: String, view: View) {
        try {
            if (request != null) {
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

        } catch (e: Exception) {
            displayMessage(e.toString(), view)
        }

    }

    //define the listener
    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            userCurrentLocation = location
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    private fun postNotification() {
        var topic = "_help_request"
        var title = getString(R.string.notification_title)
        var content = getString(R.string.notification_description)

        MyFirebaseMessagingService.sendMessage(title, content, topic)
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showMsg("Permission Granted")
            } else {
                showMsg("Permission Denied")
            }
        }
    }

}