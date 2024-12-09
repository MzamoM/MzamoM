package com.sos.msgroup
import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.sos.msgroup.model.User
import com.squareup.picasso.Picasso

class EditUserActivity : AppCompatActivity() {

    private lateinit var editTextFirstName: EditText
    private lateinit var editTextLastName: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPhoneNumber: EditText

    private lateinit var editTextNextKinFirstNameEdit: EditText
    private lateinit var editTextNextKinLastNameEdit: EditText
    private lateinit var editTextNextKinPhoneEdit: EditText
    private lateinit var profilePictureImageViewEdit : ImageView
    private lateinit var buttonSaveProfile: Button
    private lateinit var buttonDeleteProfile: Button

    private lateinit var role: String
    private lateinit var user: User

    private var radioGroup: RadioGroup? = null
    private lateinit var radioButton: RadioButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user)
        supportActionBar?.title = "Edit User Profile"
        user = intent.getParcelableExtra("user_data")!!

        //security

        role = user.type

        findViewByIds()

        updateViewedStatus()
    }

    private fun deleteUserAccount(email: String, password: String) {

        val database = FirebaseDatabase.getInstance().reference.child("users")
        database.child(user.id).removeValue().addOnSuccessListener {
            showMsg("User was deleted")
        }.addOnFailureListener {
            showMsg(it.toString())
            return@addOnFailureListener
        }
    }

    private fun updateViewedStatus(){
        if (this::user.isInitialized) {
            user.userCaptured = true

            val database = FirebaseDatabase.getInstance().reference.child("users")
            database.child(user.id).setValue(user).addOnSuccessListener {
            }.addOnFailureListener {
                showMsg(it.toString())
            }
        }else{
            showMsg("Something went wrong")
        }

    }

    private fun findViewByIds() {
        editTextFirstName = findViewById(R.id.edtFirstNameEdit)
        editTextLastName = findViewById(R.id.edtLastNameEdit)
        editTextEmail = findViewById(R.id.edtEmailAddressEdit)
        editTextPhoneNumber = findViewById(R.id.edtPhoneEdit)
        buttonSaveProfile = findViewById(R.id.btnSaveProfileEdit)
        buttonDeleteProfile = findViewById(R.id.btnDeleteProfileEdit)

        editTextNextKinFirstNameEdit = findViewById(R.id.editTextNextKinFirstNameEdit)
        editTextNextKinLastNameEdit = findViewById(R.id.editTextNextKinLastNameEdit)
        editTextNextKinPhoneEdit = findViewById(R.id.editTextNextKinPhoneEdit)
        profilePictureImageViewEdit = findViewById(R.id.img_UserProfilePictureEdit)

        radioGroup = findViewById(R.id.rdg_roles)


        var radioRoleAdmin: RadioButton = findViewById(R.id.rb_roleAdmin)
        var radioRoleCustomer: RadioButton = findViewById(R.id.rb_roleCustomer)
        var radioRoleSecurity: RadioButton = findViewById(R.id.rb_roleSecurity)

        showUserDetails()

        if (role.isNotEmpty()) {

            if (role.lowercase() == "customer") {
                radioRoleCustomer.isChecked = true
                radioRoleAdmin.isChecked = false
                radioRoleSecurity.isChecked = false
            }

           else if (role.lowercase() == "security") {
                radioRoleSecurity.isChecked = true
                radioRoleCustomer.isChecked = false
                radioRoleAdmin.isChecked = false
            }

            else {
                radioRoleAdmin.isChecked = true
                radioRoleCustomer.isChecked = false
                radioRoleSecurity.isChecked = false
            }
        }

        buttonSaveProfile.setOnClickListener(View.OnClickListener {
            saveUserInfo()
        })

        buttonDeleteProfile.setOnClickListener(View.OnClickListener {
            deleteUserAccount(user.email,user.password)
        })

    }

    private fun showUserDetails() {

        if (user.profileImage.isNotEmpty()) {
            Picasso.get().load(user.profileImage)
                .placeholder(R.drawable.ic_default_user)
                .into(profilePictureImageViewEdit)
        }

        if (user.nextKinFirstName.isNotBlank()) {
            editTextNextKinFirstNameEdit.setText(user.nextKinFirstName)
        }

        if (user.nextKinLastName.isNotBlank()) {
            editTextNextKinLastNameEdit.setText(user.nextKinLastName)
        }

        if (user.nextKinPhone.isNotBlank()) {
            editTextNextKinPhoneEdit.setText(user.nextKinPhone)
        }

        if (user.firstName.isNotBlank()) {
            editTextFirstName.setText(user.firstName)
        }

        if (user.lastName.isNotBlank()) {
            editTextLastName.setText(user.lastName)
        }


        if (user.phoneNumber.isNotBlank()) {
            editTextPhoneNumber.setText(user.phoneNumber)
        }


        if (user.email.isNotBlank()) {
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