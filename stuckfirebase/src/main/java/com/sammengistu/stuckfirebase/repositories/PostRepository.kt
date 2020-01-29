package com.sammengistu.stuckfirebase.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sammengistu.stuckfirebase.access.FirebaseItemAccess.OnItemsRetrieved
import com.sammengistu.stuckfirebase.access.StarPostAccess
import com.sammengistu.stuckfirebase.database.PostDao
import com.sammengistu.stuckfirebase.database.model.DraftPostModel
import com.sammengistu.stuckfirebase.models.StarPostModel
import java.util.*

class PostRepository(val dao: PostDao) {

    fun getAllDraftPosts(ownerId: String) = dao.getAllPosts(ownerId)

    fun getDraftPost(id: Long) = dao.getPost(id)

    fun insertDraftPost(post: DraftPostModel) = dao.insertPost(post)

    fun deleteDraftPost(post: DraftPostModel) = dao.deletePost(post)

    fun deleteDraftPost(postId: Long) = dao.deleteByPostId(postId)

    fun getStarPosts(userRef: String): LiveData<List<StarPostModel>> {
        val liveData = MutableLiveData<List<StarPostModel>>()
        StarPostAccess().getUsersStarredPosts(
            userRef,
            object : OnItemsRetrieved<StarPostModel> {
                override fun onSuccess(list: List<StarPostModel>) {
                    liveData.value = list
                }

                override fun onFailed(e: Exception) {
                    liveData.value = Collections.emptyList()
                }
            })
        return liveData
    }

    companion object {
        @Volatile
        private var instance: PostRepository? = null

        fun getInstance(postDao: PostDao) =
            instance
                ?: synchronized(this) {
                instance
                    ?: PostRepository(
                        postDao
                    ).also { instance = it }
            }
    }
}