package com.sammengistu.stuckfirebase.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sammengistu.stuckfirebase.access.FirebaseItemAccess.OnItemsRetrieved
import com.sammengistu.stuckfirebase.access.PostAccess
import com.sammengistu.stuckfirebase.access.StarPostAccess
import com.sammengistu.stuckfirebase.database.dao.PostDao
import com.sammengistu.stuckfirebase.models.DraftPostModel
import com.sammengistu.stuckfirebase.models.PostModel
import com.sammengistu.stuckfirebase.models.StarPostModel

private const val TAG = "PostRepository"

class PostRepository private constructor(val dao: PostDao) {

    fun getAllDraftPosts(ownerId: String) = dao.getDraftPosts(ownerId)

    fun getDraftPost(draftId: Int) = dao.getDraftPost(draftId)

    fun insertDraftPost(post: DraftPostModel) = dao.insertPost(post)

    fun deleteDraftPost(post: DraftPostModel) = dao.deletePost(post)

    fun deleteDraftPost(draftId: Int) = dao.deleteByPostId(draftId)

    fun getStarPosts(userRef: String, timestamp: Any): LiveData<List<PostModel>?> {
        val liveData = MutableLiveData<List<PostModel>?>()
        StarPostAccess().getUsersStarredPostsBefore(
            userRef,
            timestamp,
            object : OnItemsRetrieved<StarPostModel> {
                override fun onSuccess(list: List<StarPostModel>) {
                    liveData.value = list
                }

                override fun onFailed(e: Exception) {
                    Log.e(TAG, "Failed to get StarPosts", e)
                    liveData.value = null
                }
            })
        return liveData
    }

    fun getPostCategory(category: String, timestamp: Any): LiveData<List<PostModel>?> {
        val liveData = MutableLiveData<List<PostModel>?>()
        PostAccess().getPostsInCategory(
            category,
            timestamp,
            object : OnItemsRetrieved<PostModel>{
                override fun onSuccess(list: List<PostModel>) {
                    liveData.value = list
                }

                override fun onFailed(e: Exception) {
                    Log.e(TAG, "Failed to get Posts in category '$category'", e)
                    liveData.value = null
                }

            })
        return liveData
    }

    fun getUserPosts(userRef: String, timestamp: Any): LiveData<List<PostModel>?> {
        val liveData = MutableLiveData<List<PostModel>?>()
        PostAccess().getOwnerPosts(
            userRef,
            timestamp,
            object : OnItemsRetrieved<PostModel>{
                override fun onSuccess(list: List<PostModel>) {
                    liveData.value = list
                }

                override fun onFailed(e: Exception) {
                    Log.e(TAG, "Failed to get users posts", e)
                    liveData.value = null
                }

            })
        return liveData
    }

    fun getRecentPosts(timestamp: Any): LiveData<List<PostModel>?> {
        val liveData = MutableLiveData<List<PostModel>?>()
        PostAccess().getRecentPosts(
            timestamp,
            object : OnItemsRetrieved<PostModel>{
                override fun onSuccess(list: List<PostModel>) {
                    liveData.value = list
                }

                override fun onFailed(e: Exception) {
                    Log.e(TAG, "Failed to get recent posts", e)
                    liveData.value = null
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