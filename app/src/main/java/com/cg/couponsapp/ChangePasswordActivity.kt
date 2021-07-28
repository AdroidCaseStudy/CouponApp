package com.cg.couponsapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_change_password.*


class ChangePasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

    }

    fun buttonClick(view: View){
        when(view.id){
            R.id.changePasswordBtn ->{
                changePassword()
            }
        }
    }

    private fun changePassword() {
        if (validatePasswordDetails()) {
            val user = Firebase.auth.currentUser
            val credential = EmailAuthProvider
                .getCredential(user?.email!!, editTextCurrentPassword.text.toString().trim { it<=' ' })

            val newPassword = editTextNewPassword.text.toString().trim{it<= ' '}

            user?.reauthenticate(credential)?.addOnSuccessListener {

                user!!.updatePassword(newPassword)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                this, "User password updated.",
                                Toast.LENGTH_LONG
                            ).show()

                        } else {
                            Toast.makeText(
                                this, "Couldn't update password. Please try again",
                                Toast.LENGTH_LONG
                            ).show()
                        }
//                        startActivity(Intent(this, UserProfileActivity::class.java))
                    }
            }
                .addOnFailureListener {
                    Toast.makeText(this,"Incorrect Current Password",
                        Toast.LENGTH_LONG).show()
                }

        }
    }


    private fun validatePasswordDetails():Boolean{

        if(TextUtils.isEmpty(editTextNewPassword.text.toString().trim { it<= ' ' })) {
            Toast.makeText(this,"Please enter New Password",Toast.LENGTH_LONG)
                .show()
            return false
        }

        if(TextUtils.isEmpty(editTextConfirmNewPassword.text.toString().trim { it<= ' ' })) {
            Toast.makeText(this,"Please enter Confirm New Password"
                ,Toast.LENGTH_LONG)
                .show()
            return false
        }


        if(editTextConfirmNewPassword.text.toString() != editTextNewPassword.text.toString()) {
            Toast.makeText(this,"New Password is not matching with Confirm New Password"
                ,Toast.LENGTH_LONG)
                .show()
            return false
        }

        if(editTextCurrentPassword.text.toString() == editTextNewPassword.text.toString()) {
            Toast.makeText(this,"New Password shouldn't be same as Current Password"
                ,Toast.LENGTH_LONG)
                .show()
            return false
        }



        else {
            return true

        }

    }
}