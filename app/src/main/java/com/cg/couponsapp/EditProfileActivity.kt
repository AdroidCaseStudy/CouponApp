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
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_edit_profile.*
import java.io.IOException


class EditProfileActivity : AppCompatActivity(), View.OnClickListener {


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
                val phone = dataSnapshot.child(Constants.PHONE).value.toString()
                editPhoneET.setText(phone)

            }


            override fun onCancelled(databaseError: DatabaseError) {}
        })





        editEmailET.isEnabled = false
        editEmailET.setText(user?.email)

        val locationPref = applicationContext.getSharedPreferences(Constants.LOCATION_PREF,0)
        val location = locationPref.getString("Location",null)
        editLocationET.setText(location)


        val profile_image_ref = getSharedPreferences(Constants.PROFILE_IMAGE_REF,0)
        val uri = profile_image_ref?.getString("profile_image","")

        Glide.with(this )
            .load(uri)
            .into(profileImage)

        profileImage.setOnClickListener(this@EditProfileActivity)
        saveProfileButton.setOnClickListener(this@EditProfileActivity)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.profileImage ->{
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED){

                        Toast.makeText(this,"Storage permission already granted",
                            Toast.LENGTH_LONG).show()
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



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == Constants.READ_STORAGE_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0]== PackageManager.PERMISSION_GRANTED){

                Constants.showImageChooser(this)
            }else{
                Toast.makeText(this,"Storage permission Denied",Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if(requestCode== Constants.PICK_IMAGE_REQUEST_CODE){
                if(data!=null){
                    try{
                        selectedImageFileUri = data.data!!
                        // profileImg.setImageURI((selectedImageFileUri))
                        loadUserPicture(selectedImageFileUri!!,profileImage)
                    } catch (e: IOException){
                        e.printStackTrace()
                        Toast.makeText(this,"Image selection failed",Toast.LENGTH_LONG).show()

                    }
                }
            }
        }
    }

    fun loadUserPicture(imageUri: Uri, imageView: ImageView){
        try{
            Glide.with(applicationContext)
                .load(imageUri)
                .centerCrop()
                .into(imageView)
        }catch (e:IOException){
            e.printStackTrace()
        }
    }



    fun updateUserProfileDetails(){
        val userHashMap = HashMap<String,Any>()
        val username = editUserNameET.text.toString().trim{it<=' '}
        val phone = editPhoneET.text.toString().trim{it<= ' '}
        val location = editLocationET.text.toString().trim{it<=' '}

        if(username.isNotEmpty()){
            userHashMap[Constants.USERNAME] = username

        }
        if(phone.isNotEmpty()){
            userHashMap[Constants.PHONE] = phone.toLong()
        }
        if(location.isNotEmpty()){
            val locationPref = applicationContext.getSharedPreferences(Constants.LOCATION_PREF,0)
            val editor = locationPref.edit()
            editor.putString("Location",location)
            editor.apply()
        }





        updateUserProfile(this,userHashMap)
        Toast.makeText(this,"Profile updated successfully", Toast.LENGTH_LONG).show()

        startActivity(Intent(this@EditProfileActivity, MainActivity::class.java))
        finish()
    }

    fun updateUserProfile(activity: Activity, userHashMap:HashMap<String,Any>){


        val user = FirebaseAuth.getInstance().currentUser
        val reference = FirebaseDatabase.getInstance().getReference(Constants.USERS).child(user?.uid!!)
        reference.updateChildren(userHashMap)

        //reference.setValue(userHashMap)

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
                FirebaseAuth.getInstance().currentUser?.email
                        + "." + Constants.getFileExtension(activity,imageFileUri)
            )

        fStorage.putFile(imageFileUri!!).addOnSuccessListener { taskSnapshot ->
            Log.e("Firebase image URI",taskSnapshot.metadata!!.reference!!.downloadUrl.toString())

            taskSnapshot.metadata!!.reference!!.downloadUrl
                .addOnSuccessListener { uri ->
                 val profile_image_ref =  applicationContext
                     .getSharedPreferences(Constants.PROFILE_IMAGE_REF,0)

                 val editor = profile_image_ref.edit()
                 editor.putString("profile_image",uri.toString())
                 editor.apply()


                            imageUploadSuccess(uri.toString())
                }
        }
            .addOnFailureListener{exception ->

                Log.e(activity.javaClass.simpleName,
                    exception.message,exception)
            }
    }

}