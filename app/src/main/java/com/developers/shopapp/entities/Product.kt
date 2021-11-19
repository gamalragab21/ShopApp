package com.developers.shopapp.entities


import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("data")
    val productPagesData: ProductPagesData,
    @SerializedName("message")
    val message: Any,
    @SerializedName("status")
    val status: Boolean
)