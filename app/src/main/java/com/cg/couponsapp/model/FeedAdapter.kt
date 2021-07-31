package com.cg.couponsapp.model

import android.net.Uri
import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YtFile
import com.bumptech.glide.Glide
import com.cg.couponsapp.R
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.MergingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.firebase.database.FirebaseDatabase


class FeedAdapter(val feedList: List<Feed>): RecyclerView.Adapter<FeedAdapter.ViewHolder>() {

    lateinit var videoUrl:String
    lateinit var audioAttributes: AudioAttributes
    lateinit var videoPlayer: SimpleExoPlayer
    lateinit var name : String

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val descpT = view.findViewById<TextView>(R.id.feedDescTV)
        val nameT = view.findViewById<TextView>(R.id.feedUNameTV)
        val videoV = view.findViewById<PlayerView>(R.id.feedPlayerView)
        val imageV = view.findViewById<ImageView>(R.id.feedImageView)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.fragment_feed,parent,false)
        audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.CONTENT_TYPE_MOVIE)
            .build()
        videoPlayer = SimpleExoPlayer.Builder(parent.context).build()
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val feed = feedList[position]


        holder.nameT.text = feed.name
        holder.descpT.text = feed.description

        //IMAGE
        if(feed.image) {
            holder.imageV.visibility =View.VISIBLE
            holder.videoV.visibility = View.INVISIBLE
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
            holder.imageV.visibility =View.INVISIBLE
            videoUrl = "${feed.url}"
            holder.videoV?.player = videoPlayer
            Log.d("FeedList","$videoUrl")
            playYoutubeVideo(videoUrl,holder,videoPlayer)
            //initializePlayer(holder)
        }


    }

    private fun playYoutubeVideo(videoUrl: String, holder: ViewHolder, videoPlayer: SimpleExoPlayer) {
        class YouTubeExtractor1 : YouTubeExtractor(holder.itemView.context) {
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

//        if(!holder.itemView.isFocused) {
//            Log.d("Firstfunc","Here")
//            videoPlayer?.run {
//                playbackPosition = this.currentPosition
//                currentWindow = this.currentWindowIndex
//                playWhenReady = this.playWhenReady
//                release()
//            }
//            videoPlayer.pause()
//        }
//        if(!holder.videoV.hasFocus())  {
//            Log.d("Firstfunc","Here2")
//            videoPlayer?.run {
//                playbackPosition = this.currentPosition
//                currentWindow = this.currentWindowIndex
//                playWhenReady = this.playWhenReady
//                release()
//            }
//            videoPlayer.pause()
//        }
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
    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        if(!holder.videoV.isFocused){
            Log.d("Firstfunc","Here2")
            if(videoPlayer.isPlaying)
                videoPlayer.pause()
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        if(!recyclerView.isFocused){
            Log.d("Firstfunc","Here")
//            if(videoPlayer.isPlaying)
//            {
//                videoPlayer.stop()
//            }
        }
    }

}