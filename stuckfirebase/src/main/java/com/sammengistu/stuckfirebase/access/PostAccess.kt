package com.sammengistu.stuckfirebase.access

import android.content.Context
import android.graphics.Bitmap
import com.google.firebase.firestore.CollectionReference
import com.sammengistu.stuckapp.data.AppDatabase
import com.sammengistu.stuckapp.data.DraftPost
import com.sammengistu.stuckapp.data.PostRepository
import com.sammengistu.stuckfirebase.FbStorageHelper
import com.sammengistu.stuckfirebase.constants.POSTS
import com.sammengistu.stuckfirebase.data.PostModel

class PostAccess: FirebaseItemAccess<PostModel>() {
    override fun getModelClass(): Class<PostModel> {
        return PostModel::class.java
    }

    override fun getCollectionRef(): CollectionReference {
        return getEnvironmentCollectionRef(POSTS)
    }

    fun incrementVote(ref: String, key: String) {
        incrementField(ref, "votes.$key")
    }

    fun incrementStarTotal(ref: String) {
        incrementField(ref, "totalStars")
    }

    fun getRecentPosts(listener: OnItemRetrieved<PostModel>) {
        getItems(listener)
    }

    fun getPostsInCategory(filterCategory: String, listener: OnItemRetrieved<PostModel>) {
        getItemsWhereEqual("category", filterCategory, listener)
    }

    fun getOwnerPosts(ownerId: String, listener: OnItemRetrieved<PostModel>) {
        getItemsWhereEqual("ownerId", ownerId, listener)
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
        fun insertPost(context: Context, post: DraftPost) {
            PostRepository.getInstance(AppDatabase.getInstance(context).postsDao()).insertPost(post)
        }

        fun deletePost(context: Context, post: DraftPost) {
            PostRepository.getInstance(AppDatabase.getInstance(context).postsDao()).deletePost(post)
        }
    }
}