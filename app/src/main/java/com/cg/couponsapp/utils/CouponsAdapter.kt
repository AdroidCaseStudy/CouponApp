package com.cg.couponsapp.utils

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
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

                        holder.couponDesc.setText(desc)
                        holder.couponTitle.setText(title)

                        Glide.with(holder.itemView.context).load(Uri.parse(image)).into(holder.couponImage)
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
    }

}