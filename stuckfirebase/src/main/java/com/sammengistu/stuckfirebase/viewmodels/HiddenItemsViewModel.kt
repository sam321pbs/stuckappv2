package com.sammengistu.stuckfirebase.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.sammengistu.stuckfirebase.models.HiddenItemModel
import com.sammengistu.stuckfirebase.repositories.HiddenItemsRepository

class HiddenItemsViewModel internal constructor(
    hiddenItemsRepository: HiddenItemsRepository,
    ownerRef: String
) : ViewModel() {
    val items: LiveData<List<HiddenItemModel>> = hiddenItemsRepository.getAllHiddenItems(ownerRef)
}