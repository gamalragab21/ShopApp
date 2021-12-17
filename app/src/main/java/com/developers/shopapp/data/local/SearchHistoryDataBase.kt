package com.developers.shopapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.developers.shopapp.entities.SearchHistory

@Database(entities = [SearchHistory::class], version = 1, exportSchema = false)
abstract class SearchHistoryDataBase:RoomDatabase() {

    abstract fun searchHistoryDao(): SearchHistoryDao



}