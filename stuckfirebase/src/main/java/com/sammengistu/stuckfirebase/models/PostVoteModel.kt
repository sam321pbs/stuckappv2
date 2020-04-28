package com.sammengistu.stuckfirebase.models

data class PostVoteModel(
    val ownerRef: String,
    val postRef: String,
    val postOwnerRef: String,
    val choiceId: String
) : FirebaseItem(ownerRef) {
    constructor() : this( "", "", "","")
}