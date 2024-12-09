package com.sos.msgroup.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sos.msgroup.R
import com.sos.msgroup.databinding.ActivityMapsBinding
import com.sos.msgroup.model.HelpNotification
import com.sos.msgroup.model.User

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var user: User
    private lateinit var binding: ActivityMapsBinding
    private lateinit var  helpNotification: HelpNotification

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        helpNotification = intent.getParcelableExtra("helpRequested")!!
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        actionBar?.title ?:  helpNotification.firstName

        initializeDbRef()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun initializeDbRef() {
        database =  FirebaseDatabase.getInstance().reference
        getRequestedUserDetails()

        updateViewedStatus()
    }

    private fun getRequestedUserDetails() {

        var myRef: DatabaseReference = database.child("users").child(helpNotification.userId)
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                user = dataSnapshot.getValue(User::class.java)!!
            }

            override fun onCancelled(error: DatabaseError) {
                showMsg(error.toString())
            }

        })

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mMap.isMyLocationEnabled = true

        val sydney = LatLng(helpNotification.latitude.toDouble(), helpNotification.longitude.toDouble())

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(sydney.latitude, sydney.longitude), 14f))
        mMap.addMarker(MarkerOptions().position(sydney).title(helpNotification.firstName +" "+ helpNotification.lastName ))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.help_map_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_close_report -> {

                if (this::helpNotification.isInitialized) {

                    if(!helpNotification.isActive){
                        cancelHelpRequest("This request have already been closed and achieved","")
                    }else{
                        showMessageBox()
                    }

                }

                true
            }
            R.id.action_call -> {

                if (this::user.isInitialized && user !=null) {
                    if(!user.phoneNumber.isNullOrBlank()){
                        call(user.phoneNumber)
                    }else{
                        showMsg("Phone number is missing")
                    }
                }

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showMessageBox(){

        //Inflate the dialog as custom view
        val messageBoxView = LayoutInflater.from(this).inflate(R.layout.pop_up_close_help_layout, null)

        //AlertDialogBuilder
        val messageBoxBuilder = AlertDialog.Builder(this).setView(messageBoxView)

        //setting text values
        var comment :EditText = messageBoxView.findViewById(R.id.edtComment)
        var save :Button = messageBoxView.findViewById(R.id.btnSaveWithComment)

        //show dialog
        val  messageBoxInstance = messageBoxBuilder.show()
        messageBoxInstance.setCancelable(false)

        //set Listener
        save.setOnClickListener {
            //close dialog
            if(comment !=null ){
                cancelHelpRequest("This request has been closed and achieved",comment.text.toString())
                messageBoxInstance.dismiss()
            }

        }
    }

    private fun updateViewedStatus(){
        if (this::helpNotification.isInitialized) {
            helpNotification.viewed = true
            database.child("requests").child(helpNotification.requestId).setValue(helpNotification).addOnSuccessListener {
            }.addOnFailureListener {
                showMsg(it.toString())
            }
        }else{
            showMsg("Something went wrong")
        }

    }

    private fun cancelHelpRequest(message:String,comment:String){
        if (this::helpNotification.isInitialized) {
            helpNotification.isActive = false
            helpNotification.comment = comment
            database.child("requests").child(helpNotification.requestId).setValue(helpNotification).addOnSuccessListener {
                showMsg(message)
            }.addOnFailureListener {
                showMsg(it.toString())
            }
        }else{
            showMsg("Something went wrong")
        }

    }

    private fun showMsg(msg: String) {
        val toast = Toast.makeText(this, msg, Toast.LENGTH_LONG)
        toast.show()
    }

    fun call(phoneNumber: String) {
        val dialIntent = Intent(Intent.ACTION_DIAL)
        dialIntent.data = Uri.parse("tel:$phoneNumber")
        startActivity(dialIntent)
    }
}