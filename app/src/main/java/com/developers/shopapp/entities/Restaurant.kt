package com.developers.shopapp.entities



data class Restaurant (
    val restaurantId :Int?=-1,
    val restaurantName:String,
    val imageRestaurant :String,
    val email:String,
    val password :String,
    val contact :String,
    val latitude :Double,
    val longitude :Double,
    val createAt :Long,
    val restaurantType:String,
    val rateRestaurant:List<RateRestaurant>?=null,
    var inFav:Boolean?=false,
    var rateCount:Float?=0.0f
)
