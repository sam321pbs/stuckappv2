package com.sammengistu.stuckfirebase.repositories

import com.sammengistu.stuckfirebase.database.dao.HiddenItemsDao
import com.sammengistu.stuckfirebase.models.HiddenItemModel

class HiddenItemsRepository private constructor(val dao: HiddenItemsDao) {

    fun getAllHiddenItems(ownerRef: String) = dao.getAllItems(ownerRef)

    fun getHiddenItem(id: Long) = dao.getItem(id)

    fun insertHiddenItem(item: HiddenItemModel) = dao.insertItem(item)

    fun deleteHiddenItem(item: HiddenItemModel) = dao.deleteItem(item)

    fun deleteHiddenItem(itemtId: Long) = dao.deleteByItemId(itemtId)

    companion object {
        @Volatile
        private var instance: HiddenItemsRepository? = null

        fun getInstance(hiddenItemsDao: HiddenItemsDao) =
            instance
                ?: synchronized(this) {
                instance
                    ?: HiddenItemsRepository(
                        hiddenItemsDao
                    ).also { instance = it }
            }
    }
}