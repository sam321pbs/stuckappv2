package com.sammengistu.stuckfirebase.access

import com.google.firebase.firestore.CollectionReference
import com.sammengistu.stuckfirebase.data.FirebaseItem

abstract class FirebaseSubOwnerItemAccess<T : FirebaseItem>(private val userId: String) :
    FirebaseItemAccess<T>() {

    abstract fun getCollectionName(): String

    override fun getCollectionRef(): CollectionReference {
        return getUserCollection(userId, getCollectionName())
    }
}