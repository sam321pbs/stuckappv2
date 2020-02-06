package com.sammengistu.stuckfirebase.models

import androidx.room.Ignore
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.FieldValue
import java.util.*

abstract class FirebaseItem(
    // ownerId is a link to the firebase user id
    ownerId: String,
    // ownerRef is a link to the User model ref
    ownerRef: String
) {
    var ref: String = ""

    // Todo: fix this
    @Ignore
    var createdAt: Any? = FieldValue.serverTimestamp()

    @Exclude
    fun getDate(): Date? = (createdAt as Timestamp).toDate()
}