package com.sammengistu.stuckfirebase.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sammengistu.stuckfirebase.repositories.PostRepository
import com.sammengistu.stuckfirebase.viewmodels.PostListViewModel

class PostListViewModelFactory(
    private val repository: PostRepository,
    private val loadType: String
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        PostListViewModel(
            repository,
            loadType
        ) as T
}