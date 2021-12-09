package com.developers.shopapp.ui.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developers.shopapp.entities.MyResponse
import com.developers.shopapp.entities.Restaurant
import com.developers.shopapp.entities.User
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
class UserViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val repository: DefaultHomeRepository,
    @MainThread
    private val dispatcher: CoroutineDispatcher

) : ViewModel() {


    private val _userProfileStatus =
        MutableStateFlow<Event<Resource<MyResponse<User>>>>(Event(Resource.Init()))
    val userProfileStatus: MutableStateFlow<Event<Resource<MyResponse<User>>>> =
        _userProfileStatus

    fun getAllFavouritesRestaurant(){
        viewModelScope.launch(dispatcher) {
            _userProfileStatus.emit(Event(Resource.Loading()))
            val result = repository.getProfile()
            _userProfileStatus.emit(Event(result))
        }
    }



}