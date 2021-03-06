package com.sammengistu.stuckfirebase.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface HiddenItemsDao {
    @Query("SELECT * FROM hidden_items WHERE ownerId = :ownerId ORDER BY _id")
    fun getAllItems(ownerId: String): LiveData<List<HiddenItemModel>>

    @Query("SELECT * FROM hidden_items WHERE _id = :id")
    fun getItem(id: Long): LiveData<List<HiddenItemModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItem(item: HiddenItemModel)

    @Delete
    fun deleteItem(item: HiddenItemModel)

    @Query("DELETE FROM hidden_items WHERE _id = :itemId")
    fun deleteByItemId(itemId: Long)
}