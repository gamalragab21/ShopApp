package com.developers.shopapp.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developers.shopapp.entities.MyResponse
import com.developers.shopapp.entities.Product
import com.developers.shopapp.entities.Restaurant
import com.developers.shopapp.entities.SearchHistory
import com.developers.shopapp.helpers.Event
import com.developers.shopapp.helpers.Resource
import com.developers.shopapp.qualifiers.MainThread
import com.developers.shopapp.repositories.DefaultHomeRepository
import com.developers.shopapp.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val repository: DefaultHomeRepository,
    @MainThread
    private val dispatcher: CoroutineDispatcher

) : ViewModel() {


    private val _searchHistoryStatus = MutableStateFlow<Event<Resource<List<SearchHistory>>>>(Event(Resource.Init()))
    val searchHistoryStatus: MutableStateFlow<Event<Resource<List<SearchHistory>>>> = _searchHistoryStatus

    private val _insertSearchKeyWordStatus = MutableStateFlow<Event<Resource<Long>>>(Event(Resource.Init()))
    val insertSearchKeyWordStatus: MutableStateFlow<Event<Resource<Long>>> = _insertSearchKeyWordStatus

    private val _deleteSearchKeyWordStatus = MutableStateFlow<Event<Resource<Int>>>(Event(Resource.Init()))
    val deleteSearchKeyWordStatus: MutableStateFlow<Event<Resource<Int>>> = _deleteSearchKeyWordStatus

    private val _clearAllSearchHistoryStatus = MutableStateFlow<Event<Resource<Int>>>(Event(Resource.Init()))
    val clearAllSearchHistoryStatus: MutableStateFlow<Event<Resource<Int>>> = _clearAllSearchHistoryStatus


    fun getAllSearchHistory(countItem: Int) {
        viewModelScope.launch(dispatcher) {
            _searchHistoryStatus.emit(Event(Resource.Loading()))
            val result = repository.getAllSearchHistory(countItem)
            _searchHistoryStatus.emit(Event(result))
        }
    }

    fun insertNewSearchKeyWord(searchText: String) {
        viewModelScope.launch(dispatcher) {
            val createAt= Utils.getTimeStamp()
            val searchHistory=SearchHistory(searchText,createAt)
            _insertSearchKeyWordStatus.emit(Event(Resource.Loading()))
            val result = repository.insertNewSearchKeyWord(searchHistory)
            _insertSearchKeyWordStatus.emit(Event(result))
        }
    }

    fun deleteSearchHistory(searchHistory: SearchHistory) {
        viewModelScope.launch(dispatcher) {
            _deleteSearchKeyWordStatus.emit(Event(Resource.Loading()))
            val result = repository.deleteSearchHistory(searchHistory.id!!)
            _deleteSearchKeyWordStatus.emit(Event(result))
        }
    }

    fun clearAllSearchHistory() {
        viewModelScope.launch(dispatcher) {
            _clearAllSearchHistoryStatus.emit(Event(Resource.Loading()))
            val result = repository.clearAllSearchHistory()
            _clearAllSearchHistoryStatus.emit(Event(result))
        }
    }


}