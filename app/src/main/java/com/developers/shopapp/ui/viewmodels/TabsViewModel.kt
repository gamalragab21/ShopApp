package com.developers.shopapp.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developers.shopapp.entities.MyResponse
import com.developers.shopapp.entities.Restaurant
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
class TabsViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val repository: DefaultHomeRepository,
    @MainThread
    private val dispatcher: CoroutineDispatcher

) : ViewModel(){

    private val _restaurantStatus =
        MutableStateFlow<Event<Resource<MyResponse<List<Restaurant>>>>>(Event(Resource.Init()))
    val restaurantStatus: MutableStateFlow<Event<Resource<MyResponse<List<Restaurant>>>>> = _restaurantStatus

    private val _deleteFavRestaurantStatus =
        MutableStateFlow<Event<Resource<MyResponse<String>>>>(Event(Resource.Init()))
    val deleteFavRestaurantStatus: MutableStateFlow<Event<Resource<MyResponse<String>>>> = _deleteFavRestaurantStatus

    private val _setFavRestaurantStatus =
        MutableStateFlow<Event<Resource<MyResponse<String>>>>(Event(Resource.Init()))
    val setFavRestaurantStatus: MutableStateFlow<Event<Resource<MyResponse<String>>>> = _setFavRestaurantStatus

    fun getAllRestaurant(){
        viewModelScope.launch(dispatcher) {
            _restaurantStatus.emit(Event(Resource.Loading()))
            val result = repository.getAllRestaurants()
            _restaurantStatus.emit(Event(result))
        }
    }

    fun deleteFavRestaurant(restaurant: Restaurant) {
        viewModelScope.launch(dispatcher) {
            _deleteFavRestaurantStatus.emit(Event(Resource.Loading()))
            val result = repository.deleteFavRestaurant(restaurant.restaurantId)
            _deleteFavRestaurantStatus.emit(Event(result))
        }
    }

    fun setFavRestaurant(restaurant: Restaurant) {
        viewModelScope.launch(dispatcher) {
            _setFavRestaurantStatus.emit(Event(Resource.Loading()))
            val result = repository.setFavRestaurant(restaurant.restaurantId)
            _setFavRestaurantStatus.emit(Event(result))
        }
    }

}