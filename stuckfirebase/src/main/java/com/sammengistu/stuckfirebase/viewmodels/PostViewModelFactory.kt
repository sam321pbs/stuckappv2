package com.sammengistu.stuckfirebase.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sammengistu.stuckapp.data.PostRepository

class PostViewModelFactory(private val postId: Long, private val repository: PostRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = PostViewModel(postId, repository) as T
}