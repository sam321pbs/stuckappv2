package com.sammengistu.stuckapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.sammengistu.stuckapp.data.Post
import com.sammengistu.stuckapp.data.PostRepository

class PostListViewModel internal constructor(postRepository: PostRepository) : ViewModel() {
    val posts: LiveData<List<Post>> = postRepository.getAllPosts()
}