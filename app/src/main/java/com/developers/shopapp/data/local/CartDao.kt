package com.developers.shopapp.data.local

import androidx.room.*
import com.developers.shopapp.entities.ProductCart
import com.developers.shopapp.entities.SearchHistory

@Dao
interface CartDao {

    @Query("select * from Cart order by createAt desc ")
    suspend fun getAllCarts(): List<ProductCart>

    @Query("select * from Cart WHERE foodId = :foodId ")
    suspend fun findItemProductById(foodId:Int): ProductCart

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewCart(productCart: ProductCart): Long

    @Query("DELETE FROM Cart WHERE foodId = :foodId")
    suspend fun deleteCart(foodId: Int): Int

    @Update
    suspend fun updateCart(obj: ProductCart): Int

    @Query("DELETE FROM Cart")
    suspend fun clearAllCarts(): Int

    @Query("SELECT COUNT(foodId) FROM Cart")
    suspend fun getCount(): Int
}