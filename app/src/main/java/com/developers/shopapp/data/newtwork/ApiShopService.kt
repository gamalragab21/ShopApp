package com.developers.shopapp.data.newtwork



import com.developers.shopapp.entities.*
import com.developers.shopapp.entities.Order
import retrofit2.http.*
import kotlin.collections.HashMap

interface ApiShopService {

    @POST("users/login")
    suspend fun loginUser( @Body userinfo: HashMap<String, String>): MyResponse<String>

    @POST("users/register")
    suspend fun register( @Body userinfo: User): MyResponse<String>

    @POST("logout")
    suspend fun logout():AuthModel

    @POST("verify-email")
    suspend fun verifyEmail(@Body email:HashMap<String, String>):AuthModel

    @POST("verify-code")
    suspend fun verifyCode(@Body hashMap: HashMap<String, String>): AuthModel

    @POST("reset-password")
    suspend fun resetPassword(@Body hashMap: HashMap<String, String>): AuthModel

    @GET("restaurants")
    suspend fun getAllRestaurants(): MyResponse<List<Restaurant>>

    @DELETE("restaurants/fav/delete")
    suspend fun deleteFavRestaurant(@Query("restaurantId") restaurantId: Int): MyResponse<String>

    @POST("restaurants/createFav")
    suspend fun setFavRestaurant(@Body restaurantId: HashMap<String, Int>): MyResponse<String>

    @GET("restaurants/favourites")
    suspend fun getAllFavouritesRestaurant():MyResponse<List<Restaurant>>

    @GET("restaurants/rating")
    suspend fun getAllRatingRestaurants():MyResponse<List<Restaurant>>

    @GET("restaurants/popular")
    suspend fun getPopularRestaurant(): MyResponse<List<Restaurant>>

    @POST("restaurants/nearlyLocation")
    suspend fun getNearlyRestaurant(@Query("latitude") latitude:Double,
                                    @Query("longitude") longitude:Double): MyResponse<List<Restaurant>>

    @GET("restaurants/filter")
    suspend fun filterRestaurant(@Query("restaurantName") restaurantName: String): MyResponse<List<Restaurant>>

    @GET("category")
    suspend fun getCategoriesOfRestaurant(@Query("restaurantId") restaurantId: Int): MyResponse<List<Category>>

    @GET("product")
    suspend fun getProductOfCategory(@Query("restaurantId") restaurantId: Int,
                                     @Query("categoryId")  categoryId: Int): MyResponse<List<Product>>

    @GET("product/for you")
    suspend fun getProductForYou(@Query("restaurantId") restaurantId: Int): MyResponse<List<Product>>

    @DELETE("product/fav/delete")
    suspend fun deleteFavProduct(@Query("productId") productId: Int): MyResponse<String>

    @POST("product/setFav")
    suspend fun setFavProduct(@Body productId: HashMap<String, Int>): MyResponse<String>

    @GET("product/favorites")
    suspend fun getAllFavouritesProduct(): MyResponse<List<Product>>

    @GET("product/popular")
    suspend fun getPopularProduct(): MyResponse<List<Product>>

    @GET("users/me")
    suspend fun getMyProfile(): MyResponse<User>

    @POST("restaurants/rate")
    suspend fun setupRatingMyRestaurant(@Body rateRestaurant: RateRestaurant): MyResponse<RateRestaurant>

    @GET("restaurants/findById")
    suspend fun findMyRestaurant(@Query("restaurantId") restaurantId: Int): MyResponse<Restaurant>

    @PUT("restaurants/updateRate")
    suspend fun updateRateRestaurant(@Body rateRestaurant: RateRestaurant): MyResponse<RateRestaurant>

    @GET("product/findByID")
    suspend fun findProductById(@Query("productId") productId: Int): MyResponse<Product>

    @POST("product/rate")
    suspend fun setupRatingMyProduct(@Body rateProduct: RateProduct): MyResponse<RateProduct>

    @PUT("product/updateRate")
    suspend fun updateRateProduct(@Body rateProduct: RateProduct): MyResponse<RateProduct>

    @GET("users/findUserById")
    suspend fun findUserById(@Query("userId") userId: Int): User

    @POST("orders/createOrder")
    suspend fun createOrder(@Body order: Order): MyResponse<Int>

    @GET("orders/comingOrders")
    suspend fun comingOrders(): MyResponse<List<Order>>

    @GET("orders/preOrders")
    suspend fun preOrders(): MyResponse<List<Order>>

    @GET("orders/historyOrders")
    suspend fun historyOrders(): MyResponse<List<Order>>

    @DELETE("orders/deleteOrder")
    suspend fun deleteOrder(@Query("orderId") orderId:Int): MyResponse<Int>

    @PUT("orders/updateOrder")
    suspend fun updateOrder(@Query("orderId") orderId:Int,
                            @Query("orderType") orderType:Int): MyResponse<Int>



    @POST("orders/updateTracking")
    suspend fun updateTracking(@Body tracking: Tracking): MyResponse<Int>

    @GET("orders/orderTracking")
    suspend fun getOrderTracking(
        @Query("orderId") orderId:Int
    ): MyResponse<List<Tracking>>

}