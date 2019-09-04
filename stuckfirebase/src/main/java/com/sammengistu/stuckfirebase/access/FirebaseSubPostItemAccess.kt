package com.sammengistu.stuckfirebase.access

import com.google.firebase.firestore.CollectionReference
import com.sammengistu.stuckfirebase.data.FirebaseItem

abstract class FirebaseSubPostItemAccess<T : FirebaseItem>(private val postId: String) :
    FirebaseItemAccess<T>() {

    abstract fun getCollectionName(): String

    override fun getCollectionRef(): CollectionReference {
        return getPostCollection(postId, getCollectionName())
    }
}