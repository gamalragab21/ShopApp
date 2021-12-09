package com.developers.shopapp.entities



data class Category(
    val categoryId :Int?=-1,
    var restaurantId :Int?=null,
    val categoryName: String
)
