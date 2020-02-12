package com.sammengistu.stuckfirebase.models

data class ReportModel(
    val reason: String,
    val postRef: String,
    // Link to
    val ownerRef: String
) : FirebaseItem(ownerRef)