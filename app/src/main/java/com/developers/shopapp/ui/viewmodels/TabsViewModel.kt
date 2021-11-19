package com.developers.shopapp.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developers.shopapp.entities.AuthModel
import com.developers.shopapp.entities.Product
import com.developers.shopapp.helpers.Event
import com.developers.shopapp.helpers.Resource
import com.developers.shopapp.qualifiers.MainThread
import com.developers.shopapp.repositories.AuthenticationRepository
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

    private val _productStatus =
        MutableStateFlow<Event<Resource<Product>>>(Event(Resource.Init()))
    val productStatus: MutableStateFlow<Event<Resource<Product>>> = _productStatus


    fun getAllProduct(){
        viewModelScope.launch(dispatcher) {
            _productStatus.emit(Event(Resource.Loading()))
            val result = repository.getAllProduct()
            _productStatus.emit(Event(result))
        }
    }

}