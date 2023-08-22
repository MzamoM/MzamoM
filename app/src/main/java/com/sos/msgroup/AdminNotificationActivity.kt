package com.sos.msgroup

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sos.msgroup.adapter.HelpNotificationAdapter
import com.sos.msgroup.model.HelpNotification

class AdminNotificationActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var database: DatabaseReference

    private var helpNotifications: ArrayList<HelpNotification> = arrayListOf()
    private lateinit var helpNotificationAdapter: HelpNotificationAdapter
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_notification)
        supportActionBar?.subtitle = "Help Request(s)"

        helpNotifications.clear()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setMessage("Loading ...")
        progressDialog.setCancelable(false) // blocks UI interaction

        findViewByIds()

        initializeDbRef()
    }

    private fun initializeDbRef() {
        database = FirebaseDatabase.getInstance().reference

        getAllNotification()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.users_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_user_management -> {
                val intent = Intent(this, UserManagementActivity::class.java)
                startActivity(intent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun findViewByIds() {
        recyclerView = findViewById(R.id.recyclerView)
    }

    private fun getAllNotification() {

        progressDialog.show()

        var myRef: DatabaseReference = database.child("requests")

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                helpNotifications.clear()

                for (childSnapshot in dataSnapshot.children) {

                    var helpNotification = childSnapshot.getValue(HelpNotification::class.java)

                    if (helpNotification != null && helpNotification.isActive) {
                        helpNotifications.add(helpNotification)
                    }
                }

                helpNotifications.reverse()

                val layoutManager = GridLayoutManager(this@AdminNotificationActivity, 1)

                helpNotificationAdapter = this@AdminNotificationActivity?.let {
                    HelpNotificationAdapter(
                        it, helpNotifications
                    )
                }!!
                recyclerView.setHasFixedSize(true)
                recyclerView.layoutManager = layoutManager
                recyclerView.adapter = helpNotificationAdapter

                progressDialog.dismiss()
            }

            override fun onCancelled(error: DatabaseError) {
                showMsg(error.toString())
                progressDialog.dismiss()
            }
        })
    }

    private fun showMsg(msg: String) {
        val toast = Toast.makeText(this@AdminNotificationActivity, msg, Toast.LENGTH_LONG)
        toast.show()
    }

}