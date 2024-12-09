package com.sos.msgroup

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sos.msgroup.databinding.ActivityOnlineSecurityMapsBinding
import com.sos.msgroup.model.SecurityGuard


class OnlineSecurityMapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var myRef: DatabaseReference
    private lateinit var binding: ActivityOnlineSecurityMapsBinding
    private lateinit var onLineSecuritiesList: ArrayList<SecurityGuard>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOnlineSecurityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        myRef = FirebaseDatabase.getInstance().reference.child("onLineSecurities")

        getAllOnLineSecurities()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // turn off rotation and compass
        mMap.uiSettings.isRotateGesturesEnabled = false
        mMap.uiSettings.isCompassEnabled = false
        mMap.uiSettings.isZoomControlsEnabled = false

        showPins()
    }

    private fun showAllPins() {

        try {
            if (onLineSecuritiesList.isNotEmpty()) {
                val boundsBuilder = LatLngBounds.builder()

                onLineSecuritiesList.forEach {
                    boundsBuilder.include(LatLng(it.latitude.toDouble(), it.longitude.toDouble()))
                }

                val bounds = boundsBuilder.build()
                //mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))

                val cameraPosition = CameraPosition.Builder().target(bounds.center).zoom(18f).build()
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            }
        }catch (e : Exception){

        }
    }

    /**
     * Show pins without clusters
     */
    private fun showPins() {

        try {
            mMap?.let { map ->
                onLineSecuritiesList.forEach {
                    addPin(map, LatLng(it.latitude.toDouble(), it.longitude.toDouble()), it.userID)
                }

            }

            showAllPins()

        }catch (e: Exception){

        }


    }

    /**
     * Put a pin on a map
     */
    private fun addPin(map: GoogleMap, location: LatLng, securityID: String) {
        val markerOptions = MarkerOptions()
            .position(location)
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.securityguard))
            //map.addMarker(markerOptions)

        val marker = map.addMarker(markerOptions)
        marker!!.tag= securityID

        map.setOnMarkerClickListener {
            // on below line we are displaying a toast message on clicking on marker
            Toast.makeText(
                this@OnlineSecurityMapsActivity,
                "Clicked location is " + it.tag,
                Toast.LENGTH_SHORT
            ).show()
            false
        }


    }


    private fun getAllOnLineSecurities() {

        onLineSecuritiesList = arrayListOf<SecurityGuard>()

        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Please wait...")
        progressDialog.show()
        progressDialog.setCancelable(false)

        myRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (childSnapshot in dataSnapshot.children) {

                    var security = childSnapshot.getValue(SecurityGuard::class.java)

                    if (security != null) {
                        onLineSecuritiesList.add(security)
                    }
                }

                progressDialog.hide()
                showPins()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Database", error.toString())
                progressDialog.hide()
                Toast.makeText(
                    this@OnlineSecurityMapsActivity,
                    error.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

}