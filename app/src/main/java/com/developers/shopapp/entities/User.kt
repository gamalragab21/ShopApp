package com.developers.shopapp.entities




data class User(
    val id:Int?=-1,
    val username:String,
    val email:String,
    val image:String,
    val password:String,
    val latitude:Double,
    val longitude:Double,
    val mobile:String,
    val createAt:Long
)

