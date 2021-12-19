package com.developers.shopapp.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

@Entity(tableName = "Cart")
data class ProductCart(



    val foodName: String,

    val foodPrice: Double,

    val foodQuality: Int,

    val userPhone: String,

    val userId: Int,

    val createAt: Long,

    ){
    @PrimaryKey(autoGenerate = true)
    var foodId: Int?=null
}