package com.sammengistu.stuckfirebase.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.sammengistu.stuckapp.data.DraftPostModel
import com.sammengistu.stuckapp.data.PostRepository

class PostListViewModel internal constructor(postRepository: PostRepository) : ViewModel() {
    val posts: LiveData<List<DraftPostModel>> = postRepository.getAllPosts()
}