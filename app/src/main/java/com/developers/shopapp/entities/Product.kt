package com.developers.shopapp.entities


data class Product(
    val productId:Int?=-1,
    val categoryId:Int,
    var restaurantId:Int?=-1,
    val productName:String,
    val productPrice:Double,
    val createAt:Long,
    val freeDelivery:Boolean,
    val productDescription:String,
    var inFav:Boolean?=false,
    val inCart:Boolean?=false,
    var rateCount:Double?=0.0,
    val images:List<ProductImage>,
    val rating:List<RateProduct>?=null

)
