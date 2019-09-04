package com.sammengistu.stuckfirebase.access

import android.util.Log
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.sammengistu.stuckfirebase.BuildConfig
import com.sammengistu.stuckfirebase.constants.FirebaseConstants
import com.sammengistu.stuckfirebase.constants.POSTS
import com.sammengistu.stuckfirebase.constants.USERS
import com.sammengistu.stuckfirebase.data.FirebaseItem
import java.lang.ref.WeakReference

abstract class FirebaseItemAccess<T : FirebaseItem> {
    abstract fun getCollectionRef(): CollectionReference
    abstract fun getModelClass(): Class<T>

//    interface OnItemRetrieved<T: FirebaseItem> {
//        fun onSuccess(list: List<T>)
//        fun onFailed()
//    }

    fun createItemInFB(item: T) {
        getCollectionRef()
            .add(item)
            .addOnSuccessListener { documentReference ->
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "Snapshot added with ID: ${documentReference.id}")
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error creating item", e)
            }
    }

    fun deleteItemInFb(documentId: String) {
        getCollectionRef()
            .document(documentId)
            .delete()
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
    }

    fun getItems(listener: OnItemRetrieved<T>) {
        getCollectionRef()
            //Todo: add limit
            .get()
            .addOnSuccessListener(getSuccessListener(listener))
            .addOnFailureListener {
                Log.e(TAG, "Failed to get posts", it)
                listener.onFailed()
            }
    }

    fun getItemsWhereEqual(field: String, value: Any, listener: OnItemRetrieved<T>) {
        getCollectionRef()
            //Todo: add limit
            .whereEqualTo(field, value)
            .get()
            .addOnSuccessListener(getSuccessListener(listener))
            .addOnFailureListener {
                Log.e(TAG, "Failed to get items", it)
                listener.onFailed()
            }
    }

    protected fun getEnvironmentCollectionRef(collection: String): CollectionReference {
        val db = FirebaseFirestore.getInstance()
        return db.collection(FirebaseConstants.ENVIRONMENT_COLLECTION)
            .document(FirebaseConstants.ENVIRONMENT_COLLECTION)
            .collection(collection)
    }

    protected fun getUserCollection(ownerId: String, collection: String): CollectionReference {
        return getEnvironmentCollectionRef(USERS)
            .document(ownerId)
            .collection(collection)
    }

    protected fun getPostCollection(postRef: String, collection: String): CollectionReference {
        return getEnvironmentCollectionRef(POSTS)
            .document(postRef)
            .collection(collection)
    }

    private fun getSuccessListener(listener: OnItemRetrieved<T>): OnSuccessListener<QuerySnapshot> {
        val weakRef = WeakReference(listener)
        return OnSuccessListener { document ->
            val listenerRef = weakRef.get()
            if (listenerRef != null) {
                if (document != null) {
                    val list = document.toObjects(getModelClass())
                    addRefToItems(document, list)
                    listenerRef.onSuccess(list)
                } else {
                    listenerRef.onFailed()
                }
            }
        }
    }

    private fun addRefToItems(document: QuerySnapshot, list: List<T>) {
        val var4 = document.iterator()
        var pos = 0
        while (var4.hasNext()) {
            val d = var4.next() as DocumentSnapshot
            list[pos++].ref = d.reference.id
        }
    }

    companion object {
        val TAG = this::class.java.simpleName
    }

    interface OnItemRetrieved<T: FirebaseItem> {
        fun onSuccess(list: List<T>)
        fun onFailed()
    }
}