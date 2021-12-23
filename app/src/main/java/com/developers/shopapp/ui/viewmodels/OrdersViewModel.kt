package com.developers.shopapp.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developers.shopapp.entities.ProductCart
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
class OrdersViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val repository: DefaultHomeRepository,
    @MainThread
    private val dispatcher: CoroutineDispatcher

) : ViewModel() {

    private val _cartsStatus = MutableStateFlow<Event<Resource<List<ProductCart>>>>(Event(Resource.Init()))
    val cartsStatus: MutableStateFlow<Event<Resource<List<ProductCart>>>> = _cartsStatus

    private val _findCartStatus = MutableStateFlow<Event<Resource<ProductCart>>>(Event(Resource.Init()))
    val findCartStatus: MutableStateFlow<Event<Resource<ProductCart>>> = _findCartStatus

    private val _updateCartStatus = MutableStateFlow<Event<Resource<Int>>>(Event(Resource.Init()))
    val updateCartStatus: MutableStateFlow<Event<Resource<Int>>> = _updateCartStatus

    fun getAllCarts(startLoading: Boolean) {
        viewModelScope.launch(dispatcher) {

           if (startLoading) _cartsStatus.emit(Event(Resource.Loading()))
            val result = repository.getAllCarts()
            _cartsStatus.emit(Event(result))
        }
    }

    fun findItemCart(productId: Int) {
        viewModelScope.launch(dispatcher) {
            _findCartStatus.emit(Event(Resource.Loading()))
            val result = repository.findItemCart(productId)
            _findCartStatus.emit(Event(result))
        }
    }

    fun updateItemQuality(itemProduct: ProductCart) {
        viewModelScope.launch(dispatcher) {
            _updateCartStatus.emit(Event(Resource.Loading()))
            val result = repository.updateItemQuality(itemProduct)
            _updateCartStatus.emit(Event(result))
        }
    }

}