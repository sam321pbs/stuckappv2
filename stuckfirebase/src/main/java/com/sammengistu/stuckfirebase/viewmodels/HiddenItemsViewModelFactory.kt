package com.sammengistu.stuckfirebase.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sammengistu.stuckfirebase.repositories.HiddenItemsRepository

class HiddenItemsViewModelFactory(
    private val repository: HiddenItemsRepository,
    val ownerId: String
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        HiddenItemsViewModel(repository, ownerId) as T
}