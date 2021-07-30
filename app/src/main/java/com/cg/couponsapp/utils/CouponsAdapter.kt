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
import com.cg.couponsapp.model.Coupons
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CouponsAdapter(val view: View) {

    val auth : FirebaseAuth = FirebaseAuth.getInstance()

    val couponReference : DatabaseReference = FirebaseDatabase.getInstance().reference.child("coupons")
    val usersReference : DatabaseReference = FirebaseDatabase.getInstance().reference.child("users")
    val currentUserId = auth.currentUser?.uid

    fun displayAllCoupons(couponsList: RecyclerView) {
        val options = FirebaseRecyclerOptions.Builder<Coupons>().setQuery(couponReference,Coupons::class.java).build()

        val firebaseRecyclerAdapter : FirebaseRecyclerAdapter<Coupons,CouponViewHolder> =
            object : FirebaseRecyclerAdapter<Coupons,CouponViewHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponViewHolder {
                val view : View = LayoutInflater.from(parent.context).inflate(R.layout.fragment_coupons,parent,false)
                val viewHolder = CouponViewHolder(view)
                return viewHolder
            }

            override fun onBindViewHolder(holder: CouponViewHolder, position: Int, model: Coupons) {

                val couponID = getRef(position).key
                couponReference.child(couponID!!).addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val title = snapshot.child("name").value.toString()
                        val desc = snapshot.child("desc").value.toString()
                        val image = snapshot.child("image").value.toString()
                        val link = snapshot.child("link").value.toString()
                        val rewards : Long = snapshot.child("rewards").value as Long

                        holder.couponDesc.setText(desc)
                        holder.couponTitle.setText(title)

                        Glide.with(holder.itemView.context).load(Uri.parse(image)).into(holder.couponImage)

                        holder.couponClaimB.setOnClickListener {
                            usersReference.child(currentUserId!!).addValueEventListener(object : ValueEventListener{
                                override fun onDataChange(snapshot: DataSnapshot) {

                                    val coins : Long = snapshot.child("coins").value as Long

                                    if(!snapshot.child("couponsClaimed").hasChild(couponID))
                                    {
                                        val couponMap = HashMap<String,Any>()
                                        couponMap["cid"] = couponID
                                        couponMap["name"] = title

                                        usersReference.child(currentUserId!!).child("couponsClaimed").child(couponID).updateChildren(couponMap).addOnCompleteListener {
                                            if(it.isSuccessful) {
                                                val url = Uri.parse(link)
                                                val i = Intent(Intent.ACTION_VIEW,url)
                                                holder.itemView.context.startActivity(i)
                                            }
                                        }

                                        var newBalance = coins + rewards
                                        usersReference.child(currentUserId).child("coins").setValue(newBalance).addOnCompleteListener {
                                            if(it.isSuccessful)
                                            {
                                                Toast.makeText(holder.itemView.context,"CoupCoins deposited!",Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    }
                                    else {
                                        Toast.makeText(holder.itemView.context,"Coupon already claimed",Toast.LENGTH_LONG).show()
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {}

                            })
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {}
                })
            }
        }

        couponsList.adapter = firebaseRecyclerAdapter
        firebaseRecyclerAdapter.startListening()
    }

    inner class CouponViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val couponTitle = itemView.findViewById<TextView>(R.id.couponTitle)
        val couponDesc = itemView.findViewById<TextView>(R.id.couponDesc)
        val couponImage = itemView.findViewById<ImageView>(R.id.couponImg)
        val couponClaimB = itemView.findViewById<Button>(R.id.couponClaim)
    }

}