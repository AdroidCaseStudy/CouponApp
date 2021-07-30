package com.cg.couponsapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.cg.couponsapp.utils.NetworkUtil
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    var firebaseAuth : FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        firebaseAuth = FirebaseAuth.getInstance()

        Handler().postDelayed({
            val networkState = NetworkUtil().checkStatus(this,this.intent)
            if(!networkState ) {}
            else if(firebaseAuth!!.currentUser == null){
                finish()
                val i = Intent(this, SignInActivity::class.java)
                startActivity(i)
                finish()
            }
            else {
                val i = Intent(this,NavigationActivity::class.java)
                startActivity(i)
                finish()
            }
        },2000)
    }
}