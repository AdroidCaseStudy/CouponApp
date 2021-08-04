package com.cg.couponsapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cg.couponsapp.model.Feed
import com.cg.couponsapp.utils.FeedAdapter
import com.cg.couponsapp.utils.MakeProgressBar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_feed_list.*
import java.text.SimpleDateFormat
import java.util.*


class FeedFragment : Fragment() {

    lateinit var rView: RecyclerView
    lateinit var fDatabase: FirebaseDatabase
    val feedList = mutableListOf<Feed>()
    lateinit var filePath : Uri
    lateinit var StorageReference : StorageReference
    lateinit var fAuth : FirebaseAuth
    lateinit var dialogView : View
    lateinit var name : String
    lateinit var materialDialog : androidx.appcompat.app.AlertDialog
    lateinit var listener : ValueEventListener
    lateinit var ref : DatabaseReference
    lateinit var pBar : ProgressBar
    var radioId = -1


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fDatabase = FirebaseDatabase.getInstance()
        fAuth = FirebaseAuth.getInstance()
        pBar = MakeProgressBar(activity?.findViewById(android.R.id.content)!!).make()
        pBar.visibility = View.VISIBLE
        val ref = fDatabase.reference.child("users").child("${fAuth.currentUser?.uid}")
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        name = snapshot.child("name").value.toString()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

        return inflater.inflate(R.layout.fragment_feed_list, container, false)
    }

    private fun createPost(context: Context) {
        val dialog = MaterialAlertDialogBuilder(context)
        val layoutInflater = LayoutInflater.from(context)
        val customView = layoutInflater.inflate(R.layout.custom_feed_dialog,null,false)
        val radioGroup = customView.findViewById<RadioGroup>(R.id.post_radio_group)
        val imageRadio = customView.findViewById<RadioButton>(R.id.post_radio_image)
        val videoRadio = customView.findViewById<RadioButton>(R.id.post_radio_video)
        val uploadButton = customView.findViewById<ImageButton>(R.id.post_upload)
        val imageView = customView.findViewById<ImageView>(R.id.post_imageView)
        val ytUrl = customView.findViewById<EditText>(R.id.post_ytTv)
        val postDesc = customView.findViewById<EditText>(R.id.post_desc)
        val postButton = customView.findViewById<Button>(R.id.post_button)
        val imageLayout = customView.findViewById<LinearLayout>(R.id.post_image_layout)
        val cancelDialoag = customView.findViewById<Button>(R.id.post_cancel)
        dialog.setView(customView)
        dialogView = customView
        materialDialog = dialog.create()
        materialDialog.setCancelable(false)
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            radioId = checkedId
            if(checkedId == R.id.post_radio_image){
                imageLayout.visibility = View.VISIBLE
                ytUrl.visibility = View.GONE
            }
            else if(checkedId == R.id.post_radio_video){
                imageLayout.visibility = View.GONE
                ytUrl.visibility = View.VISIBLE
            }
        }
        cancelDialoag.setOnClickListener {
            materialDialog.dismiss()
        }
        uploadButton.setOnClickListener{
            openGalleryForImage()
        }
        postButton.setOnClickListener {
            if(radioId==-1) Toast.makeText(customView.context,"Please select either an Image or a Video",Toast.LENGTH_LONG).show()
            else if(radioId == R.id.post_radio_image) {
                uploadImage()
                postButton.isEnabled = false
                postButton.isFocusable = false
                postButton.isClickable = false
            }
            else {
                uploadvideo()
                postButton.isEnabled = false
                postButton.isFocusable = false
                postButton.isClickable = false
            }
        }
        materialDialog.show()

    }

    private fun uploadvideo() {
        if(dialogView.findViewById<EditText>(R.id.post_ytTv).text.isNotEmpty()){
            pBar.visibility = View.VISIBLE
            postFeed(false,dialogView.findViewById<EditText>(R.id.post_ytTv).text.toString())
            pBar.visibility = View.GONE
        }else{
            Toast.makeText(dialogView.context,"Please enter the video url",Toast.LENGTH_LONG).show()
        }
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 1)
    }
    private fun uploadImage(){
        if (filePath != null) {
            pBar.visibility = View.VISIBLE
            StorageReference = FirebaseStorage.getInstance().reference
            val reference = StorageReference.child("images/"+UUID.randomUUID().toString())
            val uploadTask = reference.putFile(filePath).addOnSuccessListener {
                Toast.makeText(activity,"Post Uploaded",Toast.LENGTH_LONG).show()
            }.addOnFailureListener{
                Toast.makeText(activity,"${it.message}",Toast.LENGTH_LONG).show()
            }.continueWithTask{
                reference.downloadUrl
            }.addOnCompleteListener{
                if(it.isSuccessful){
                    pBar.visibility = View.GONE
                    postFeed(true,it.result.toString())
                }
            }
        }
    }
    private fun postFeed(isImage : Boolean,id:String){
        var calendar = Calendar.getInstance()
        val currentDate = SimpleDateFormat("dd-MM-yyyy")
        val feedDate = currentDate.format(calendar.time)
        val currentTime = SimpleDateFormat("HH:mm")
        val feedTime = currentTime.format(calendar.time)

        fDatabase.reference.child("feeds").child(UUID.randomUUID().toString()).setValue(Feed(name,
        id,isImage,dialogView.findViewById<EditText>(R.id.post_desc)?.text.toString(),fAuth.currentUser?.uid!!,feedDate,feedTime
            ))
        materialDialog.dismiss()
        rView.adapter?.notifyDataSetChanged()

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 1  && data != null
            && data.data != null){

            dialogView?.findViewById<ImageView>(R.id.post_imageView)?.setImageURI(data.data) // handle chosen image
            // Get the Uri of data
            filePath = data?.data!!
        }
    else{
        Toast.makeText(dialogView.context,"Error in selecting image",Toast.LENGTH_LONG).show()
    }
}
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        feedFAB.setOnClickListener{
            createPost(view.context)
        }
        rView = view.findViewById(R.id.feedRView)
        rView.layoutManager = LinearLayoutManager(activity)

        //FIREBASE

        ref = fDatabase.reference.child("feeds")
        listener = ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    feedList.clear()
                    for (child in snapshot.children) {
                        val feed =child.getValue(Feed::class.java)
                        feedList.add(feed!!)
                        Log.d("FeedList","$feedList")
                    }
                }
                rView.adapter = FeedAdapter(feedList)
                pBar.visibility = View.GONE

            }
            override fun onCancelled(error: DatabaseError) {
            }
        })


        //UPDATE ADAPTER CHECK
//        rView.adapter = FeedAdapter(listOf(Feed(url="https://i.imgur.com/YkjBfxg.jpeg"),Feed(url="https://www.youtube.com/watch?v=8MLa-Lh8lkU&ab_channel=EDMTDev",isImage = false),))
    }

    override fun onResume() {
        super.onResume()
        rView.adapter = FeedAdapter(feedList)
    }

    override fun onPause() {
        super.onPause()
        rView.adapter = null
        pBar.visibility = View.GONE
    }

    override fun onDetach() {
        super.onDetach()
        ref.removeEventListener(listener)
    }
}