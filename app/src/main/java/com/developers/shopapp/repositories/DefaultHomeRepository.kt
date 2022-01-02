package com.developers.shopapp.repositories

import com.developers.shopapp.data.local.CartDao
import com.developers.shopapp.data.local.SearchHistoryDao
import com.developers.shopapp.data.newtwork.ApiShopService
import com.developers.shopapp.entities.*
import com.developers.shopapp.helpers.Resource
import com.developers.shopapp.helpers.safeCall
import com.developers.shopapp.qualifiers.IOThread
import com.developers.shopapp.entities.Order
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultHomeRepository @Inject constructor(
    private val apiShopService: ApiShopService,
    private val searchHistoryDao: SearchHistoryDao,
    private val cartDao: CartDao,
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
            val result = apiShopService.getMyProfile()
            Resource.Success(result)
        }

    }

    suspend fun setupRatingMyRestaurant(rateRestaurant: RateRestaurant): Resource<MyResponse<RateRestaurant>> =
        withContext(dispatcher) {
            safeCall {
                val result = apiShopService.setupRatingMyRestaurant(rateRestaurant)
                Resource.Success(result)
            }
        }

    suspend fun updateRateRestaurant(rateRestaurant: RateRestaurant): Resource<MyResponse<RateRestaurant>> =
        withContext(dispatcher) {
            safeCall {
                val result = apiShopService.updateRateRestaurant(rateRestaurant)
                Resource.Success(result)
            }
        }

    suspend fun findMyRestaurant(restaurantId: Int): Resource<MyResponse<Restaurant>> =
        withContext(dispatcher) {
            safeCall {
                val result = apiShopService.findMyRestaurant(restaurantId)
                Resource.Success(result)
            }
        }

    suspend fun findProductById(productId: Int): Resource<MyResponse<Product>> =
        withContext(dispatcher) {
            safeCall {
                val result = apiShopService.findProductById(productId)
                Resource.Success(result)
            }
        }

    suspend fun setupRatingMyProduct(rateProduct: RateProduct): Resource<MyResponse<Int>> =
        withContext(dispatcher) {
            safeCall {
                val result = apiShopService.setupRatingMyProduct(rateProduct)
                Resource.Success(result)
            }
        }

    suspend fun updateRateProduct(rateProduct: RateProduct): Resource<MyResponse<Int>> =
        withContext(dispatcher) {
            safeCall {
                val result = apiShopService.updateRateProduct(rateProduct)
                Resource.Success(result)
            }
        }

    suspend fun getAllSearchHistory(countItem: Int): Resource<List<SearchHistory>> =
        withContext(dispatcher) {
            safeCall {
                val result = searchHistoryDao.getAllSearchHistory(countItem)
                Resource.Success(result)
            }
        }

    suspend fun insertNewSearchKeyWord(searchHistory: SearchHistory): Resource<Long> =
        withContext(dispatcher) {
            safeCall {

                val result = searchHistoryDao.insertSearchHistory(searchHistory)
                Resource.Success(result)
            }
        }

    suspend fun deleteSearchHistory(id: Int): Resource<Int> = withContext(dispatcher) {
        safeCall {
            val result = searchHistoryDao.deleteSearchHistory(id)
            Resource.Success(result)
        }
    }

    suspend fun clearAllSearchHistory(): Resource<Int> = withContext(dispatcher) {
        safeCall {
            val result = searchHistoryDao.clearAllSearchHistory()
            Resource.Success(result)
        }
    }

    suspend fun addProductToCart(productCart: ProductCart): Resource<Long> =
        withContext(dispatcher) {
            safeCall {
                val result = cartDao.insertNewCart(productCart)
                Resource.Success(result)
            }
        }

    suspend fun getAllCarts(): Resource<List<ProductCart>> = withContext(dispatcher) {
        safeCall {
            val result = cartDao.getAllCarts()
            Resource.Success(result)
        }
    }

    suspend fun findItemCart(productId: Int): Resource<ProductCart> = withContext(dispatcher) {
        safeCall {
            val result = cartDao.findItemProductById(productId)
            Resource.Success(result)
        }
    }

    suspend fun updateItemQuality(itemProduct: ProductCart): Resource<Int> =
        withContext(dispatcher) {
            safeCall {
                val result = cartDao.updateCart(itemProduct)
                Resource.Success(result)
            }
        }

    suspend fun createOrder(order: Order): Resource<MyResponse<Int>> = withContext(dispatcher) {
        safeCall {
            val result = apiShopService.createOrder(order)
            Resource.Success(result)
        }
    }

    suspend fun comingOrders(): Resource<MyResponse<List<Order>>> = withContext(dispatcher) {
        safeCall {
            val result = apiShopService.comingOrders()
            Resource.Success(result)
        }
    }

    suspend fun preOrders(): Resource<MyResponse<List<Order>>> = withContext(dispatcher) {
        safeCall {
            val result = apiShopService.preOrders()
            Resource.Success(result)
        }
    }

    suspend fun historyOrders(): Resource<MyResponse<List<Order>>> = withContext(dispatcher) {
        safeCall {
            val result = apiShopService.historyOrders()
            Resource.Success(result)
        }
    }

    suspend fun deleteOrder(orderId: Int): Resource<MyResponse<Int>> = withContext(dispatcher) {
        safeCall {
            val result = apiShopService.deleteOrder(orderId)
            Resource.Success(result)
        }
    }


    suspend fun updateOrder(orderId: Int, orderType: Int): Resource<MyResponse<Int>> =
        withContext(dispatcher) {
            safeCall {
                val result = apiShopService.updateOrder(orderId, orderType)
                Resource.Success(result)
            }
        }

    suspend fun deleteIteFromCart(itemId: Int): Resource<Int> = withContext(dispatcher) {
        safeCall {
            val result = cartDao.deleteCart(itemId)
            Resource.Success(result)
        }
    }
    suspend fun getOrderTracking(orderId: Int): Resource<MyResponse<List<Tracking>>> = withContext(dispatcher) {
                safeCall {
                    val result = apiShopService.getOrderTracking(orderId)
                    Resource.Success(result)
                }

            }

  suspend  fun filterProduct(filterName: String): Resource<MyResponse<List<Product>>> = withContext(dispatcher) {
        safeCall {
            val result = apiShopService.filterProduct(filterName)
            Resource.Success(result)
        }

    }
}