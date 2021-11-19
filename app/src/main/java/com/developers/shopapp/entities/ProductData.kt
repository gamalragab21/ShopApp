package com.developers.shopapp.entities


import com.google.gson.annotations.SerializedName

data class ProductData(
    @SerializedName("description")
    val description: String,
    @SerializedName("discount")
    val discount: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("image")
    val image: String,
    @SerializedName("images")
    val images: List<String>,
    @SerializedName("in_cart")
    val inCart: Boolean,
    @SerializedName("in_favorites")
    val inFavorites: Boolean,
    @SerializedName("name")
    val name: String,
    @SerializedName("old_price")
    val oldPrice: Int,
    @SerializedName("price")
    val price: Int
)