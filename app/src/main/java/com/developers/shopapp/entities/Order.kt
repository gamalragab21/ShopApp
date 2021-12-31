package com.developers.shopapp.entities



data class Order(
    val orderId: Int?=-1,
    var userId: Int?=null,
    val restaurantId: Int,
    val foodId: Int,
    val productName: String,
    val productPrice: Double,
    val productDistCount: Double,
    val productQuantity: Int,
    val freeDelivery: Boolean,
    val createAt: Long,
    val coinType: String,
    val foodImage: String,
    var orderType:Int=0,
    val user: User? = null,
    val restaurant: Restaurant? = null

)
/* order type  equal 0 , 1,-1
* 0 pre-order
* 1 oncoming
* -1 history
* */
