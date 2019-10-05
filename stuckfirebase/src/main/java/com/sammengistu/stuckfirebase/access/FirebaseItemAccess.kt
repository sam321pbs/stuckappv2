package com.sammengistu.stuckfirebase.access

import android.util.Log
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.*
import com.sammengistu.stuckfirebase.constants.FirebaseConstants
import com.sammengistu.stuckfirebase.constants.POSTS
import com.sammengistu.stuckfirebase.constants.USERS
import com.sammengistu.stuckfirebase.exceptions.DocNotExistException
import com.sammengistu.stuckfirebase.models.FirebaseItem
import java.lang.ref.WeakReference

abstract class FirebaseItemAccess<T : FirebaseItem> {
    abstract fun getCollectionRef(): CollectionReference
    abstract fun getModelClass(): Class<T>

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
                Log.d(TAG, "Successfully created ${getModelClass().simpleName}")
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
                    Log.d(TAG, "Successfully created ${getModelClass().simpleName}")
                    item.ref = it.id
                    listener.onSuccess(item)
                }
            }
            .addOnFailureListener { e ->
                listener.onFailed(e)
                Log.e(TAG, "Error creating ${getModelClass().simpleName}", e)
            }
    }

    fun updateItemInFB(documentRef: String, updates: Map<String, Any>, listener: OnItemUpdated?) {
        getCollectionRef()
            .document(documentRef)
            .update(updates)
            .addOnSuccessListener {
                listener?.onSuccess()
                Log.d(TAG, "Successful update of ${getModelClass().simpleName}")
            }
            .addOnFailureListener { e ->
                listener?.onFailed(e)
                Log.e(TAG, "Error updating ${getModelClass().simpleName}", e)
            }
    }

    fun deleteItemInFb(documentId: String) {
        getCollectionRef()
            .document(documentId)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "${getModelClass().simpleName} successfully deleted!")
                onItemDeleted()
            }
            .addOnFailureListener { e -> Log.w(TAG, "Error deleting ${getModelClass().simpleName}", e) }
    }

    fun deleteItemInFb(documentId: String, listener: OnItemDeleted) {
        getCollectionRef()
            .document(documentId)
            .delete()
            .addOnSuccessListener {
                listener.onSuccess()
                Log.d(TAG, "${getModelClass().simpleName} successfully deleted!")
                onItemDeleted()
            }
            .addOnFailureListener { e ->
                listener.onFailed(e)
                Log.w(TAG, "Error deleting ${getModelClass().simpleName}", e)
            }
    }

    fun getItem(ref: String, listener: OnItemRetrieved<T>) {
        getCollectionRef()
            .document(ref)
            .get()
            .addOnSuccessListener(getDocumentSuccessListener(listener))
            .addOnFailureListener {
                Log.e(TAG, "Failed to get ${getModelClass().simpleName}", it)
                listener.onFailed(it)
            }
    }

    fun getItems(listener: OnItemsRetrieved<T>) {
        getCollectionRef()
            .limit(QUERY_LIMIT)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener(getSuccessListener(listener))
            .addOnFailureListener {
                Log.e(TAG, "Failed to get ${getModelClass().simpleName}", it)
                listener.onFailed(it)
            }
    }

    fun getItemsWhereEqual(field: String, value: Any, listener: OnItemsRetrieved<T>) {
        getCollectionRef()
            .limit(QUERY_LIMIT)
            .whereEqualTo(field, value)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener(getSuccessListener(listener))
            .addOnFailureListener {
                Log.e(TAG, "Failed to get ${getModelClass().simpleName}", it)
                listener.onFailed(it)
            }
    }

    fun getItemsWhereEqual(field: String, value: Any, limit: Long, listener: OnItemsRetrieved<T>) {
        getCollectionRef()
            .limit(QUERY_LIMIT)
            .whereEqualTo(field, value)
            .limit(limit)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener(getSuccessListener(listener))
            .addOnFailureListener {
                Log.e(TAG, "Failed to get ${getModelClass().simpleName}", it)
                listener.onFailed(it)
            }
    }

    fun getItemsBefore(beforeTime: Any?, listener: OnItemsRetrieved<T>) {
        Log.d(TAG, "Getting list of ${getModelClass().simpleName} before")
        if (beforeTime != null) {
            getCollectionRef()
                .limit(QUERY_LIMIT)
                .whereLessThan("createdAt", beforeTime)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(getSuccessListener(listener))
                .addOnFailureListener {
                    Log.e(TAG, "Failed to get ${getModelClass().simpleName}", it)
                    listener.onFailed(it)
                }
        }
    }

    fun getItemsWhereEqualAndBefore(
        field: String,
        value: Any,
        beforeTime: Any?,
        listener: OnItemsRetrieved<T>
    ) {
        Log.d(TAG, "Getting list of ${getModelClass().simpleName} where and before")
        if (beforeTime != null) {
            getCollectionRef()
                .limit(QUERY_LIMIT)
                .whereEqualTo(field, value)
                .whereLessThan("createdAt", beforeTime)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(getSuccessListener(listener))
                .addOnFailureListener {
                    Log.e(TAG, "Failed to get ${getModelClass().simpleName}", it)
                    listener.onFailed(it)
                }
        }
    }

    fun incrementField(documentRef: String, fieldToIncrement: String) {
        incrementField(documentRef, fieldToIncrement, 1)
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

    protected fun getEnvironmentCollectionRef(collection: String): CollectionReference {
        val db = FirebaseFirestore.getInstance()
        return db.collection(FirebaseConstants.ENVIRONMENT_COLLECTION)
            .document(FirebaseConstants.ENVIRONMENT_COLLECTION)
            .collection(collection)
    }

    protected fun getUserCollectionRef(userRef: String, collection: String): CollectionReference {
        return getEnvironmentCollectionRef(USERS)
            .document(userRef)
            .collection(collection)
    }

    protected fun getPostCollectionRef(postRef: String, collection: String): CollectionReference {
        return getEnvironmentCollectionRef(POSTS)
            .document(postRef)
            .collection(collection)
    }

    private fun getSuccessListener(listener: OnItemsRetrieved<T>): OnSuccessListener<QuerySnapshot> {
        val weakRef = WeakReference(listener)
        return OnSuccessListener { document ->
            val listenerRef = listener
            var resultList: List<T> = ArrayList()
            if (listenerRef != null) {
                if (document != null) {
                    resultList = document.toObjects(getModelClass())
                    Log.d(TAG, "Retrieved ${resultList.size} ${getModelClass().simpleName}")
                    addRefToItems(document, resultList)
                }
                listenerRef.onSuccess(resultList)
            } else {
                Log.d(TAG, "ListenerRef was null")
            }
        }
    }

    private fun getDocumentSuccessListener(listener: OnItemRetrieved<T>): OnSuccessListener<DocumentSnapshot> {
        val weakRef = WeakReference(listener)
        return OnSuccessListener { document ->
//            val listenerRef = weakRef.get()
            val listenerRef = listener
            if (listenerRef != null) {
                if (document != null) {
                    if (document.exists()) {
                        try {
                            val item = document.toObject(getModelClass())
                            Log.d(TAG, "Retrieved single ${getModelClass().simpleName}")
                            item!!.ref = document.reference.id
                            listenerRef.onSuccess(item)
                        } catch (e: Exception) {
                            listenerRef.onFailed(e)
                        }
                    } else {
                        listenerRef.onFailed(DocNotExistException())
                    }
                }
            } else {
                Log.d(TAG, "ListenerRef was null")
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

    interface OnItemsRetrieved<T : FirebaseItem> {
        fun onSuccess(list: List<T>)
        fun onFailed(e: Exception)
    }

    interface OnItemRetrieved<T : FirebaseItem> {
        fun onSuccess(item: T)
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

    companion object {
        val TAG = this::class.java.simpleName
        const val QUERY_LIMIT = 50L
    }
}