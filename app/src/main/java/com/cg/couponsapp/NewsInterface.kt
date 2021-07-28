package com.cg.couponsapp

import com.cg.couponsapp.model.Articles
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsInterface {

    @GET("v2/top-headlines")
    fun getTopHeadlines(@Query("country") country:String
                        , @Query("category") category:String, @Query("apiKey") key:String) : Call<Articles>

    companion object{
        val BASE_URL = "https://newsapi.org/"

        fun getInstance() : NewsInterface{

            val builder = Retrofit.Builder()
            builder.addConverterFactory(GsonConverterFactory.create())
            builder.baseUrl(BASE_URL)

            val retrofit = builder.build()
            return retrofit.create(NewsInterface::class.java)

        }
    }
}