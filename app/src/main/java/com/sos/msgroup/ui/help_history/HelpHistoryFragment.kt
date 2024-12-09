package com.sos.msgroup.ui.help_history

import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sos.msgroup.adapter.HelpHistoryNotificationAdapter
import com.sos.msgroup.databinding.FragmentHelpHistoryBinding
import com.sos.msgroup.model.HelpNotification

class HelpHistoryFragment : Fragment() {

    private var _binding: FragmentHelpHistoryBinding? = null

    // This property is only valid between onCreateView and
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference

    private lateinit var recyclerView: RecyclerView
    private var helpNotifications: ArrayList<HelpNotification> = arrayListOf()
    private lateinit var helpNotificationAdapter: HelpHistoryNotificationAdapter
    private lateinit var progressDialog: ProgressDialog


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHelpHistoryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        recyclerView = binding.historyRecyclerView

        progressDialog = ProgressDialog(activity)
        progressDialog.setTitle("Please Wait")
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)

        initializeDbRef()

        return root
    }

    private fun initializeDbRef() {
        database =  FirebaseDatabase.getInstance().reference


        getAllCurrentUserNotification()
    }

    private fun getAllCurrentUserNotification() {

        progressDialog.show()

        var myRef: DatabaseReference = database.child("requests")

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                helpNotifications.clear()

                for (childSnapshot in dataSnapshot.children) {

                    var helpNotification = childSnapshot.getValue(HelpNotification::class.java)

                    if (helpNotification != null && helpNotification.userId == FirebaseAuth.getInstance().uid.toString()) {
                        helpNotifications.add(helpNotification)
                    }
                }

                helpNotifications.reverse()

                val layoutManager = GridLayoutManager(activity, 1)

                helpNotificationAdapter = activity?.let {
                    HelpHistoryNotificationAdapter(
                        it, helpNotifications
                    )
                }!!
                recyclerView.setHasFixedSize(true)
                recyclerView.layoutManager = layoutManager
                recyclerView.adapter = helpNotificationAdapter

                if(helpNotifications.isEmpty()){
                    recyclerView.visibility = View.GONE
                    binding.tvNoHistory.visibility = View.VISIBLE
                }

                progressDialog.dismiss()
            }

            override fun onCancelled(error: DatabaseError) {
                showMsg(error.toString())
                progressDialog.dismiss()
            }
        })
    }

    private fun showMsg(msg: String) {
        val toast = Toast.makeText(activity, msg, Toast.LENGTH_LONG)
        toast.show()
    }

}