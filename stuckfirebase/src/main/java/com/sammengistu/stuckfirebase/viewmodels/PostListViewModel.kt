package com.sammengistu.stuckfirebase.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.sammengistu.stuckapp.data.DraftPost
import com.sammengistu.stuckapp.data.PostRepository

class PostListViewModel internal constructor(postRepository: PostRepository) : ViewModel() {
    val posts: LiveData<List<DraftPost>> = postRepository.getAllPosts()
}