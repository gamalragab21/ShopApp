package com.developers.shopapp.entities

import android.os.Parcel
import android.os.Parcelable


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
    val freeDelivery:Boolean?=true,
    var rateRestaurant:List<RateRestaurant>?=null,
    var inFav:Boolean?=false,
    var rateCount:Float?=0.0f,
    var user: User
):Parcelable {
    override fun describeContents(): Int =0


    override fun writeToParcel(p0: Parcel?, p1: Int) {

    }
}
