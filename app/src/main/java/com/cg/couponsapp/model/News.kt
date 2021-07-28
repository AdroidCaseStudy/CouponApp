package com.cg.couponsapp.model

data class Sources(val name:String)
data class News(val source: Sources, val title: String, val description: String,val url:String, val urlToImage: String, val publishedAt: String)
data class Articles(val articles: List<News>)
