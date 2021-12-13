package com.developers.shopapp.ui.viewmodels

import android.content.Context
import android.text.Editable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developers.shopapp.entities.MyResponse
import com.developers.shopapp.entities.RateRestaurant
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
class RestaurantViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val repository: DefaultHomeRepository,
    @MainThread
    private val dispatcher: CoroutineDispatcher

) : ViewModel(){

    val callPhone=MutableLiveData<String>()

    private val _restaurantStatus =
        MutableStateFlow<Event<Resource<MyResponse<List<Restaurant>>>>>(Event(Resource.Init()))
    val restaurantStatus: MutableStateFlow<Event<Resource<MyResponse<List<Restaurant>>>>> = _restaurantStatus

    private val _findRestaurantStatus =
        MutableStateFlow<Event<Resource<MyResponse<Restaurant>>>>(Event(Resource.Init()))
    val findRestaurantStatus: MutableStateFlow<Event<Resource<MyResponse<Restaurant>>>> = _findRestaurantStatus


    private val _deleteFavRestaurantStatus =
        MutableStateFlow<Event<Resource<MyResponse<String>>>>(Event(Resource.Init()))
    val deleteFavRestaurantStatus: MutableStateFlow<Event<Resource<MyResponse<String>>>> = _deleteFavRestaurantStatus

    private val _setFavRestaurantStatus =
        MutableStateFlow<Event<Resource<MyResponse<String>>>>(Event(Resource.Init()))
    val setFavRestaurantStatus: MutableStateFlow<Event<Resource<MyResponse<String>>>> = _setFavRestaurantStatus

    private val _favouritesRestaurantStatus =
        MutableStateFlow<Event<Resource<MyResponse<List<Restaurant>>>>>(Event(Resource.Init()))
    val favouritesRestaurantStatus: MutableStateFlow<Event<Resource<MyResponse<List<Restaurant>>>>> = _favouritesRestaurantStatus

    private val _ratingRestaurantStatus =
        MutableStateFlow<Event<Resource<MyResponse<List<Restaurant>>>>>(Event(Resource.Init()))
    val ratingRestaurantStatus: MutableStateFlow<Event<Resource<MyResponse<List<Restaurant>>>>> = _ratingRestaurantStatus

    private val _popularRestaurantStatus =
        MutableStateFlow<Event<Resource<MyResponse<List<Restaurant>>>>>(Event(Resource.Init()))
    val popularRestaurantStatus: MutableStateFlow<Event<Resource<MyResponse<List<Restaurant>>>>> = _popularRestaurantStatus

    private val _nearlyRestaurantStatus =
        MutableStateFlow<Event<Resource<MyResponse<List<Restaurant>>>>>(Event(Resource.Init()))
    val nearlyRestaurantStatus: MutableStateFlow<Event<Resource<MyResponse<List<Restaurant>>>>> = _nearlyRestaurantStatus

    private val _filterRestaurantStatus =
        MutableStateFlow<Event<Resource<MyResponse<List<Restaurant>>>>>(Event(Resource.Init()))
    val filterRestaurantStatus: MutableStateFlow<Event<Resource<MyResponse<List<Restaurant>>>>> = _filterRestaurantStatus

    private val _myRatingRestaurantStatus =
        MutableStateFlow<Event<Resource<MyResponse<RateRestaurant>>>>(Event(Resource.Init()))
    val myRatingRestaurantStatus: MutableStateFlow<Event<Resource<MyResponse<RateRestaurant>>>> = _myRatingRestaurantStatus

    fun getAllRestaurant(){
        viewModelScope.launch(dispatcher) {
            _restaurantStatus.emit(Event(Resource.Loading()))
            val result = repository.getAllRestaurants()
            _restaurantStatus.emit(Event(result))
        }
    }
    fun findMyRestaurant(restaurantId: Int){
        viewModelScope.launch(dispatcher) {
            _findRestaurantStatus.emit(Event(Resource.Loading()))
            val result = repository.findMyRestaurant(restaurantId)
            _findRestaurantStatus.emit(Event(result))
        }
    }

    fun getAllFavouritesRestaurant(){
        viewModelScope.launch(dispatcher) {
            _favouritesRestaurantStatus.emit(Event(Resource.Loading()))
            val result = repository.getAllFavouritesRestaurant()
            _favouritesRestaurantStatus.emit(Event(result))
        }
    }

    fun setupRatingMyRestaurant(rateRestaurant: RateRestaurant) {
        viewModelScope.launch(dispatcher) {
            _myRatingRestaurantStatus.emit(Event(Resource.Loading()))
            val result = repository.setupRatingMyRestaurant(rateRestaurant)
            _myRatingRestaurantStatus.emit(Event(result))
        }
    }
    fun updateRateRestaurant(rateRestaurant: RateRestaurant) {
        viewModelScope.launch(dispatcher) {
            _myRatingRestaurantStatus.emit(Event(Resource.Loading()))
            val result = repository.updateRateRestaurant(rateRestaurant)
            _myRatingRestaurantStatus.emit(Event(result))
        }
    }

    fun getPopularRestaurant(){
        viewModelScope.launch(dispatcher) {
            _popularRestaurantStatus.emit(Event(Resource.Loading()))
            val result = repository.getPopularRestaurant()
            _popularRestaurantStatus.emit(Event(result))
        }
    }
    fun filterRestaurant(it: Editable) {
        viewModelScope.launch(dispatcher) {
            _filterRestaurantStatus.emit(Event(Resource.Loading()))
            val result = repository.filterRestaurant(it.toString())
            _filterRestaurantStatus.emit(Event(result))
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

    fun getAllRatingRestaurant() {
        viewModelScope.launch(dispatcher) {
            _ratingRestaurantStatus.emit(Event(Resource.Loading()))
            val result = repository.getAllRatingRestaurants()
            _ratingRestaurantStatus.emit(Event(result))
        }
    }

    fun getNearlyRestaurant(latitude: Double, longitude: Double) {
        viewModelScope.launch(dispatcher) {
            _nearlyRestaurantStatus.emit(Event(Resource.Loading()))
            val result = repository.getNearlyRestaurant(latitude,longitude)
            _nearlyRestaurantStatus.emit(Event(result))
        }
    }




}