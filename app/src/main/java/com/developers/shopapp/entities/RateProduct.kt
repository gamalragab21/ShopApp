package com.developers.shopapp.entities


data class RateProduct(
    val rateId :Int?=null,
    var userId :Int?=null,
    val productId :Int,
    val countRate:Double,
    val messageRate:String,
    val createAt:Long,
)
