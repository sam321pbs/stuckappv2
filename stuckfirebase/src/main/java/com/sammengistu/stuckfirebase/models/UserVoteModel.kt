package com.sammengistu.stuckfirebase.models

data class UserVoteModel(
    val ownerRef: String,
    val postRef: String,
    val postOwnerRef: String,
    val choiceId: String
) : FirebaseItem(ownerRef) {
    constructor() : this( "", "", "","")
}