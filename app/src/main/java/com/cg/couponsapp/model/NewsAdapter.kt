package com.cg.couponsapp.model

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cg.couponsapp.R
import java.text.SimpleDateFormat
import java.util.*

class NewsAdapter(val newsList: List<News>): RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val sourceT = view.findViewById<TextView>(R.id.newsSourceT)
        val titleT = view.findViewById<TextView>(R.id.newsTitleT)
        val imgV = view.findViewById<ImageView>(R.id.newsImgV)
        val descT = view.findViewById<TextView>(R.id.newDescT)
        val publishT = view.findViewById<TextView>(R.id.newsPublishedAtT)
        val cardView = view.findViewById<CardView>(R.id.newsCard)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.fragment_news,parent,false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val news = newsList[position]

        val time = TimeZone.getTimeZone("UTC")
        val df = SimpleDateFormat("dd-MM-yyyy 'at' HH:mm")
        df.timeZone = time
        val newTime =  df.format(Date())

        holder.sourceT.text = news.source.name
        holder.titleT.text = news.title
        holder.descT.text = news.description
        holder.publishT.text = newTime

        val imageUrl = news.urlToImage
        if(imageUrl.isNullOrEmpty()){}
        else{
            Glide.with(holder.itemView.context).load(Uri.parse(imageUrl)).placeholder(R.drawable.waiting).into(holder.imgV)
        }

        holder.cardView.setOnClickListener {
            Toast.makeText(it.context,"Working", Toast.LENGTH_LONG).show()
            val newUrl = Uri.parse(news.url)
            val intent = Intent(Intent.ACTION_VIEW,newUrl)
            it.context.startActivity(intent)
        }
    }

    override fun getItemCount() = newsList.size

}