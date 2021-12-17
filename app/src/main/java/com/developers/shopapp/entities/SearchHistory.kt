package com.developers.shopapp.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "search_history")
data class SearchHistory(
 val searchName:String,
 val createAt:Long
) {
    @PrimaryKey(autoGenerate = true )
    var  id:Int?=null

}