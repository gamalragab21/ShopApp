package com.developers.shopapp.entities


import com.google.gson.annotations.SerializedName

data class UserData(
    @SerializedName("credit")
    val credit: Int,
    @SerializedName("email")
    val email: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("image")
    val image: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("points")
    val points: Int,
    @SerializedName("token")
    val token: String
)