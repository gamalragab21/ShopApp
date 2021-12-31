package com.developers.shopapp.entities

import com.developers.shopapp.entities.Order


data class Tracking(
    var trackingId: Int? = -1,
    var status: Int = 0,
    val userId: Int,
    val createAt: Long,
    val orderId: Int,
    var restaurantId: Int,
    val order: Order?
)