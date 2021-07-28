package com.cg.couponsapp.model

data class Users(
    val name : String,
    val email : String,
    val coins : Int,
    val phone : Long
){
    constructor() : this("","",0,0)
}