package com.developers.shopapp.repositories

import android.provider.Telephony
import com.developers.shopapp.data.newtwork.ApiShopService
import com.developers.shopapp.entities.*
import com.developers.shopapp.helpers.Resource
import com.developers.shopapp.helpers.safeCall
import com.developers.shopapp.qualifiers.IOThread
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultHomeRepository @Inject constructor(
    private val apiShopService: ApiShopService,
    @IOThread
    private val dispatcher: CoroutineDispatcher
) {
    suspend fun getAllRestaurants(): Resource<MyResponse<List<Restaurant>>> =
        withContext(dispatcher) {
            safeCall {
                val result = apiShopService.getAllRestaurants()
                Resource.Success(result)
            }
        }

    suspend fun getAllFavouritesRestaurant(): Resource<MyResponse<List<Restaurant>>> =
        withContext(dispatcher) {
            safeCall {
                val result = apiShopService.getAllFavouritesRestaurant()
                Resource.Success(result)
            }
        }

    suspend fun getAllRatingRestaurants(): Resource<MyResponse<List<Restaurant>>> =
        withContext(dispatcher) {
            safeCall {
                val result = apiShopService.getAllRatingRestaurants()
                Resource.Success(result)
            }
        }

    suspend fun getPopularRestaurant(): Resource<MyResponse<List<Restaurant>>> =
        withContext(dispatcher) {
            safeCall {
                val result = apiShopService.getPopularRestaurant()
                Resource.Success(result)
            }
        }

    suspend fun getNearlyRestaurant(
        latitude: Double,
        longitude: Double
    ): Resource<MyResponse<List<Restaurant>>> = withContext(dispatcher) {
        safeCall {
            val result = apiShopService.getNearlyRestaurant(latitude, longitude)
            Resource.Success(result)
        }
    }

    suspend fun filterRestaurant(restaurantName: String): Resource<MyResponse<List<Restaurant>>> =
        withContext(dispatcher) {
            safeCall {
                val result = apiShopService.filterRestaurant(restaurantName)
                Resource.Success(result)
            }
        }

    suspend fun deleteFavRestaurant(restaurantId: Int?): Resource<MyResponse<String>> =
        withContext(dispatcher) {
            safeCall {


                val result = apiShopService.deleteFavRestaurant(restaurantId ?: -1)
                Resource.Success(result)
            }
        }

    suspend fun setFavRestaurant(restaurantId: Int?): Resource<MyResponse<String>> =
        withContext(dispatcher) {
            safeCall {
                val hasmap = HashMap<String, Int>()
                hasmap.put("restaurantId", restaurantId ?: -1)
                val result = apiShopService.setFavRestaurant(hasmap)
                Resource.Success(result)
            }
        }

    suspend fun getCategoriesOfRestaurant(restaurantId: Int): Resource<MyResponse<List<Category>>> =
        withContext(dispatcher) {

            safeCall {

                val result = apiShopService.getCategoriesOfRestaurant(restaurantId)
                Resource.Success(result)
            }

        }

    suspend fun getProductOfCategory(restaurantId: Int, categoryId: Int) = withContext(dispatcher) {

        safeCall {

            val result = apiShopService.getProductOfCategory(restaurantId, categoryId)
            Resource.Success(result)
        }
    }

    suspend fun getProductForYou(restaurantId: Int): Resource<MyResponse<List<Product>>> =
        withContext(dispatcher) {
            safeCall {

                val result = apiShopService.getProductForYou(restaurantId)
                Resource.Success(result)
            }
        }

    suspend fun deleteFavProduct(productId: Int?): Resource<MyResponse<String>> =
        withContext(dispatcher) {
            safeCall {
                val result = apiShopService.deleteFavProduct(productId ?: -1)
                Resource.Success(result)
            }
        }

    suspend fun setFavProduct(productId: Int?): Resource<MyResponse<String>> =
        withContext(dispatcher) {
            safeCall {
                val hasmap = HashMap<String, Int>()
                hasmap["productId"] = productId ?: -1
                val result = apiShopService.setFavProduct(hasmap)
                Resource.Success(result)
            }
        }

    suspend fun getAllFavouritesProduct(): Resource<MyResponse<List<Product>>> =
        withContext(dispatcher) {
            safeCall {
                val result = apiShopService.getAllFavouritesProduct()
                Resource.Success(result)
            }
        }

    suspend fun getPopularProduct(): Resource<MyResponse<List<Product>>> =
        withContext(dispatcher) {
            safeCall {
                val result = apiShopService.getPopularProduct()
                Resource.Success(result)
            }
        }

    suspend fun getProfile(): Resource<MyResponse<User>> = withContext(dispatcher) {

        safeCall {
            val result=apiShopService.getMyProfile()
            Resource.Success(result)
        }

    }

    suspend fun setupRatingMyRestaurant(rateRestaurant: RateRestaurant): Resource<MyResponse<RateRestaurant>> = withContext(dispatcher) {
        safeCall {
            val result=apiShopService.setupRatingMyRestaurant(rateRestaurant)
            Resource.Success(result)
        }
    }

    suspend fun updateRateRestaurant (rateRestaurant: RateRestaurant): Resource<MyResponse<RateRestaurant>> = withContext(dispatcher) {
        safeCall {
            val result=apiShopService.updateRateRestaurant(rateRestaurant)
            Resource.Success(result)
        }
    }

    suspend fun findMyRestaurant(restaurantId: Int): Resource<MyResponse<Restaurant>> = withContext(dispatcher) {
        safeCall {
            val result=apiShopService.findMyRestaurant(restaurantId)
            Resource.Success(result)
        }
    }

   suspend fun findProductById(productId: Int): Resource<MyResponse<Product>> = withContext(dispatcher){
        safeCall {
            val result=apiShopService.findProductById(productId)
            Resource.Success(result)
        }
    }

    suspend fun setupRatingMyProduct(rateProduct: RateProduct): Resource<MyResponse<RateProduct>> = withContext(dispatcher) {
        safeCall {
            val result=apiShopService.setupRatingMyProduct(rateProduct)
            Resource.Success(result)
        }
    }

    suspend fun updateRateProduct(rateProduct: RateProduct): Resource<MyResponse<RateProduct>> = withContext(dispatcher) {
        safeCall {
            val result=apiShopService.updateRateProduct(rateProduct)
            Resource.Success(result)
        }
    }


}