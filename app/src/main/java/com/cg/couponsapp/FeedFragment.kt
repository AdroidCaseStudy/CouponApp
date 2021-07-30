package com.cg.couponsapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cg.couponsapp.model.Feed
import com.cg.couponsapp.model.FeedAdapter
import com.google.firebase.database.FirebaseDatabase


class FeedFragment : Fragment() {

    lateinit var rView: RecyclerView
    lateinit var fDatabase: FirebaseDatabase
    val feedList = mutableListOf<Feed>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rView = view.findViewById(R.id.feedRView)
        rView.layoutManager = LinearLayoutManager(activity)

        //FIREBASE
      /*  fDatabase = FirebaseDatabase.getInstance()
        val ref = fDatabase.reference.child("feed")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (child in snapshot.children) {
                        val feed =child.getValue(Feed::class.java)
                        feedList.add(feed!!)
                    }
                    rView.adapter = FeedAdapter(feedList)
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })*/


        //UPDATE ADAPTER CHECK
        rView.adapter = FeedAdapter(listOf(Feed(url="https://i.imgur.com/YkjBfxg.jpeg"),Feed(url="https://www.youtube.com/watch?v=8MLa-Lh8lkU&ab_channel=EDMTDev",isImage = false),))
    }

    override fun onPause() {
        super.onPause()

    }
}