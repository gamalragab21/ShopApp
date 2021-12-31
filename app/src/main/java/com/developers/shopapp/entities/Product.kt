package com.developers.shopapp.entities

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose


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
    var coinType:String,
    val images:List<ProductImage>,
    val rating:List<RateProduct>?=null,
    var user:User?
):Parcelable {
    override fun describeContents(): Int =0

    override fun writeToParcel(p0: Parcel?, p1: Int) {
    }
}
