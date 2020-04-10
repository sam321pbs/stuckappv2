package com.sammengistu.stuckfirebase.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sammengistu.stuckfirebase.repositories.PostRepository
import com.sammengistu.stuckfirebase.viewmodels.DraftViewModel

class DraftPostListViewModelFactory(
    private val repository: PostRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        DraftViewModel(
            repository
        ) as T
}