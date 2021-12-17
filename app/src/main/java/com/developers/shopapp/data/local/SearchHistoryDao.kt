package com.developers.shopapp.data.local

import androidx.room.*
import com.developers.shopapp.entities.SearchHistory


@Dao
interface SearchHistoryDao {

    @Query("select * from search_history order by createAt desc LIMIT  :countItem")
    suspend fun getAllSearchHistory(countItem: Int): List<SearchHistory>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchHistory(note : SearchHistory):Long

    @Query("DELETE FROM search_history WHERE id = :id")
    suspend fun deleteSearchHistory(id : Int):Int

    @Update
    suspend fun update(obj: SearchHistory):Int

    @Query("DELETE FROM search_history")
    suspend fun clearAllSearchHistory(): Int

}