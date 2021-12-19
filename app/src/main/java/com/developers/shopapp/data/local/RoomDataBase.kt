package com.developers.shopapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.developers.shopapp.entities.ProductCart
import com.developers.shopapp.entities.SearchHistory

@Database(entities = [SearchHistory::class,ProductCart::class], version = 1, exportSchema = false)
abstract class RoomDataBase:RoomDatabase() {

    abstract fun searchHistoryDao(): SearchHistoryDao

    abstract fun cartDao(): CartDao



}