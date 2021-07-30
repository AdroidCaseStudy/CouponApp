package com.cg.couponsapp.model

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cg.couponsapp.R
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory

class FeedAdapter(val feedList: List<Feed>): RecyclerView.Adapter<FeedAdapter.ViewHolder>() {

    lateinit var videoUrl:String
    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val descpT = view.findViewById<TextView>(R.id.feedDescTV)
        val nameT = view.findViewById<TextView>(R.id.feedUNameTV)
        val videoV = view.findViewById<PlayerView>(R.id.feedPlayerView)
        val imageV = view.findViewById<ImageView>(R.id.feedImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.fragment_feed,parent,false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val feed = feedList[position]


        holder.nameT.text = feed.name
        holder.descpT.text = feed.description

        //IMAGE
        if(feed.isImage) {
            holder.imageV.visibility =View.VISIBLE
            val imageUrl = feed.url
            if (imageUrl.isNullOrEmpty()) {
            } else {
                Glide.with(holder.itemView.context).load(Uri.parse(imageUrl))
                    .placeholder(R.drawable.waiting).into(holder.imageV)
            }
        }

        //VIDEO EXOPLAYER
        else{
            holder.videoV.visibility =View.VISIBLE
            videoUrl = feed.url
            initializePlayer(holder)
        }


    }

    //VIDEO EXOPLAYER
    fun initializePlayer(holder: ViewHolder) {
        var videoPlayer: SimpleExoPlayer? = null
        videoPlayer = SimpleExoPlayer.Builder(holder.itemView.context).build()
        holder.videoV?.player = videoPlayer
        buildMediaSource(holder)?.let {
            videoPlayer?.prepare(it)
        }
    }
    fun buildMediaSource(holder: ViewHolder): MediaSource? {
        val dataSourceFactory = DefaultDataSourceFactory(holder.itemView.context, "sample")
        return ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(Uri.parse(videoUrl))
    }


    override fun getItemCount() = feedList.size

}