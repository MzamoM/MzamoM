package com.sos.msgroup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.sos.msgroup.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    //Forgot password screen
    //Show/hide password [Done]
    //Admin page

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = FirebaseAuth.getInstance()
    }

    fun login(view: View) {

        if(binding.editTextLoginEmailAddress.text.isNullOrBlank()){
            binding.editTextLoginEmailAddress.error = "Email required"
            return
        }else if(binding.editTextLoginPassword.text.isNullOrBlank()) {
            binding.editTextLoginPassword.error = "Password required"
            return
        } else{
            val email = binding.editTextLoginEmailAddress.text.toString()
            val password = binding.editTextLoginPassword.text.toString()

            auth.signInWithEmailAndPassword(email.trim(), password.trim()).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }


    }

    fun goToRegister(view: View) {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    fun goToForgotPassword(view: View) {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
}