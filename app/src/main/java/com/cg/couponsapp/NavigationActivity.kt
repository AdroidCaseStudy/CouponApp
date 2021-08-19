package com.cg.couponsapp

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.cg.couponsapp.databinding.ActivityMainBinding
import com.cg.couponsapp.databinding.ActivityNavigationBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import me.ibrahimsn.lib.SmoothBottomBar


class NavigationActivity : AppCompatActivity(){

    lateinit var mauth: FirebaseAuth
    lateinit var userReference : DatabaseReference
    private lateinit var navController: NavController
    private lateinit var binding: ActivityNavigationBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navController = findNavController(R.id.bottom_nav_fragment)
        mauth = FirebaseAuth.getInstance()

        //this is a test comment

        setupActionBarWithNavController(navController)
        setupSmoothBottomMenu()

        binding.bottomBar.setOnItemReselectedListener {  }
    }

    private fun setupSmoothBottomMenu() {
        val popupMenu = PopupMenu(this, null)
        popupMenu.inflate(R.menu.bottom_nav_menu)
        val menu = popupMenu.menu
        binding.bottomBar.setupWithNavController(menu, navController)

    }



    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onStart() {
        super.onStart()
        val currentUser : FirebaseUser? = mauth.currentUser
        if(currentUser==null)
        {
            sendToLogin()
        }
    }

    private fun sendToLogin() {
        val i = Intent(this, SignInActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(i)
        finish()
    }
}