package com.cg.couponsapp.utils

import android.content.Context
import android.net.Uri
import android.util.*
import android.view.*
import android.widget.*
import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YtFile
import com.bumptech.glide.Glide
import com.cg.couponsapp.Constants
import com.cg.couponsapp.R
import com.cg.couponsapp.model.Feed
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.source.*
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.lang.Exception


class FeedAdapter(val feedList: List<Feed>): RecyclerView.Adapter<FeedAdapter.ViewHolder>() {

    var videoUrl:String? = null
    lateinit var audioAttributes: AudioAttributes
    var videoPlayer: SimpleExoPlayer? = null
    var hiddenVideoPlayer: SimpleExoPlayer? = null
    lateinit var name : String

    val storageRef : StorageReference = FirebaseStorage.getInstance().getReference()
    val userRef : DatabaseReference = FirebaseDatabase.getInstance().reference.child("users")

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val descpT = view.findViewById<TextView>(R.id.feedDescTV)
        val nameT = view.findViewById<TextView>(R.id.feedUNameTV)
        val videoV = view.findViewById<PlayerView>(R.id.feedPlayerView)
        val imageV = view.findViewById<ImageView>(R.id.feedImageView)
        val profileV = view.findViewById<ImageView>(R.id.feedProfileP)
        val timeT = view.findViewById<TextView>(R.id.feedTimeTV)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        videoPlayer = SimpleExoPlayer.Builder(parent.context).build()
        val v = inflater.inflate(R.layout.fragment_feed,parent,false)
        audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.CONTENT_TYPE_MOVIE)
            .build()

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val feed = feedList[position]


        holder.nameT.text = feed.name
        holder.descpT.text = feed.description
        holder.timeT.text = feed.date + " " + feed.time

        userRef.child(feed.uid).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.hasChild("profileImage")){
                    val profileImg = snapshot.child("profileImage").value.toString()
                    Glide.with(holder.itemView.context).load(profileImg).into(holder.profileV)
                    userRef.removeEventListener(this)
                }
            }

            override fun onCancelled(error: DatabaseError) {}

        })

        //IMAGE
        if(feed.image) {
            holder.imageV.visibility =View.VISIBLE
            holder.videoV.visibility = View.INVISIBLE
            val imageUrl = feed.url
            if (imageUrl.isNullOrEmpty()) {
            } else {
                Glide.with(holder.itemView.context).load(Uri.parse(imageUrl))
                    .placeholder(R.drawable.waiting).into(holder.imageV)
                holder.imageV.setOnClickListener {
                    val dialog = MaterialAlertDialogBuilder(it.context)
                    val layoutInflater = LayoutInflater.from(it.context)
                    val customView = layoutInflater.inflate(R.layout.custom_feed_fullscreen_dialog,null,false)
                    val imageView = customView.findViewById<ImageView>(R.id.fullscreen_imageView)
                    val videoView = customView.findViewById<PlayerView>(R.id.fullscreen_videoView)
                    videoView.visibility = View.INVISIBLE
                    imageView.visibility = View.VISIBLE
                    dialog.setView(customView)
                    val materialDialog = dialog.create()
                    Glide.with(customView.context).load(Uri.parse(imageUrl))
                        .placeholder(R.drawable.waiting).into(imageView)
                    imageView.minimumHeight = 0
                    imageView.minimumWidth = 0
                    materialDialog.show()

                }
            }
        }

        //VIDEO EXOPLAYER
        else{
            holder.videoV.visibility =View.VISIBLE
            holder.imageV.visibility =View.INVISIBLE
            videoUrl = "${feed.url}"
            videoPlayer = SimpleExoPlayer.Builder(holder.itemView.context).build()
            holder.videoV?.player = videoPlayer
            Log.d("FeedList","$videoUrl")
            playYoutubeVideo(videoUrl!!,holder.itemView.context,videoPlayer!!)

            holder.videoV.setOnClickListener {
                holder.videoV.player?.pause()
                val dialog = MaterialAlertDialogBuilder(it.context)
                val layoutInflater = LayoutInflater.from(it.context)
                val customView = layoutInflater.inflate(R.layout.custom_feed_fullscreen_dialog,null,false)
                val imageView = customView.findViewById<ImageView>(R.id.fullscreen_imageView)
                val videoView = customView.findViewById<PlayerView>(R.id.fullscreen_videoView)
                imageView.visibility = View.INVISIBLE
                videoView.visibility = View.VISIBLE
                dialog.setView(customView)
                val materialDialog = dialog.create()
                hiddenVideoPlayer = SimpleExoPlayer.Builder(customView.context).build()
                videoView.player = hiddenVideoPlayer
                videoUrl = "${feed.url}"
                playYoutubeVideo(videoUrl!!,customView.context,hiddenVideoPlayer!!)

                materialDialog.setOnDismissListener {
                    hiddenVideoPlayer!!.stop()
                }
                materialDialog.setOnCancelListener {
                    hiddenVideoPlayer!!.stop()
                }
                materialDialog.show()
            }

        }


    }

    private fun playYoutubeVideo(videoUrl: String, context: Context, videoPlayer: SimpleExoPlayer) {
        class YouTubeExtractor1 : YouTubeExtractor(context) {
            override fun onExtractionComplete(
                ytFiles: SparseArray<YtFile>?,
                videoMeta: VideoMeta?
            ) {
                if(ytFiles!=null)
                {
                    val videoTag = 18
                    val audioTag = 140
                    val audioSource:MediaSource =ProgressiveMediaSource.Factory(DefaultHttpDataSource.Factory()).createMediaSource(
                        MediaItem.fromUri(ytFiles.get(audioTag).url))
                    val videoSource:MediaSource =ProgressiveMediaSource.Factory(DefaultHttpDataSource.Factory()).createMediaSource(
                        MediaItem.fromUri(ytFiles.get(videoTag).url))

                    videoPlayer.setMediaSource(MergingMediaSource(true,videoSource,audioSource),true)
                    videoPlayer.prepare()
//                    videoPlayer.playWhenReady=true
                }
            }
        }

        YouTubeExtractor1().extract(videoUrl)
        videoPlayer.setAudioAttributes(audioAttributes,true)


    }




    override fun getItemCount() = feedList.size
    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        if(!holder.videoV.isFocused){
            Log.d("Firstfunc","Here2 \n $videoUrl")
            holder.videoV.player?.pause()
            hiddenVideoPlayer?.pause()
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        if(!recyclerView.isFocused){
            if(videoPlayer!=null)
            {
//                Log.d("Firstfunc","Here")
                videoPlayer?.stop()
                hiddenVideoPlayer?.pause()
            }
        }
    }


}