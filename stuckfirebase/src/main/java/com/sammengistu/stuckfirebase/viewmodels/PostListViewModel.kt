package com.sammengistu.stuckfirebase.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.sammengistu.stuckfirebase.models.DraftPostModel
import com.sammengistu.stuckfirebase.repositories.PostRepository

class PostListViewModel internal constructor(postRepository: PostRepository, ownerId: String) : ViewModel() {
    val posts: LiveData<List<DraftPostModel>> = postRepository.getAllDraftPosts(ownerId)
//    val starPosts: LiveData<List<StarPostModel>> = postRepository.getStarPosts(ownerId)
}