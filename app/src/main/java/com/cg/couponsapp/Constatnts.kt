package com.cg.couponsapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constants {
    const val USERS:String = "users"
    const val APP_PREFERENCES: String = "AppPrefs"
    const val LOGGED_IN_USERNAME: String = "logged_in_username"
    const val FIRST_NAME:String = "firstName"
    const val LAST_NAME:String = "lastName"

    const val EXTRA_USER_DETAILS:String = "extra_user_details"
    const val READ_STORAGE_PERMISSION_CODE = 2
    const val PICK_IMAGE_REQUEST_CODE = 1

    const val MALE:String = "Male"
    const val FEMALE:String = "Female"

    const val MOBILE:String = "mobile"
    const val GENDER:String = "gender"
    const val IMAGE:String = "image"
    const val LOCATION :String = "location"

    const val COMPLETE_PROFILE : String = "profileCompleted"

    const val PROFILE_IMAGE:String = "profile_image"

    fun showImageChooser(activity: Activity){
        val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        activity.startActivityForResult(i, PICK_IMAGE_REQUEST_CODE)
    }

    fun getFileExtension(activity: Activity, uri: Uri?):String?{
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }


}