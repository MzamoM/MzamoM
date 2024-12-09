package com.sos.msgroup

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.sos.msgroup.databinding.ActivityRegisterBinding
import com.sos.msgroup.model.User

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var database: DatabaseReference
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)

        initializeDbRef()
        auth = FirebaseAuth.getInstance()
    }

    private fun initializeDbRef() {
        database = FirebaseDatabase.getInstance().reference
    }

    fun register(view: View) {

        if (binding.editTextEmailAddress.text.isNullOrBlank()) {
            binding.editTextEmailAddress.error = "Email required"
        } else if (binding.editTextPassword.text.isNullOrBlank()) {
            binding.editTextPassword.error = "Password required"
        } else if (binding.editTextRegFirstName.text.isNullOrBlank()) {
            binding.editTextRegFirstName.error = "Firstname required"
        } else if (binding.editTextRegLastName.text.isNullOrBlank()) {
            binding.editTextRegLastName.error = "Lastname required"
        } else if (binding.editTextPhoneNumber.text.isNullOrBlank()) {
            binding.editTextPhoneNumber.error = "Phone number required"
        } else if (binding.editTextPhoneNumber.text.isNotEmpty() && binding.editTextPhoneNumber.text.length != 10) {
            binding.editTextPhoneNumber.error = "Invalid Phone number"
        } else {
            progressDialog.show()

            val email = binding.editTextEmailAddress.text.toString()
            val password = binding.editTextPassword.text.toString()
            val firstName = binding.editTextRegFirstName.text.toString()
            val lastName = binding.editTextRegLastName.text.toString()
            val phone = binding.editTextPhoneNumber.text.toString()

            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    var user = User(
                        "Customer",
                        "",
                        firstName,
                        lastName,
                        "male",
                        phone,
                        email,
                        FirebaseAuth.getInstance().uid.toString(),
                        "",
                        false,
                        "","","","",true
                    )
                    saveNewUser(user)
                }
            }.addOnFailureListener { exception ->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun saveNewUser(user: User) {

        database.child("users").child(user.id).setValue(user).addOnSuccessListener {
            progressDialog.dismiss()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }.addOnFailureListener {
            showMsg(it.toString())
            progressDialog.dismiss()
        }

    }

    private fun showMsg(msg: String) {
        val toast = Toast.makeText(this@RegisterActivity, msg, Toast.LENGTH_LONG)
        toast.show()
    }

    fun goToLogin(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}