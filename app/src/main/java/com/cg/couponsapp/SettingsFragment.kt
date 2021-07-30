package com.cg.couponsapp

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_settings_tab.view.*
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_settings.view.*


class SettingsFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.activity_settings_tab, container, false)


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
            val builder: AlertDialog.Builder = AlertDialog.Builder(activity)

            builder.setMessage("Do you want to Logout ?")

            builder.setTitle("Logout")

            builder.setCancelable(false)

            builder
                .setPositiveButton(
                    "Yes"
                ) { dialog, which ->
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(activity, SignInActivity::class.java)
                    startActivity(intent)
                }

            builder
                .setNegativeButton(
                    "No",
                    { dialog, which ->
                        dialog.cancel()
                    })

            val alertDialog: AlertDialog = builder.create()

            alertDialog.show()

        }


        return view

    }

}