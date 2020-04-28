package com.sammengistu.stuckfirebase.models

import androidx.room.Ignore
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.FieldValue
import java.util.*

abstract class FirebaseItem(
    // ownerId is a link to the user ref
    ownerRef: String
) {
    @Exclude
    var ref: String = ""

    @Ignore
    var createdAt: Any? = FieldValue.serverTimestamp()

    @Exclude
    fun getDate(): Date? = (createdAt as Timestamp).toDate()
}