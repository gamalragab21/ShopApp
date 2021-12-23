package com.developers.shopapp.entities

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

@Entity(tableName = "Cart")
data class ProductCart(



    val foodName: String,

    val foodImage: String,

    val foodPrice: Double,

    val foodDiscount: Double,

    val coinType: String,

    var foodQuality: Int,

    val foodDelivery: Boolean,

    val userPhone: String,

    val userId: Int,

    val createAt: Long,

    ):Parcelable{
    @PrimaryKey(autoGenerate = true)
    var foodId: Int?=null
    override fun describeContents(): Int =0

    override fun writeToParcel(p0: Parcel?, p1: Int) {
    }
}