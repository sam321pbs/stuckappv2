package com.sammengistu.stuckfirebase.database.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.sammengistu.stuckfirebase.database.DraftPostModel
import com.sammengistu.stuckfirebase.database.PostRepository

class PostListViewModel internal constructor(postRepository: PostRepository, ownerId: String) : ViewModel() {
    val posts: LiveData<List<DraftPostModel>> = postRepository.getAllPosts(ownerId)
}