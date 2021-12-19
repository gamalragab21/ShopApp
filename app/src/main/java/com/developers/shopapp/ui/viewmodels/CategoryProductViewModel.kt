package com.developers.shopapp.ui.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developers.shopapp.entities.*
import com.developers.shopapp.helpers.Event
import com.developers.shopapp.helpers.Resource
import com.developers.shopapp.qualifiers.MainThread
import com.developers.shopapp.repositories.DefaultHomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryProductViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val repository: DefaultHomeRepository,
    @MainThread
    private val dispatcher: CoroutineDispatcher

) : ViewModel() {

    private val _categoryStatus =
        MutableStateFlow<Event<Resource<MyResponse<List<Category>>>>>(Event(Resource.Init()))
    val categoryStatus: MutableStateFlow<Event<Resource<MyResponse<List<Category>>>>> =
        _categoryStatus

    private val _productStatus =
        MutableStateFlow<Event<Resource<MyResponse<List<Product>>>>>(Event(Resource.Init()))
    val productStatus: MutableStateFlow<Event<Resource<MyResponse<List<Product>>>>> = _productStatus

    private val _deleteFavProductStatus =
        MutableStateFlow<Event<Resource<MyResponse<String>>>>(Event(Resource.Init()))
    val deleteFavProductStatus: MutableStateFlow<Event<Resource<MyResponse<String>>>> = _deleteFavProductStatus

    private val _setFavProductStatus =
        MutableStateFlow<Event<Resource<MyResponse<String>>>>(Event(Resource.Init()))
    val setFavProductStatus: MutableStateFlow<Event<Resource<MyResponse<String>>>> = _setFavProductStatus

    private val _favouritesProductStatus =
        MutableStateFlow<Event<Resource<MyResponse<List<Product>>>>>(Event(Resource.Init()))
    val favouritesProductStatus: MutableStateFlow<Event<Resource<MyResponse<List<Product>>>>> = _favouritesProductStatus

    private val _popularProductStatus =
        MutableStateFlow<Event<Resource<MyResponse<List<Product>>>>>(Event(Resource.Init()))
    val popularProductStatus: MutableStateFlow<Event<Resource<MyResponse<List<Product>>>>> = _popularProductStatus

    private val _findProductStatus =
        MutableStateFlow<Event<Resource<MyResponse<Product>>>>(Event(Resource.Init()))
    val findProductStatus: MutableStateFlow<Event<Resource<MyResponse<Product>>>> = _findProductStatus

    private val _myRatingProductStatus =
        MutableStateFlow<Event<Resource<MyResponse<RateProduct>>>>(Event(Resource.Init()))
    val myRatingProductStatus: MutableStateFlow<Event<Resource<MyResponse<RateProduct>>>> = _myRatingProductStatus

    private val _addProductToCartStatus =
        MutableStateFlow<Event<Resource<Long>>>(Event(Resource.Init()))
    val addProductToCartStatus: MutableStateFlow<Event<Resource<Long>>> = _addProductToCartStatus


    fun getCategoriesOfRestaurant(restaurantId: Int) {
        viewModelScope.launch(dispatcher) {
            _categoryStatus.emit(Event(Resource.Loading()))
            val result = repository.getCategoriesOfRestaurant(restaurantId)
            _categoryStatus.emit(Event(result))
        }
    }

    fun getProductOfCategory(restaurantId:Int,categoryId: Int) {
        viewModelScope.launch(dispatcher) {
            _productStatus.emit(Event(Resource.Loading()))

            val result = if (categoryId > 0) {
                repository.getProductOfCategory(restaurantId,categoryId)
            } else { // load for you
                repository.getProductForYou(restaurantId)
            }
            _productStatus.emit(Event(result))
        }
    }

    fun deleteFavProduct(product: Product) {
        viewModelScope.launch(dispatcher) {
            _deleteFavProductStatus.emit(Event(Resource.Loading()))
            val result = repository.deleteFavProduct(product.productId)
            _deleteFavProductStatus.emit(Event(result))
        }
    }

    fun setFavProduct(product: Product) {
        viewModelScope.launch(dispatcher) {
            _setFavProductStatus.emit(Event(Resource.Loading()))
            val result = repository.setFavProduct(product.productId)
            _setFavProductStatus.emit(Event(result))
        }
    }

    fun getAllFavProducts() {
        viewModelScope.launch(dispatcher) {
            _favouritesProductStatus.emit(Event(Resource.Loading()))
            val result = repository.getAllFavouritesProduct()
            _favouritesProductStatus.emit(Event(result))
        }
    }
    fun getPopularRestaurant(){
        viewModelScope.launch(dispatcher) {
            _popularProductStatus.emit(Event(Resource.Loading()))
            val result = repository.getPopularProduct()
            _popularProductStatus.emit(Event(result))
        }
    }

    fun findProductById(productId: Int) {
        viewModelScope.launch(dispatcher) {
            _findProductStatus.emit(Event(Resource.Loading()))
            val result = repository.findProductById(productId)
            _findProductStatus.emit(Event(result))
        }
    }



    fun setupRatingMyProduct(rateProduct: RateProduct) {
        viewModelScope.launch(dispatcher) {
            _myRatingProductStatus.emit(Event(Resource.Loading()))
            val result = repository.setupRatingMyProduct(rateProduct)
            _myRatingProductStatus.emit(Event(result))
        }
    }
    fun updateRateProduct(rateProduct: RateProduct) {
        viewModelScope.launch(dispatcher) {
            _myRatingProductStatus.emit(Event(Resource.Loading()))
            val result = repository.updateRateProduct(rateProduct)
            _myRatingProductStatus.emit(Event(result))
        }
    }

    fun addProductToCart(productCart: ProductCart) {
        viewModelScope.launch(dispatcher) {
            _addProductToCartStatus.emit(Event(Resource.Loading()))
            val result = repository.addProductToCart(productCart)
            _addProductToCartStatus.emit(Event(result))
        }

    }

}