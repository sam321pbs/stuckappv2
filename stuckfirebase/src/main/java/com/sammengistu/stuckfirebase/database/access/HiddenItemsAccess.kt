package com.sammengistu.stuckfirebase.database.access

import android.content.Context
import com.sammengistu.stuckapp.data.AppDatabase
import com.sammengistu.stuckfirebase.database.HiddenItemModel
import com.sammengistu.stuckfirebase.database.HiddenItemsRepository

class HiddenItemsAccess(private val context: Context) {
    fun insertItem(item: HiddenItemModel) {
        HiddenItemsRepository.getInstance(AppDatabase.getInstance(context).hiddenItemsDao())
            .insertHiddenItem(item)
    }

    fun deleteItem(itemId: Long) {
        HiddenItemsRepository.getInstance(AppDatabase.getInstance(context).hiddenItemsDao())
            .deleteHiddenItem(itemId)
    }
}