package com.sammengistu.stuckfirebase.viewmodels

import androidx.lifecycle.ViewModel
import com.sammengistu.stuckapp.data.PostRepository

class PostViewModel internal constructor(postId: Long, postRepository: PostRepository) : ViewModel() {
//    val post: LiveData<DraftPostModel> = postRepository.getPost(postId)
}