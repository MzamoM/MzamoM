package com.sos.msgroup
import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.sos.msgroup.model.User
class EditUserActivity : AppCompatActivity() {

    private lateinit var editTextFirstName: EditText
    private lateinit var editTextLastName: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPhoneNumber: EditText
    private lateinit var buttonSaveProfile: Button

    private lateinit var role: String
    private lateinit var user: User

    private var radioGroup: RadioGroup? = null
    private lateinit var radioButton: RadioButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user)
        supportActionBar?.title = "Edit User Profile"
        user = intent.getParcelableExtra("user_data")!!

        role = user.type

        findViewByIds()
    }

    private fun findViewByIds() {
        editTextFirstName = findViewById(R.id.edtFirstNameEdit)
        editTextLastName = findViewById(R.id.edtLastNameEdit)
        editTextEmail = findViewById(R.id.edtEmailAddressEdit)
        editTextPhoneNumber = findViewById(R.id.edtPhoneEdit)
        buttonSaveProfile = findViewById(R.id.btnSaveProfileEdit)
        radioGroup = findViewById(R.id.rdg_roles)


        var radioRoleAdmin: RadioButton = findViewById(R.id.rb_roleAdmin)
        var radioRoleCustomer: RadioButton = findViewById(R.id.rb_roleCustomer)

        showUserDetails()

        if (role != null && role.isNotEmpty()) {

            if (role.lowercase() == "customer") {
                radioRoleCustomer.isChecked = true
                radioRoleAdmin.isChecked = false
            }  else {
                radioRoleAdmin.isChecked = true
                radioRoleCustomer.isChecked = false
            }
        }

        buttonSaveProfile.setOnClickListener(View.OnClickListener {
            saveUserInfo()
        })

    }

    private fun showUserDetails() {

        if (user.firstName!!.isNotBlank()) {
            editTextFirstName.setText(user.firstName)
        }

        if (user.lastName!!.isNotBlank()) {
            editTextLastName.setText(user.lastName)
        }


        if (user.phoneNumber!!.isNotBlank()) {
            editTextPhoneNumber.setText(user.phoneNumber)
        }


        if (user.email!!.isNotBlank()) {
            editTextEmail.setText(user.email)
        }
    }

    private fun saveUserInfo() {

        val progressDialog = ProgressDialog(this@EditUserActivity)
        progressDialog.setMessage("Please wait...")
        progressDialog.show()
        progressDialog.setCancelable(false)
        progressDialog.show()

        val intSelectButton: Int = radioGroup!!.checkedRadioButtonId
        radioButton = findViewById(intSelectButton)

        if (radioButton.text != null) {

            if (radioButton.text.toString().lowercase() != user.type.lowercase()) {
                user.type = radioButton.text.toString()

                val database = FirebaseDatabase.getInstance().reference.child("users")

                database.child(user.id).setValue(user)
                    .addOnSuccessListener {
                        showMsg("user updated successfully")
                        progressDialog.hide()
                    }
                    .addOnFailureListener {
                        progressDialog.hide()
                    }

            } else {
                progressDialog.hide()
                showMsg("No details were changed")
            }
        }

    }

    private fun showMsg(msg: String) {
        val toast = Toast.makeText(this@EditUserActivity, msg, Toast.LENGTH_LONG)
        toast.show()
    }
}