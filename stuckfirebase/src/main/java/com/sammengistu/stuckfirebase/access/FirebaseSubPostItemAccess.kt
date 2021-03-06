package com.sammengistu.stuckfirebase.access

import com.google.firebase.firestore.CollectionReference
import com.sammengistu.stuckfirebase.models.FirebaseItem

abstract class FirebaseSubPostItemAccess<T : FirebaseItem>(private val postId: String) :
    FirebaseItemAccess<T>() {

    abstract fun getCollectionName(): String

    override fun getCollectionRef(): CollectionReference {
        return getPostCollectionRef(postId, getCollectionName())
    }
}