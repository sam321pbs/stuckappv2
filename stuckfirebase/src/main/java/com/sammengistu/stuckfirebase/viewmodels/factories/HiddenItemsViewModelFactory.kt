package com.sammengistu.stuckfirebase.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sammengistu.stuckfirebase.repositories.HiddenItemsRepository
import com.sammengistu.stuckfirebase.viewmodels.HiddenItemsViewModel

class HiddenItemsViewModelFactory(
    private val repository: HiddenItemsRepository,
    val ownerRef: String
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        HiddenItemsViewModel(
            repository,
            ownerRef
        ) as T
}