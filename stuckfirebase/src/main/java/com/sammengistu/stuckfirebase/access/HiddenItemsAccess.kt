package com.sammengistu.stuckfirebase.access

import android.content.Context
import com.sammengistu.stuckfirebase.database.AppDatabase
import com.sammengistu.stuckfirebase.models.HiddenItemModel
import com.sammengistu.stuckfirebase.repositories.HiddenItemsRepository

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