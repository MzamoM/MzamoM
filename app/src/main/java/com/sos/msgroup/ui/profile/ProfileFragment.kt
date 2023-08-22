package com.sos.msgroup.ui.profile

import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sos.msgroup.databinding.FragmentProfileBinding
import com.sos.msgroup.model.User


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference

    private lateinit var user: User
    private lateinit var  editTextFirstName : EditText
    private lateinit var  editTextLastName : EditText
    private lateinit var  editTextIDNumber : EditText
    private lateinit var  editTextPhoneNumber : EditText
    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

         editTextFirstName = binding.editTextFirstName
         editTextLastName = binding.editTextLastName
         editTextIDNumber = binding.editTextIDNumber
         editTextPhoneNumber = binding.editTextPhoneNumber

        progressDialog = ProgressDialog(activity)
        progressDialog.setTitle("Please Wait")
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)

        initializeDbRef()

        binding.btnSave.setOnClickListener {
            saveUserInfo()
        }



        return root
    }

    private fun initializeDbRef() {
        database =  FirebaseDatabase.getInstance().reference

        getCurrentUserDetails()
    }

    private fun getCurrentUserDetails() {

        progressDialog.show()

        var myRef: DatabaseReference = database.child("users").child(FirebaseAuth.getInstance().uid.toString())
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                 user = dataSnapshot.getValue(User::class.java)!!

                if (user != null) {

                    editTextLastName.setText(user.lastName)
                    editTextFirstName.setText(user.firstName)
                    editTextIDNumber.setText(user.ID_Number)
                    editTextPhoneNumber.setText(user.phoneNumber)

                }else{
                    showMsg("Unknown user")
                    return
                }

                progressDialog.dismiss()

            }

            override fun onCancelled(error: DatabaseError) {
                showMsg(error.toString())
                progressDialog.dismiss()
            }

        })

    }

    private fun saveUserInfo() {

        if(editTextFirstName.text.isNotEmpty() && editTextLastName.text.isNotEmpty() ){

            user.firstName = editTextFirstName.text.toString()
            user.lastName = editTextLastName.text.toString()
            //user.ID_Number = editTextIDNumber.text.toString()

            if(editTextIDNumber.text.toString().isNotEmpty() && editTextIDNumber.text.toString().length == 13){
                user.ID_Number = editTextIDNumber.text.toString()
            }

            if(editTextPhoneNumber.text.toString().isNotEmpty() && editTextPhoneNumber.text.toString().length == 10){
                user.phoneNumber = editTextPhoneNumber.text.toString()
            }


            val progressDialog = ProgressDialog(activity)
            progressDialog.setMessage("Please wait...")
            progressDialog.show()
            progressDialog.setCancelable(false)
            progressDialog.show()

            if (user != null) {

                val database = FirebaseDatabase.getInstance().reference.child("users")

                database.child(user.id).setValue(user)
                    .addOnSuccessListener {
                        showMsg("user updated successfully")
                        progressDialog.hide()
                    }
                    .addOnFailureListener {
                        progressDialog.hide()
                    }

            }
        }
    }

    private fun showMsg(msg: String) {
        val toast = Toast.makeText(activity, msg, Toast.LENGTH_LONG)
        toast.show()
    }

}