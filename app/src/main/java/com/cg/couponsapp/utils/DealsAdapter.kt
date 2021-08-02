package com.cg.couponsapp.utils

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cg.couponsapp.R
import com.cg.couponsapp.model.Deals
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class DealsAdapter(val view: View) {

    val auth : FirebaseAuth = FirebaseAuth.getInstance()

    val dealReference : DatabaseReference = FirebaseDatabase.getInstance().reference.child("deals")
    val userReference : DatabaseReference = FirebaseDatabase.getInstance().reference.child("users")
    val currentUserId = auth.currentUser?.uid

    fun displayAllDeals(dealsList : RecyclerView) {
        val options = FirebaseRecyclerOptions.Builder<Deals>().setQuery(dealReference,Deals::class.java).build()

        val firebaseRecyclerAdapter : FirebaseRecyclerAdapter<Deals,DealViewHolder> =
            object : FirebaseRecyclerAdapter<Deals,DealViewHolder>(options){
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DealViewHolder {
                    val view : View = LayoutInflater.from(parent.context).inflate(R.layout.fragment_coupons,parent,false)
                    val viewHolder = DealViewHolder(view)
                    return viewHolder
                }

                override fun onBindViewHolder(holder: DealViewHolder, position: Int, model: Deals) {

                    val dealID = getRef(position).key
                    dealReference.child(dealID!!).addValueEventListener(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val title = snapshot.child("name").value.toString()
                            val desc = snapshot.child("desc").value.toString()
                            val image = snapshot.child("image").value.toString()
                            val link = snapshot.child("link").value.toString()
                            val rewards : Long = snapshot.child("rewards").value as Long

                            holder.dealTitle.setText(title)
                            holder.dealDesc.setText(desc)

                            Glide.with(holder.itemView.context).load(Uri.parse(image)).into(holder.dealImage)

                            holder.dealClaimB.setText("Get Deal")
                            holder.dealClaimB.setOnClickListener {
                                userReference.child(currentUserId!!).addValueEventListener(object : ValueEventListener{
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        val coins : Long = snapshot.child("coins").value as Long
                                        if(!snapshot.child("dealsClaimed").child(dealID).exists())
                                        {
                                            val dealMap = HashMap<String,Any>()
                                            dealMap["did"] = dealID
                                            dealMap["name"] = title
                                            dealMap["status"] = "claimed"

                                            userReference.child(currentUserId!!).child("dealsClaimed").child(dealID).updateChildren(dealMap).addOnCompleteListener {
                                                if(it.isSuccessful) {
                                                    val url = Uri.parse(link)
                                                    val i = Intent(Intent.ACTION_VIEW,url)
                                                    holder.itemView.context.startActivity(i)
                                                }
                                            }
                                            var newBalance = coins + rewards
                                            userReference.child(currentUserId).child("coins").setValue(newBalance).addOnCompleteListener {
                                                if(it.isSuccessful)
                                                    Toast.makeText(holder.itemView.context,"CoupCoins deposited!",Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    }
                                    override fun onCancelled(error: DatabaseError) {}
                                })
                                val url = Uri.parse(link)
                                val i = Intent(Intent.ACTION_VIEW,url)
                                holder.itemView.context.startActivity(i)
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {}
                    })
                }
            }

        dealsList.adapter = firebaseRecyclerAdapter
        firebaseRecyclerAdapter.startListening()
    }

    inner class DealViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val dealTitle = itemView.findViewById<TextView>(R.id.couponTitle)
        val dealDesc = itemView.findViewById<TextView>(R.id.couponDesc)
        val dealImage = itemView.findViewById<ImageView>(R.id.couponImg)
        val dealClaimB = itemView.findViewById<Button>(R.id.couponClaim)
    }
}