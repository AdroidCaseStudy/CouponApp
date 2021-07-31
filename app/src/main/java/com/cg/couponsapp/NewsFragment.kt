package com.cg.couponsapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cg.couponsapp.model.Articles
import com.cg.couponsapp.utils.NewsAdapter
import com.cg.couponsapp.utils.NewsInterface
import kotlinx.android.synthetic.main.fragment_news_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewsFragment: Fragment(), AdapterView.OnItemSelectedListener {

    lateinit var rView: RecyclerView
    lateinit var newsSpinner: Spinner
    var keywords = ""

    val keywordsList = mutableListOf<String>("Business", "Entertainment", "General", "Health", "Science",
        "Sports", "Technology")
    lateinit var adapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_news_list,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rView = view.findViewById(R.id.newsRView)
        rView.layoutManager = LinearLayoutManager(activity)

        newsSpinner = view.findViewById(R.id.newsSpinner)
        adapter = activity?.let { ArrayAdapter(it,android.R.layout.simple_spinner_dropdown_item,keywordsList) }!!
        newsSpinner.adapter = adapter
        newsSpinner.onItemSelectedListener = this
    }

    inner class TopNewsCallback : Callback<Articles> {
        override fun onResponse(call: Call<Articles>, response: Response<Articles>) {
            //Toast.makeText(this@MainActivity,"${response.body()}",Toast.LENGTH_LONG).show()
            if(response.isSuccessful) {
                newsPBar?.visibility=View.INVISIBLE
                val news = response.body()
                news?.articles?.let {
                    rView.adapter = NewsAdapter(it)
                }
            }
        }

        override fun onFailure(call: Call<Articles>, t: Throwable) {
            Log.d("MainActivity","${t.message}: ${t.cause}")
            Toast.makeText(activity,"Failed to get data: ${t.message}", Toast.LENGTH_LONG).show()
        }

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when(keywordsList[position])
        {
            "Business" -> {keywords = "business"}
            "Entertainment" -> {keywords = "entertainment"}
            "General" -> {keywords = "general"}
            "Health" -> {keywords = "health"}
            "Science" -> {keywords = "science"}
            "Sports" -> {keywords = "sports"}
            "Technology" -> {keywords = "technology"}
        }

        val key = "7aca9a156b0e410a905d3bbd83ca8472"
        //Toast.makeText(this,"$country asdad",Toast.LENGTH_LONG).show()
        //country = "in"
        val request = NewsInterface.getInstance().getTopHeadlines("in",keywords, key)
        request.enqueue(TopNewsCallback())
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}