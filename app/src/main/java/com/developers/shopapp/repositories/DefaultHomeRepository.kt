package com.developers.shopapp.repositories

import com.developers.shopapp.data.newtwork.ApiShopService
import com.developers.shopapp.entities.MyResponse
import com.developers.shopapp.entities.Restaurant
import com.developers.shopapp.helpers.Resource
import com.developers.shopapp.helpers.safeCall
import com.developers.shopapp.qualifiers.IOThread
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultHomeRepository  @Inject constructor(
    private val apiShopService: ApiShopService,
    @IOThread
    private val dispatcher: CoroutineDispatcher
) {
    suspend fun getAllRestaurants(): Resource<MyResponse<List<Restaurant>>> = withContext(dispatcher){
        safeCall {
            val result=apiShopService.getAllRestaurants()
            Resource.Success(result)
        }
    }

    suspend fun deleteFavRestaurant(restaurantId: Int?): Resource<MyResponse<String>> = withContext(dispatcher) {
        safeCall {


            val result=apiShopService.deleteFavRestaurant(restaurantId?:-1)
            Resource.Success(result)
        }
    }

    suspend fun setFavRestaurant(restaurantId: Int?): Resource<MyResponse<String>> = withContext(dispatcher) {
        safeCall {
            val hasmap=HashMap<String,Int>()
            hasmap.put("restaurantId",restaurantId?:-1)
            val result=apiShopService.setFavRestaurant(hasmap)
            Resource.Success(result)
        }
    }


}