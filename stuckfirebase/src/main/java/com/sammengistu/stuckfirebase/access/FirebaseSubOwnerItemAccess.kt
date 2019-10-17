package com.sammengistu.stuckfirebase.access

import com.google.firebase.firestore.CollectionReference
import com.sammengistu.stuckfirebase.models.FirebaseItem

abstract class FirebaseSubOwnerItemAccess<T : FirebaseItem>(private val userRef: String) :
    FirebaseItemAccess<T>() {

    abstract fun getCollectionName(): String

    override fun getCollectionRef(): CollectionReference {
        return getUserCollectionRef(userRef, getCollectionName())
    }
}