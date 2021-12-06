package com.developers.shopapp.entities

import com.google.android.gms.maps.model.LatLng

data class UserInfoDB(
    val email: String = "",
    val password: String = "",
    val token: String = "",
    val latLng: LatLng
)