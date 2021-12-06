package com.developers.shopapp.data.newtwork



import com.developers.shopapp.entities.AuthModel
import com.developers.shopapp.entities.MyResponse
import com.developers.shopapp.entities.Restaurant
import com.developers.shopapp.entities.User
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

}