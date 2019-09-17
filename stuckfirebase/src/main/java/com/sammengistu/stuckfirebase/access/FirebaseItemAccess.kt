package com.sammengistu.stuckfirebase.access

import android.util.Log
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.*
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

    protected open fun onItemCreated(item: T) {
        // Override me
    }

    protected open fun onItemDeleted() {
        // Override me
    }

    fun createItemInFB(item: T) {
        getCollectionRef()
            .add(item)
            .addOnSuccessListener { documentReference ->
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "Snapshot added with ID: ${documentReference.id}")
                }
                item.ref = documentReference.id
                onItemCreated(item)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error creating item", e)
            }
    }

    fun createItemInFB(item: T, listener: OnItemCreated<T>) {
        getCollectionRef()
            .add(item)
            .addOnSuccessListener {
                if (it != null) {
                    item.ref = it.id
                    listener.onSuccess(item)
                }
            }
            .addOnFailureListener { e ->
                listener.onFailed(e)
                Log.e(TAG, "Error creating item", e)
            }
    }

    fun updateItemInFB(documentRef: String, updates: Map<String, Any>, listener: OnItemUpdated) {
        getCollectionRef()
            .document(documentRef)
            .update(updates)
            .addOnSuccessListener {
                listener.onSuccess()
            }
            .addOnFailureListener { e ->
                listener.onFailed(e)
                Log.e(TAG, "Error updating item", e)
            }
    }

    fun deleteItemInFb(documentId: String) {
        getCollectionRef()
            .document(documentId)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully deleted!")
                onItemDeleted()
            }
            .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
    }

    fun deleteItemInFb(documentId: String, listener: OnItemDeleted) {
        getCollectionRef()
            .document(documentId)
            .delete()
            .addOnSuccessListener {
                listener.onSuccess()
                Log.d(TAG, "DocumentSnapshot successfully deleted!")
                onItemDeleted()
            }
            .addOnFailureListener { e ->
                listener.onFailed(e)
                Log.w(TAG, "Error deleting document", e)
            }
    }

    fun getItems(listener: OnItemRetrieved<T>) {
        getCollectionRef()
            //Todo: add limit
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener(getSuccessListener(listener))
            .addOnFailureListener {
                Log.e(TAG, "Failed to get posts", it)
                listener.onFailed(it)
            }
    }

    fun getItemsWhereEqual(field: String, value: Any, listener: OnItemRetrieved<T>) {
        getCollectionRef()
            //Todo: add limit
            .whereEqualTo(field, value)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener(getSuccessListener(listener))
            .addOnFailureListener {
                Log.e(TAG, "Failed to get items", it)
                listener.onFailed(it)
            }
    }

    fun getItemsWhereEqual(field: String, value: Any, limit: Long, listener: OnItemRetrieved<T>) {
        getCollectionRef()
            //Todo: add limit
            .whereEqualTo(field, value)
            .limit(limit)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener(getSuccessListener(listener))
            .addOnFailureListener {
                Log.e(TAG, "Failed to get items", it)
                listener.onFailed(it)
            }
    }

    fun incrementField(documentRef: String, fieldToIncrement: String) {
        getCollectionRef()
            .document(documentRef)
            .update(fieldToIncrement, FieldValue.increment(1))
            .addOnSuccessListener {
                Log.d(TAG, "Incremented: $fieldToIncrement")
            }
            .addOnFailureListener {
                Log.e(TAG, "Failed to increment: $fieldToIncrement", it)
            }
    }

    fun incrementField(documentRef: String, fieldToIncrement: String, amount: Int) {
        getCollectionRef()
            .document(documentRef)
            .update(fieldToIncrement, FieldValue.increment(amount.toDouble()))
            .addOnSuccessListener {
                Log.d(TAG, "Incremented: $fieldToIncrement")
            }
            .addOnFailureListener {
                Log.e(TAG, "Failed to increment: $fieldToIncrement", it)
            }
    }

    private fun getSuccessListener(listener: OnItemRetrieved<T>): OnSuccessListener<QuerySnapshot> {
        val weakRef = WeakReference(listener)
        return OnSuccessListener { document ->
            val listenerRef = weakRef.get()
            var resultList: List<T> = ArrayList()
            if (listenerRef != null) {
                if (document != null) {
                    resultList = document.toObjects(getModelClass())
                    addRefToItems(document, resultList)
                }
                listenerRef.onSuccess(resultList)
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

        fun getEnvironmentCollectionRef(collection: String): CollectionReference {
            val db = FirebaseFirestore.getInstance()
            return db.collection(FirebaseConstants.ENVIRONMENT_COLLECTION)
                .document(FirebaseConstants.ENVIRONMENT_COLLECTION)
                .collection(collection)
        }

        fun getUserCollectionRef(userRef: String, collection: String): CollectionReference {
            return getEnvironmentCollectionRef(USERS)
                .document(userRef)
                .collection(collection)
        }

        fun getPostCollectionRef(postRef: String, collection: String): CollectionReference {
            return getEnvironmentCollectionRef(POSTS)
                .document(postRef)
                .collection(collection)
        }
    }

    interface OnItemRetrieved<T : FirebaseItem> {
        fun onSuccess(list: List<T>)
        fun onFailed(e: Exception)
    }

    interface OnItemCreated<T : FirebaseItem> {
        fun onSuccess(item: T)
        fun onFailed(e: Exception)
    }

    interface OnItemUpdated {
        fun onSuccess()
        fun onFailed(e: Exception)
    }

    interface OnItemDeleted {
        fun onSuccess()
        fun onFailed(e: Exception)
    }
}