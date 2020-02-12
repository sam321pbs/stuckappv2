package com.sammengistu.stuckfirebase.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.sammengistu.stuckfirebase.models.HiddenItemModel

@Dao
interface HiddenItemsDao {
    @Query("SELECT * FROM hidden_items WHERE ownerRef = :ownerRef ORDER BY _id")
    fun getAllItems(ownerRef: String): LiveData<List<HiddenItemModel>>

    @Query("SELECT * FROM hidden_items WHERE _id = :id")
    fun getItem(id: Long): LiveData<List<HiddenItemModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItem(item: HiddenItemModel)

    @Delete
    fun deleteItem(item: HiddenItemModel)

    @Query("DELETE FROM hidden_items WHERE _id = :itemId")
    fun deleteByItemId(itemId: Long)
}