package com.cg.couponsapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.fragment_settings.view.*

class EditProfileActivity : AppCompatActivity(), View.OnClickListener {

    private val firestore = FirebaseFirestore.getInstance()

    private var selectedImageFileUri: Uri? = null
    private var userProfileImageURL: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        supportActionBar?.hide()

        val user = FirebaseAuth.getInstance().currentUser
        val reference = FirebaseDatabase.getInstance().getReference(Constants.USERS).child(user?.uid!!)

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val username =
                        dataSnapshot.child(Constants.USERNAME).value.toString()

                    editUserNameET.setText(username)

            }


            override fun onCancelled(databaseError: DatabaseError) {}
        })





        editEmailET.isEnabled = false
        editEmailET.setText(user?.email)

        profileImage.setOnClickListener(this@EditProfileActivity)
        saveProfileButton.setOnClickListener(this@EditProfileActivity)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.profileImage ->{
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED){
                    //showErrorSnackbar("Storage permission already granted",false,profileImg)
                    Constants.showImageChooser(this)
                }else{
                    ActivityCompat.requestPermissions(
                        this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        Constants.READ_STORAGE_PERMISSION_CODE
                    )
                }
            }

            R.id.saveProfileButton ->{




                if(validateUserDetails()){

                    if(selectedImageFileUri != null) {
                        uploadImgaeToCloudStorage(this, selectedImageFileUri)
                    }
                    else{
                        updateUserProfileDetails()
                    }

                }

            }

        }

    }

    fun updateUserProfileDetails(){
        val userHashMap = HashMap<String,Any>()
        val username = editUserNameET.text.toString().trim{it<=' '}
        val mobile = editPhoneET.text.toString().trim{it<= ' '}
        val location = editLocationET.text.toString().trim{it<=' '}

        if(username.isNotEmpty()){
            userHashMap[Constants.USERNAME] = username

        }
        if(mobile.isNotEmpty()){
            userHashMap[Constants.MOBILE] = mobile.toLong()
        }
        if(location.isNotEmpty()){
            userHashMap[Constants.LOCATION] = location
        }





        updateUserProfile(this,userHashMap)
        Toast.makeText(this,"Profile updated successfully", Toast.LENGTH_LONG).show()

        startActivity(Intent(this@EditProfileActivity, MainActivity::class.java))
        finish()
    }

    fun updateUserProfile(activity: Activity, userHashMap:HashMap<String,Any>){
        firestore.collection(Constants.USERS).document(getCurrentUserID())
            .update(userHashMap)
            .addOnFailureListener { e->

                Log.e(
                    activity.javaClass.simpleName, "Error while updating user details",e
                )
            }
    }

    fun getCurrentUserID():String{
        val currentUser = FirebaseAuth.getInstance().currentUser

        var currentUserID = ""
        if(currentUser!=null){
            currentUserID = currentUser.uid

        }
        return currentUserID

    }

    private fun validateUserDetails():Boolean{
        return when{
            TextUtils.isEmpty(editPhoneET.text.toString().trim{it<=' '}) ->{
                Toast.makeText(this,"Please enter mobile no.", Toast.LENGTH_LONG).show()
                false
            }
            else ->{
                true
            }
        }
    }
    fun imageUploadSuccess(imageURL : String){
        //hideProgressDialog()

        userProfileImageURL = imageURL

        updateUserProfileDetails()
    }


    fun uploadImgaeToCloudStorage(activity: Activity, imageFileUri: Uri?){
        val fStorage = FirebaseStorage.getInstance()
            .reference.child(
                Constants.PROFILE_IMAGE + System.currentTimeMillis()
                        + "." + Constants.getFileExtension(activity,imageFileUri)
            )

        fStorage.putFile(imageFileUri!!).addOnSuccessListener { taskSnapshot ->
            Log.e("Firebase image URI",taskSnapshot.metadata!!.reference!!.downloadUrl.toString())

            taskSnapshot.metadata!!.reference!!.downloadUrl
                .addOnSuccessListener { uri ->
                    Log.e("Downloadable image URI", uri.toString())
                    when(activity){
                        is EditProfileActivity -> {
                            activity.imageUploadSuccess(uri.toString())
                        }
                    }
                }
        }
            .addOnFailureListener{exception ->

                Log.e(activity.javaClass.simpleName,
                    exception.message,exception)
            }
    }

}