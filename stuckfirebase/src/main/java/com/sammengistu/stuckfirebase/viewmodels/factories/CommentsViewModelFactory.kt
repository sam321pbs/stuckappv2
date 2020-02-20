package com.sammengistu.stuckfirebase.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sammengistu.stuckfirebase.repositories.CommentsRepository
import com.sammengistu.stuckfirebase.viewmodels.CommentsViewModel

class CommentsViewModelFactory(
    private val repository: CommentsRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        CommentsViewModel(repository) as T
}