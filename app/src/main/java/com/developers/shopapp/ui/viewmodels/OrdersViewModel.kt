package com.developers.shopapp.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developers.shopapp.entities.MyResponse
import com.developers.shopapp.entities.ProductCart
import com.developers.shopapp.helpers.Event
import com.developers.shopapp.helpers.Resource
import com.developers.shopapp.qualifiers.MainThread
import com.developers.shopapp.repositories.DefaultHomeRepository
import com.developers.shopapp.entities.Order
import com.developers.shopapp.entities.Tracking
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

    private val _addProductToCartStatus =
        MutableStateFlow<Event<Resource<Long>>>(Event(Resource.Init()))
    val addProductToCartStatus: MutableStateFlow<Event<Resource<Long>>> = _addProductToCartStatus

    private val _deleteCartStatus =
        MutableStateFlow<Event<Resource<Int>>>(Event(Resource.Init()))
    val deleteCartStatus: MutableStateFlow<Event<Resource<Int>>> = _deleteCartStatus

    //

    private val _createOrderStatus =
        MutableStateFlow<Event<Resource<MyResponse<Int>>>>(Event(Resource.Init()))
    val createOrderStatus: MutableStateFlow<Event<Resource<MyResponse<Int>>>> = _createOrderStatus

    private val _comingOrderStatus =
        MutableStateFlow<Event<Resource<MyResponse<List<Order>>>>>(Event(Resource.Init()))
    val comingOrderStatus: MutableStateFlow<Event<Resource<MyResponse<List<Order>>>>> = _comingOrderStatus

    private val _preOrderStatus =
        MutableStateFlow<Event<Resource<MyResponse<List<Order>>>>>(Event(Resource.Init()))
    val preOrderStatus: MutableStateFlow<Event<Resource<MyResponse<List<Order>>>>> = _preOrderStatus

  private val _historyOrderStatus =
        MutableStateFlow<Event<Resource<MyResponse<List<Order>>>>>(Event(Resource.Init()))
    val historyOrderStatus: MutableStateFlow<Event<Resource<MyResponse<List<Order>>>>> = _historyOrderStatus

    private val _deleteOrderStatus =
        MutableStateFlow<Event<Resource<MyResponse<Int>>>>(Event(Resource.Init()))
    val deleteOrderStatus: MutableStateFlow<Event<Resource<MyResponse<Int>>>> = _deleteOrderStatus


    private val _updateOrderStatus =
        MutableStateFlow<Event<Resource<MyResponse<Int>>>>(Event(Resource.Init()))
    val updateOrderStatus: MutableStateFlow<Event<Resource<MyResponse<Int>>>> = _updateOrderStatus

    private val _orderTrackingStatus =
        MutableStateFlow<Event<Resource<MyResponse<List<Tracking>>>>> (Event(Resource.Init()))
    val orderTrackingStatus: MutableStateFlow<Event<Resource<MyResponse<List<Tracking>>>>> = _orderTrackingStatus



    fun addProductToCart(productCart: ProductCart) {
        viewModelScope.launch(dispatcher) {
            _addProductToCartStatus.emit(Event(Resource.Loading()))
            val result = repository.addProductToCart(productCart)
            _addProductToCartStatus.emit(Event(result))
        }

    }

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
    fun deleteIteFromCart(itemId:Int) {
        viewModelScope.launch(dispatcher) {
            _deleteCartStatus.emit(Event(Resource.Loading()))
            val result = repository.deleteIteFromCart(itemId)
            _deleteCartStatus.emit(Event(result))
        }
    }
    fun createOrder(order: Order) {
        viewModelScope.launch(dispatcher) {
            _createOrderStatus.emit(Event(Resource.Loading()))
            val result = repository.createOrder(order)
            _createOrderStatus.emit(Event(result))
        }
    }

    fun comingOrders() {
        viewModelScope.launch(dispatcher) {
            _comingOrderStatus.emit(Event(Resource.Loading()))
            val result = repository.comingOrders()
            _comingOrderStatus.emit(Event(result))
        }
    }

    fun preOrders() {
        viewModelScope.launch(dispatcher) {
            _preOrderStatus.emit(Event(Resource.Loading()))
            val result = repository.preOrders()
            _preOrderStatus.emit(Event(result))
        }
    }

    fun historyOrders() {
        viewModelScope.launch(dispatcher) {
            _historyOrderStatus.emit(Event(Resource.Loading()))
            val result = repository.historyOrders()
            _historyOrderStatus.emit(Event(result))
        }
    }

    fun deleteOrder(orderId:Int) {
        viewModelScope.launch(dispatcher) {
            _deleteOrderStatus.emit(Event(Resource.Loading()))
            val result = repository.deleteOrder(orderId)
            _deleteOrderStatus.emit(Event(result))
        }
    }

    fun updateOrder(orderId:Int,orderType:Int) {
        viewModelScope.launch(dispatcher) {
            _deleteOrderStatus.emit(Event(Resource.Loading()))
            val result = repository.updateOrder(orderId,orderType)
            _deleteOrderStatus.emit(Event(result))
        }
    }

    fun deleteOrderCart(foodId: Int) {
        viewModelScope.launch(dispatcher) {
            _deleteCartStatus.emit(Event(Resource.Loading()))
            val result = repository.deleteIteFromCart(foodId)
            _deleteCartStatus.emit(Event(result))
        }
    }
    fun getOrderTracking(orderId: Int) {
        viewModelScope.launch(dispatcher) {
            _orderTrackingStatus.emit(Event(Resource.Loading()))
            val result = repository.getOrderTracking(orderId)
            _orderTrackingStatus.emit(Event(result))
        }
    }


}