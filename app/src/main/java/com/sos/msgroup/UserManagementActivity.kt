package com.sos.msgroup

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sos.msgroup.adapter.UserAdapter
import com.sos.msgroup.model.User

class UserManagementActivity : AppCompatActivity() {

   // private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private var usersList: ArrayList<User> = arrayListOf()
    private var matchedUsersList: ArrayList<User> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_managment)

        findViewByIds()
        getUsers()
    }

   /* private fun performSearch() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                search(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                search(newText)
                return true
            }
        })
    }*/

    private fun search(text: String?) {
        matchedUsersList = arrayListOf()

        text?.let {
            usersList.forEach { store ->
                if (store.phoneNumber.contains(text, true)) {
                    matchedUsersList.add(store)
                    updateRecyclerView()
                }
            }
            if (matchedUsersList.isEmpty()) {
                Toast.makeText(this,
                    "No match found!",
                    Toast.LENGTH_SHORT).show()
            }
            updateRecyclerView()
        }
    }

    private fun updateRecyclerView() {
        recyclerView.apply {
            userAdapter.usersList = matchedUsersList
            userAdapter.notifyDataSetChanged()
        }
    }

    private fun findViewByIds() {
      //  searchView = findViewById(R.id.searchViewUserManagement)
        recyclerView = findViewById(R.id.recyclerViewUserManagement)
    }

    private fun getUsers() {

        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Please wait...")
        progressDialog.show()
        progressDialog.setCancelable(false)

        var myRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("users")

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                usersList.clear()

                for (childSnapshot in dataSnapshot.children) {
                    var user = childSnapshot.getValue(User::class.java)

                    if (user != null) {
                        usersList.add(user)
                    }
                }

                usersList.reverse()

                val layoutManager = GridLayoutManager(this@UserManagementActivity, 1)

                userAdapter = this@UserManagementActivity?.let {
                    UserAdapter(it, usersList)
                }!!
                recyclerView.setHasFixedSize(true)
                recyclerView.layoutManager = layoutManager
                recyclerView.adapter = userAdapter

                progressDialog.hide()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Database", error.toString())
                progressDialog.hide()
                Toast.makeText(this@UserManagementActivity,
                    error.toString(),
                    Toast.LENGTH_SHORT).show()
            }
        })
    }
}