package com.sammengistu.stuckfirebase.models

class ReportModel(
    val reason: String,
    val postRef: String,
    // Link to
    val ownerRef: String,
    val ownerId: String
) : FirebaseItem(ownerId, ownerRef)