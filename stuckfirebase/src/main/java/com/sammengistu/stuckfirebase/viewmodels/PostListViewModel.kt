package com.sammengistu.stuckfirebase.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.sammengistu.stuckfirebase.constants.LOAD_TYPE_CATEGORIES
import com.sammengistu.stuckfirebase.constants.LOAD_TYPE_FAVORITE
import com.sammengistu.stuckfirebase.constants.LOAD_TYPE_USER
import com.sammengistu.stuckfirebase.models.PostModel
import com.sammengistu.stuckfirebase.repositories.PostRepository
import com.sammengistu.stuckfirebase.utils.DateUtils

class PostListViewModel internal constructor(postRepository: PostRepository, loadType: String) : ViewModel() {
    private val _data = MutableLiveData<Map<String, Any>>()

    val postsViewModel : LiveData<List<PostModel>?> =
        Transformations.switchMap(_data) { data ->
            if (data == null) {
                AbsentLiveData.create()
            } else {
                when (loadType) {
                    LOAD_TYPE_FAVORITE -> postRepository.getStarPosts(data[DATA] as String, data[TIMESTAMP]!!)
                    LOAD_TYPE_CATEGORIES -> postRepository.getPostCategory(data[DATA] as String, data[TIMESTAMP]!!)
                    LOAD_TYPE_USER -> postRepository.getUserPosts(data[DATA] as String, data[TIMESTAMP]!!)
//                    LOAD_TYPE_DRAFT -> postRepository.getAllDraftPosts(data)
                    else -> postRepository.getRecentPosts(data[TIMESTAMP]!!)
                }
            }
        }

    fun setData(data: String, timestamp: Any = DateUtils.getMaxDate()) {
        val map = mapOf(Pair(DATA, data), Pair(TIMESTAMP, timestamp))
        _data.value = map
    }

    fun setData(timestamp: Any = DateUtils.getMaxDate()) {
        setData("", timestamp)
    }

    companion object {
        const val DATA = "data"
        const val TIMESTAMP = "timestamp"
    }
}