package com.sammengistu.stuckfirebase

import android.graphics.Bitmap
import android.util.Log
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.sammengistu.stuckfirebase.constants.FirebaseConstants
import com.sammengistu.stuckfirebase.constants.POSTS
import com.sammengistu.stuckfirebase.data.Post

class FirestoreHelper {

    interface OnItemRetrieved<T> {
        fun onSuccess(list: List<T>)
        fun onFailed()
    }

    companion object {
        val TAG = FirestoreHelper::class.java.simpleName

        fun createImagePost(post: Post, bitmap1: Bitmap, bitmap2: Bitmap) {
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
                                createTextPostInFB(post)
                            }

                            override fun onFailed() {

                            }
                        })
                }

                override fun onFailed() {

                }
            })
        }

        fun createTextPostInFB(post: Post) {
            getEnvironmentCollectionRef(POSTS)
                .add(post)
                .addOnSuccessListener { documentReference ->
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "Post snapshot added with ID: ${documentReference.id}")
                    }
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding post", e)
                }
        }

        fun getRecentPosts(listener: OnItemRetrieved<Post>) {
            getEnvironmentCollectionRef(POSTS)
                //Todo: add limit
                .get()
                .addOnSuccessListener(getSuccessListener(listener))
                .addOnFailureListener {
                    Log.e(TAG, "Failed to get posts", it)
                    listener.onFailed()
                }
        }

        fun getPostsInCategory(filterCategory: String, listener: OnItemRetrieved<Post>) {
            getEnvironmentCollectionRef(POSTS)
                .whereEqualTo("category", filterCategory)
                //Todo: add limit
                .get()
                .addOnSuccessListener(getSuccessListener(listener))
                .addOnFailureListener {
                    Log.e(TAG, "Failed to get posts", it)
                    listener.onFailed()
                }
        }

        fun getOwnerPosts(ownerId: String, listener: OnItemRetrieved<Post>) {
            getEnvironmentCollectionRef(POSTS)
                .whereEqualTo("owner_id", ownerId)
                //Todo: add limit
                .get()
                .addOnSuccessListener(getSuccessListener(listener))
                .addOnFailureListener {
                    Log.e(TAG, "Failed to get posts", it)
                    listener.onFailed()
                }
        }

        fun deleteItem(collection: String, documentId: String) {
            getEnvironmentCollectionRef(collection)
                .document(documentId)
                .delete()
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
        }

        private fun getSuccessListener(listener: OnItemRetrieved<Post>): OnSuccessListener<QuerySnapshot> {
            return OnSuccessListener { document ->
                if (document != null) {
                    val list = document.toObjects(Post::class.java)
                    addRefToItems(document, list)
                    listener.onSuccess(list)
                } else {
                    Log.d(TAG, "Couldn't get posts")
                }
            }
        }

        private fun addRefToItems(
            document: QuerySnapshot,
            list: List<Post>
        ) {
            val var4 = document.iterator()

            var pos = 0
            while (var4.hasNext()) {
                val d = var4.next() as DocumentSnapshot
                list[pos++].ref = d.reference.id
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