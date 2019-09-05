package com.sammengistu.stuckfirebase.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.FieldValue
import java.util.*

abstract class FirebaseItem {
    var ref : String = ""
    var createdAt: Any? = FieldValue.serverTimestamp()

    @Exclude
    fun getDate(): Date? = (createdAt as Timestamp).toDate()
}