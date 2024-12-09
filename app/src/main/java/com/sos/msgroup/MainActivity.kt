package com.sos.msgroup

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sos.msgroup.databinding.ActivityMainBinding
import com.sos.msgroup.model.User
import com.sos.msgroup.notification.MyFirebaseMessagingService


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private var isCurrentUserAdmin: Boolean = false

    private lateinit var database: DatabaseReference
    private lateinit var user: User

    companion object {
        private const val TAG = "MainActivity"
        private const val topic_toSubscribe = "_help_request"
        private const val NOTIFICATION_REQUEST_CODE = 1234
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_gallery,
                R.id.nav_profile,
                R.id.nav_history,
                R.id.nav_emergency,
                R.id.nav_payment,
                R.id.nav_subscription
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        initializeDbRef()
    }

    fun subscribeTopics() {
        MyFirebaseMessagingService.subscribeTopic(this@MainActivity,topic_toSubscribe)
    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)

        menu.findItem(R.id.action_settings).isVisible = isCurrentUserAdmin

        return true
    }

    private fun initializeDbRef() {
        database = FirebaseDatabase.getInstance().reference
        getCurrentUserRole()
    }

    private fun getCurrentUserRole() {

        var myRef: DatabaseReference = database.child("users").child(FirebaseAuth.getInstance().uid.toString())
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                 user = dataSnapshot.getValue(User::class.java)!!

                if (user != null) {
                    isCurrentUserAdmin = user.type.lowercase() != "customer"
                    invalidateOptionsMenu()

                    if(isCurrentUserAdmin){
                        subscribeTopics()
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.action_online_security -> {
                val intent = Intent(this, OnlineSecurityMapsActivity::class.java)
                startActivity(intent)
                true
            }

            R.id.action_settings -> {
                val intent = Intent(this, AdminNotificationActivity::class.java)
                startActivity(intent)
                true
            }

            R.id.action_delete -> {
                AlertDialog.Builder(this).apply {
                    setTitle("Please confirm")
                    setMessage("Are you sure you want to delete your account?")

                    setPositiveButton("Yes") { _, _ ->
                        deleteUserAccount(user.email, user.password)
                    }

                    setNegativeButton("No") { _, _ ->
                    }

                    setCancelable(true)
                }.create().show()
                true
            }

            R.id.action_logout -> {
                AlertDialog.Builder(this).apply {
                    setTitle("Please confirm")
                    setMessage("Are you sure you want to log out?")

                    setPositiveButton("Yes") { _, _ ->
                        signOut()
                    }

                    setNegativeButton("No") { _, _ ->
                    }

                    setCancelable(true)
                }.create().show()
                true
            }

            R.id.action_Share -> {
                showSharingDialog(
                    "Protect your Family with MS GROUP panic app",
                    "https://play.google.com/store/apps/details?id=com.sos.msgroup"
                )
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showSharingDialog(text: String, url: String) {
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, "$text: $url")
        startActivity(Intent.createChooser(intent, "Share via"))
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


    private fun showMsg(msg: String) {
        val toast = Toast.makeText(this, msg, Toast.LENGTH_LONG)
        toast.show()
    }

    private fun deleteUserAccount(email: String, password: String) {
        val user = FirebaseAuth.getInstance().currentUser

        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.
        val credential = EmailAuthProvider.getCredential(email, password)

        // Prompt the user to re-provide their sign-in credentials
        user?.reauthenticate(credential)?.addOnCompleteListener {
            user.delete()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        showMsg("Deleted User Successfully,")
                        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                        finish()
                    }else{
                        showMsg("User account not deleted")
                    }


                }
        }
    }

}