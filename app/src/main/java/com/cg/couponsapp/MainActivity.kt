package com.cg.couponsapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cg.couponsapp.utils.NetworkUtil
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    lateinit var fAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val networkState = NetworkUtil().checkStatus(this,this.intent)
        if(networkState) {
            fAuth = FirebaseAuth.getInstance()
            if (fAuth.currentUser != null) {
                startActivity(Intent(this,NavigationActivity::class.java))
                finish()
            }
        }

        signUpAct.setOnClickListener {
            startActivity(Intent(this,SignUpActivity::class.java))
        }
    }

}