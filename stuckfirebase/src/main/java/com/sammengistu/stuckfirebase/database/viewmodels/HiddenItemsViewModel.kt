package com.sammengistu.stuckfirebase.database.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.sammengistu.stuckfirebase.database.HiddenItemModel
import com.sammengistu.stuckfirebase.database.HiddenItemsRepository

class HiddenItemsViewModel internal constructor(
    hiddenItemsRepository: HiddenItemsRepository,
    ownerId: String
) : ViewModel() {
    val items: LiveData<List<HiddenItemModel>> = hiddenItemsRepository.getAllHiddenItems(ownerId)
}