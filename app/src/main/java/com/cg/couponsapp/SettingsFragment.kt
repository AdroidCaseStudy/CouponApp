package com.cg.couponsapp


import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import com.bumptech.glide.Glide
import com.cg.couponsapp.utils.MakeProgressBar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

import kotlinx.android.synthetic.main.activity_settings_tab.*
import kotlinx.android.synthetic.main.activity_settings_tab.view.*


class SettingsFragment : Fragment() {


    lateinit var fAuth : FirebaseAuth
    lateinit var pBar : ProgressBar
    lateinit var usersRef : DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.activity_settings_tab, container, false)

        usersRef = FirebaseDatabase.getInstance().reference.child("users")
        fAuth = FirebaseAuth.getInstance()
        pBar = MakeProgressBar(activity?.findViewById(android.R.id.content)!!).make()
        pBar.visibility = View.VISIBLE
        view.editProf_TV.setOnClickListener{
            val intent = Intent(activity, EditProfileActivity::class.java)
            startActivity(intent)
        }

        view.changePass_TV.setOnClickListener{
            val intent = Intent(activity, ChangePasswordActivity::class.java)
            startActivity(intent)
        }

        view.sendFeedback_TV.setOnClickListener {


            val email = Intent(Intent.ACTION_SEND)

            val s1={"cashcoup@gmail.com"}.toString()
            email.putExtra(Intent.EXTRA_EMAIL, s1)
            email.putExtra(Intent.EXTRA_SUBJECT, "Feedback")
            email.type = "message/rfc822"

            startActivity(Intent.createChooser(email, "Choose an Email client :"))

        }

        view.rateUs_TV.setOnClickListener {
            val packageName = "com.cg.couponsapp"
            val uri: Uri = Uri.parse("market://details?id=$packageName")
            val goToMarket = Intent(Intent.ACTION_VIEW, uri)
            // To count with Play market backstack, After pressing back button,
            // to taken back to our application, we need to add following flags to intent.
            goToMarket.addFlags(
                Intent.FLAG_ACTIVITY_NO_HISTORY or
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK
            )
            try {
                startActivity(goToMarket)
            } catch (e: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=$packageName")
                    )
                )
            }
        }


        view.aboutUs_TV.setOnClickListener {
            val intent = Intent(activity, AboutUsActivity::class.java)
            startActivity(intent)
        }


        view.logout_TV.setOnClickListener {

            val builder = MaterialAlertDialogBuilder(it.context)
            val layoutInflater = LayoutInflater.from(context)
            val customView = layoutInflater.inflate(R.layout.logout_dialog,null,false)
            builder.setView(customView)

            val materialDialog = builder.create()
            customView.findViewById<Button>(R.id.settings_exit_button).setOnClickListener {
                activity?.finish()
            }
            customView.findViewById<Button>(R.id.settings_logout_button).setOnClickListener{
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(activity, SignInActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }
            materialDialog.show()

        }


        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = FirebaseAuth.getInstance().currentUser
        val reference = FirebaseDatabase.getInstance().getReference(Constants.USERS).child(user?.uid!!)

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val username =
                    dataSnapshot.child(Constants.USERNAME).value.toString()

                profile_user_name.setText(username)
                profile_user_email.setText(dataSnapshot.child("email").value.toString())
                coin_count.setText(dataSnapshot.child("coins").value.toString())
                reference.removeEventListener(this)
                pBar.visibility = View.GONE

            }


            override fun onCancelled(databaseError: DatabaseError) {}
        })


//        profile_user_email.setText(fAuth.currentUser?.email.toString())

        val profile_image_ref = activity?.getSharedPreferences(Constants.PROFILE_IMAGE_REF,0)
        val uri = profile_image_ref?.getString("profile_image","")

        Glide.with(this )
            .load(uri)
            .into(profile_user_image)

        usersRef.child(user.uid).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.hasChild("profileImage")){
                    val profileImg = snapshot.child("profileImage").value.toString()
                    Glide.with(this@SettingsFragment).load(profileImg).into(profile_user_image)
                }
            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }

    override fun onPause() {
        super.onPause()
        pBar.visibility = View.GONE
    }

}