package com.cg.couponsapp


import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_sign_in.*


class SignInActivity : AppCompatActivity(){

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        auth = FirebaseAuth.getInstance()
        signInBtn.setOnClickListener {
            doLogin()
        }
        NewUserT.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }
    }
    private fun doLogin() {
        if(emailLoginE.text.isEmpty()){
            emailLoginE.error ="Please enter email"
            emailLoginE.requestFocus()
            return
        }
        if(passwordLoginE.text.isEmpty()){
            passwordLoginE.error ="Please enter password"
            passwordLoginE.requestFocus()
            return
        }

        //----LOGIN USER---
        auth.signInWithEmailAndPassword(emailLoginE.text.toString(), passwordLoginE.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Toast.makeText(this, "Authentication Successful", Toast.LENGTH_SHORT).show()
                    updateUI(user)
                } else { //wrong details
                    Toast.makeText(this, "${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
    private fun updateUI(currentUser : FirebaseUser?){
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }
}

