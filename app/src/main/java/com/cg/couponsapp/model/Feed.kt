package com.cg.couponsapp.model

data class Feed(val name: String="",
                val url: String="",
                val image:Boolean,
                val description:String ="",val uid:String="",val date:String="",val time:String=""){
    constructor():this("","",true,"")
}