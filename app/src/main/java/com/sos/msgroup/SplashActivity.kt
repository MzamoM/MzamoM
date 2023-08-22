package com.sos.msgroup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.sos.msgroup.ui.LocationDisclosureActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val firstTimeUser = "used"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

       AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        auth = FirebaseAuth.getInstance()

        Handler().postDelayed({

            checkSetting()

        }, 3000)
    }

    private fun checkSetting() {

        try{
           // super.onStart()
            val sh = getSharedPreferences("SecurifyPrefs", MODE_PRIVATE)
            val used = sh.getBoolean(firstTimeUser, false)

            if (!used) {
                val intent = Intent(this@SplashActivity, LocationDisclosureActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val user: FirebaseUser? = auth.currentUser
                user?.let {
                    startActivity(Intent(this, MainActivity::class.java))
                }

                if (user == null) {
                    startActivity(Intent(this, RegisterActivity::class.java))
                }

            }
        }catch(e:Exception){
            showMsg(e.toString())
            startActivity(Intent(this, RegisterActivity::class.java))
        }


    }

    private fun showMsg(msg: String) {
        val toast = Toast.makeText(this@SplashActivity, msg, Toast.LENGTH_LONG)
        toast.show()
    }
}