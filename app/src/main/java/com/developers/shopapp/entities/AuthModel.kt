package com.developers.shopapp.entities


import com.google.gson.annotations.SerializedName

data class AuthModel(
    @SerializedName("data")
    val userData: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Boolean
)