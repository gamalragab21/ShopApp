package com.developers.shopapp.ui.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developers.shopapp.entities.Category
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
class CategoryProductViewModel  @Inject constructor(
    @ApplicationContext val context: Context,
    private val repository: DefaultHomeRepository,
    @MainThread
    private val dispatcher: CoroutineDispatcher

) : ViewModel(){

    private val _categoryStatus =
        MutableStateFlow<Event<Resource<MyResponse<List<Category>>>>>(Event(Resource.Init()))
    val categoryStatus: MutableStateFlow<Event<Resource<MyResponse<List<Category>>>>> = _categoryStatus


    fun getCategoriesOfRestaurant(restaurantId: Int){
        viewModelScope.launch(dispatcher) {
            _categoryStatus.emit(Event(Resource.Loading()))
            val result = repository.getCategoriesOfRestaurant(restaurantId)
            _categoryStatus.emit(Event(result))
        }
    }

}