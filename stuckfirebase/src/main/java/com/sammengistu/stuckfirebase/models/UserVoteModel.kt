package com.sammengistu.stuckfirebase.models

data class UserVoteModel(
    val ownerRef: String,
    val postRef: String,
    val choiceId: String
) : FirebaseItem(ownerRef) {
    constructor() : this( "", "","")
}