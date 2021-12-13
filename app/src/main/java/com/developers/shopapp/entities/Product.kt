package com.developers.shopapp.entities

import android.os.Parcel
import android.os.Parcelable


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

):Parcelable {
    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(p0: Parcel?, p1: Int) {
        TODO("Not yet implemented")
    }
}
