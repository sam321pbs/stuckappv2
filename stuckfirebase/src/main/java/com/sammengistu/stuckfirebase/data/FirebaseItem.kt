package com.sammengistu.stuckfirebase.data

import com.google.firebase.firestore.FieldValue

abstract class FirebaseItem {
    var ref : String = ""
    var createdAt: Any? = FieldValue.serverTimestamp()
}