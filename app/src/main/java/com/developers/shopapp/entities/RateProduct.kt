package com.developers.shopapp.entities

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable


data class RateProduct(
    val rateId :Int?=null,
    var userId :Int?=null,
    val productId :Int,
    val countRate:Double,
    val messageRate:String,
    val createAt:Long,
    var user:User?=null
):Parcelable {
    override fun describeContents(): Int =0

    override fun writeToParcel(p0: Parcel?, p1: Int) {}
}
