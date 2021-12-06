package com.developers.shopapp.entities



data class RateRestaurant(
    val rateId :Int?=null,
    var userId :Int?=null,
    val restaurantId :Int,
    val countRate:Double,
    val createAt:Long
)
