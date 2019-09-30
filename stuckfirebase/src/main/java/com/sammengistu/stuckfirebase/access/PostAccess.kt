package com.sammengistu.stuckfirebase.access

import android.content.Context
import android.graphics.Bitmap
import com.sammengistu.stuckapp.data.AppDatabase
import com.sammengistu.stuckfirebase.FbStorageHelper
import com.sammengistu.stuckfirebase.constants.POSTS
import com.sammengistu.stuckfirebase.database.DraftPostModel
import com.sammengistu.stuckfirebase.database.PostRepository
import com.sammengistu.stuckfirebase.models.PostModel

class PostAccess: FirebaseItemAccess<PostModel>() {
    override fun getModelClass() = PostModel::class.java

    override fun getCollectionRef() = getEnvironmentCollectionRef(POSTS)

    fun incrementVote(ref: String, key: String) {
        incrementField(ref, "votes.$key")
    }

    fun incrementStarTotal(ref: String) {
        incrementField(ref, "totalStars")
    }

    fun getRecentPosts(listener: OnItemsRetrieved<PostModel>) {
        getItems(listener)
    }

    fun getRecentPosts(before: Any?, listener: OnItemsRetrieved<PostModel>) {
        getItemsBefore(before, listener)
    }

    fun getPostsInCategory(filterCategory: String, listener: OnItemsRetrieved<PostModel>) {
        getItemsWhereEqual("category", filterCategory, listener)
    }

    fun getPostsInCategory(filterCategory: String, before: Any?, listener: OnItemsRetrieved<PostModel>) {
        getItemsWhereEqualAndBefore("category", filterCategory, before, listener)
    }

    fun getOwnerPosts(ownerId: String, listener: OnItemsRetrieved<PostModel>) {
        getItemsWhereEqual("ownerId", ownerId, listener)
    }

    fun getOwnerPosts(ownerId: String, before: Any?, listener: OnItemsRetrieved<PostModel>) {
        getItemsWhereEqualAndBefore("ownerId", ownerId, before, listener)
    }

    fun createImagePost(post: PostModel, bitmap1: Bitmap, bitmap2: Bitmap, listener: OnItemCreated<PostModel>) {
        var image1Url: String
        var image2Url: String

        // Todo: delete post if it failed and this is probably not the best implementation
        FbStorageHelper.uploadImage(bitmap1, object : FbStorageHelper.UploadCompletionCallback {
            override fun onSuccess(url: String) {
                image1Url = url

                FbStorageHelper.uploadImage(
                    bitmap2,
                    object : FbStorageHelper.UploadCompletionCallback {
                        override fun onSuccess(url: String) {
                            image2Url = url
                            post.addImage(image1Url)
                            post.addImage(image2Url)
                            createItemInFB(post, listener)
                        }

                        override fun onFailed(exception: Exception) {
                            listener.onFailed(exception)
                        }
                    })
            }

            override fun onFailed(exception: Exception) {
                listener.onFailed(exception)
            }
        })
    }

    companion object {
        fun insertPost(context: Context, post: DraftPostModel) {
            PostRepository.getInstance(AppDatabase.getInstance(context).postsDao()).insertPost(post)
        }

        fun getPost(context: Context, postId: Long) : DraftPostModel? {
            return PostRepository.getInstance(AppDatabase.getInstance(context).postsDao()).getPost(postId).value?.get(0)
        }

        // todo this does not work
        fun deletePost(context: Context, post: DraftPostModel) {
            PostRepository.getInstance(AppDatabase.getInstance(context).postsDao()).deletePost(post)
        }

        fun deletePost(context: Context, postId: Long) {
            PostRepository.getInstance(AppDatabase.getInstance(context).postsDao()).deletePost(postId)
        }
    }
}