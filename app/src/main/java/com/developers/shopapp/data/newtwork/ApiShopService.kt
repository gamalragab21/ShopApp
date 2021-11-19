package com.developers.shopapp.data.newtwork



import com.developers.shopapp.entities.AuthModel
import com.developers.shopapp.entities.Product
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import kotlin.collections.HashMap

interface ApiShopService {

    @POST("login")
    suspend fun loginUser( @Body userinfo: HashMap<String, String>): AuthModel

    @POST("register")
    suspend fun register( @Body userinfo: HashMap<String, String>): AuthModel

    @POST("logout")
    suspend fun logout():AuthModel

    @POST("verify-email")
    suspend fun verifyEmail(@Body email:HashMap<String, String>):AuthModel

    @POST("verify-code")
    suspend fun verifyCode(@Body hashMap: HashMap<String, String>): AuthModel

    @POST("reset-password")
    suspend fun resetPassword(@Body hashMap: HashMap<String, String>): AuthModel

    @GET("products")
    suspend fun getProducts(): Product

}