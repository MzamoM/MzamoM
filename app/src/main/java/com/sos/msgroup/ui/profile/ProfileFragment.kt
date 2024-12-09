package com.sos.msgroup.ui.profile

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.R
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.sos.msgroup.databinding.FragmentProfileBinding
import com.sos.msgroup.model.User
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream
import java.io.IOException


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

    private lateinit var  editTextNextKinPhoneNumber : EditText
    private lateinit var  editTextNextKinFirstName : EditText
    private lateinit var  editTextNextKinLastName : EditText

    private lateinit var  profilePictureImageView : ImageView
    private lateinit var progressDialog: ProgressDialog

    private var mImageUri: Uri? = null
    private lateinit var ImageUrl: String

    private var requestCode: Int = 10
    private val cameraRequest = 1888

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

         editTextFirstName = binding.editTextFirstName
         editTextLastName = binding.editTextLastName
         editTextIDNumber = binding.editTextIDNumber
         editTextPhoneNumber = binding.editTextPhoneNumber

        editTextNextKinPhoneNumber = binding.editTextNextKinPhone
        editTextNextKinFirstName = binding.editTextNextKinFirstName
        editTextNextKinLastName = binding.editTextNextKinLastName

         profilePictureImageView = binding.profileImagesSetting

        progressDialog = ProgressDialog(activity)
        progressDialog.setTitle("Please Wait")
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)

        initializeDbRef()

        binding.btnSave.setOnClickListener {
            saveUserInfo()
        }

        profilePictureImageView.setOnClickListener(View.OnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, cameraRequest)
        })

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

                    if (user.profileImage != null && user.profileImage.isNotEmpty()) {
                        Picasso.get().load(user.profileImage)
                            .placeholder(com.sos.msgroup.R.drawable.ic_default_user)
                            .into(profilePictureImageView)
                    }

                    if (user.nextKinPhone != null && user.nextKinPhone.isNotEmpty()) {
                        editTextNextKinPhoneNumber.setText(user.nextKinPhone)
                    }

                    if (user.nextKinFirstName != null && user.nextKinFirstName.isNotEmpty()) {
                        editTextNextKinFirstName.setText(user.nextKinFirstName)
                    }

                    if (user.nextKinLastName != null && user.nextKinLastName.isNotEmpty()) {
                        editTextNextKinLastName.setText(user.nextKinLastName)
                    }

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

            if(editTextIDNumber.text.toString().isNotEmpty() && editTextIDNumber.text.toString().length == 13){
                user.ID_Number = editTextIDNumber.text.toString()
            }

            if(editTextPhoneNumber.text.toString().isNotEmpty() && editTextPhoneNumber.text.toString().length == 10){
                user.phoneNumber = editTextPhoneNumber.text.toString()
            }

            if (editTextNextKinPhoneNumber.text.toString().isNotEmpty()) {
                user.nextKinPhone = editTextNextKinPhoneNumber.text.toString()
            }

            if (editTextNextKinFirstName.text.toString().isNotEmpty()) {
                user.nextKinFirstName = editTextNextKinFirstName.text.toString()
            }

            if (editTextNextKinLastName.text.toString().isNotEmpty()) {
                user.nextKinLastName = editTextNextKinLastName.text.toString()
            }


            val progressDialog = ProgressDialog(activity)
            progressDialog.setMessage("Please wait...")
            progressDialog.show()
            progressDialog.setCancelable(false)
            progressDialog.show()


            if (mImageUri != null) {


                try {
                    val filePath = FirebaseStorage.getInstance().reference.child("UserProfile").child(user.id)
                    var bitmap: Bitmap? = null
                    try {
                        bitmap =  MediaStore.Images.Media.getBitmap(activity?.contentResolver, mImageUri)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    val baos = ByteArrayOutputStream()
                    bitmap!!.compress(Bitmap.CompressFormat.JPEG, 20, baos)
                    val data = baos.toByteArray()
                    val uploadTask = filePath.putBytes(data)
                    uploadTask.addOnFailureListener(OnFailureListener {
                        showMsg(it.toString())
                        return@OnFailureListener
                    })

                    uploadTask.addOnSuccessListener(OnSuccessListener {
                        filePath.downloadUrl.addOnSuccessListener(OnSuccessListener { downloadUrl ->
                            ImageUrl = downloadUrl.toString()
                            user.profileImage = ImageUrl

                            if (user != null) {

                                val database = FirebaseDatabase.getInstance().reference.child("users")

                                database.child(user.id).setValue(user)
                                    .addOnSuccessListener {
                                    }
                                    .addOnFailureListener {

                                    }

                            }

                        })
                    })
                } catch (e: Exception) {
                    Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show()
                }
            }

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        try {
            if (requestCode == requestCode && resultCode == AppCompatActivity.RESULT_OK) {
                val imageUri = data?.data
                mImageUri = imageUri
                profilePictureImageView.setImageURI(mImageUri)
            }

            if (requestCode == cameraRequest) {

                val imageUri = data?.data
                mImageUri = imageUri

                val photo: Bitmap = data?.extras?.get("data") as Bitmap
                profilePictureImageView.setImageBitmap(photo)
            }
        }catch (e:Exception){
            showMsg(e.toString())
        }

    }

}