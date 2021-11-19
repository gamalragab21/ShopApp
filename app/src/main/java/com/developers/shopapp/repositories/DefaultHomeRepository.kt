package com.developers.shopapp.repositories

import com.developers.shopapp.data.newtwork.ApiShopService
import com.developers.shopapp.entities.Product
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
    suspend fun getAllProduct(): Resource<Product> = withContext(dispatcher){
        safeCall {
            val result=apiShopService.getProducts()
            Resource.Success(result)
        }
    }


}