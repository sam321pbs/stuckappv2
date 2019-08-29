package com.sammengistu.stuckfirebase

import android.content.ContentValues.TAG
import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.sammengistu.stuckfirebase.constants.FirebaseConstants
import com.sammengistu.stuckfirebase.constants.POSTS
import com.sammengistu.stuckfirebase.data.Post

class FirestoreHelper {

    interface OnItemRetrieved<T> {
        fun onSuccess(list: List<T>)
        fun onFailed()
    }

    companion object {
        fun createImagePost(post: Post, bitmap1: Bitmap, bitmap2: Bitmap) {
            var image1Url: String
            var image2Url: String

            FbStorageHelper.uploadImage(bitmap1, object : FbStorageHelper.UploadCompletionCallback {
                override fun onSuccess(url: String) {
                    image1Url = url

                    FbStorageHelper.uploadImage(bitmap2, object : FbStorageHelper.UploadCompletionCallback {
                        override fun onSuccess(url: String) {
                            image2Url = url
                            post.addImage(image1Url)
                            post.addImage(image2Url)
                            createPostInFB(post)
                        }

                        override fun onFailed() {

                        }
                    })
                }

                override fun onFailed() {

                }
            })
        }

        fun createPostInFB(post: Post) {
            getEnvironmentCollectionRef(POSTS)
                .add(post)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "Post snapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding post", e)
                }
        }

        fun getPostData(listener: OnItemRetrieved<Post>) {
            getEnvironmentCollectionRef(POSTS)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        listener.onSuccess(document.toObjects(Post::class.java))
                    } else {
                        Log.d(TAG, "Couldn't get posts")
                    }
                }
                .addOnFailureListener {
                    Log.e(TAG, "Failed to get posts", it)
                    listener.onFailed()
                }
        }

        private fun getEnvironmentCollectionRef(collection: String): CollectionReference {
            val db = FirebaseFirestore.getInstance()
            return db.collection(FirebaseConstants.ENVIRONMENT_COLLECTION)
                .document(FirebaseConstants.ENVIRONMENT_COLLECTION)
                .collection(collection)
        }
    }
}