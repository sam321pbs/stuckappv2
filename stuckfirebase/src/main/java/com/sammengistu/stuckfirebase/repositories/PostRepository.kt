package com.sammengistu.stuckfirebase.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sammengistu.stuckfirebase.access.FirebaseItemAccess.OnItemsRetrieved
import com.sammengistu.stuckfirebase.access.PostAccess
import com.sammengistu.stuckfirebase.access.StarPostAccess
import com.sammengistu.stuckfirebase.access.UserAccess
import com.sammengistu.stuckfirebase.database.dao.PostDao
import com.sammengistu.stuckfirebase.models.PostModel
import com.sammengistu.stuckfirebase.models.StarPostModel
import com.sammengistu.stuckfirebase.models.UserModel

private const val TAG = "PostRepository"

class PostRepository private constructor(val dao: PostDao) {

    fun getAllDraftPosts(ownerId: String) = dao.getDraftPosts(ownerId)

    fun getDraftPost(draftId: Int) = dao.getDraftPost(draftId)

    fun insertDraftPost(post: PostModel) = dao.insertPost(post)

    fun deleteDraftPost(post: PostModel) = dao.deletePost(post)

    fun deleteDraftPost(draftId: Int) = dao.deleteByPostId(draftId)

    fun getStarPosts(userRef: String, timestamp: Any): LiveData<List<PostModel>?> {
        val liveData = MutableLiveData<List<PostModel>?>()
        StarPostAccess().getUsersStarredPostsBefore(
            userRef,
            timestamp,
            object : OnItemsRetrieved<StarPostModel> {
                override fun onSuccess(list: List<StarPostModel>) {
                    mapUsersToPosts(list, liveData)
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
                    mapUsersToPosts(list, liveData)
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
                    mapUsersToPosts(list, liveData)
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
                    mapUsersToPosts(list, liveData)
                }

                override fun onFailed(e: Exception) {
                    Log.e(TAG, "Failed to get recent posts", e)
                    liveData.value = null
                }

            })
        return liveData
    }

    private fun mapUsersToPosts(posts: List<PostModel>,
                                liveData: MutableLiveData<List<PostModel>?>) {
        val postUserIds = mutableSetOf<String>()

        for (post in posts) {
            postUserIds.add(post.ownerRef)
        }

        UserAccess().getItemsIn(postUserIds, object : OnItemsRetrieved<UserModel>{
            override fun onSuccess(list: List<UserModel>) {
                setOwnerOnPosts(posts, list)
                liveData.value = posts
            }

            override fun onFailed(e: Exception) {
                liveData.value = null
            }
        })
    }

    fun setOwnerOnPosts(posts: List<PostModel>, users: List<UserModel>) {
        for (post in posts) {
            for(user in users) {
                if (post.ownerRef == user.ref) {
                    post.owner = user
                }
            }
        }
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