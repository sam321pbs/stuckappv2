package com.sammengistu.stuckfirebase.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.sammengistu.stuckfirebase.models.PostModel
import com.sammengistu.stuckfirebase.repositories.PostRepository

class DraftViewModel internal constructor(postRepository: PostRepository) : ViewModel() {
    private val _data = MutableLiveData<Map<String, Any>>()

    val draftsViewModel : LiveData<List<PostModel>> =
        Transformations.switchMap(_data) { data ->
            if (data == null) {
                AbsentLiveData.create()
            } else {
                postRepository.getAllDraftPosts(data[QUERY_DATA] as String)
            }
        }

    fun setData(data: String) {
        val map = mapOf(Pair(QUERY_DATA, data))
        _data.value = map
    }

    companion object {
        const val QUERY_DATA = "data"
    }
}