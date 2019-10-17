package com.sammengistu.stuckfirebase.database.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sammengistu.stuckfirebase.database.PostRepository

class PostListViewModelFactory(private val repository: PostRepository, private val ownerId: String) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = PostListViewModel(repository, ownerId) as T
}