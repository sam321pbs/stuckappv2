package com.sammengistu.stuckfirebase.models

data class UserVoteModel(
    val ownerRef: String,
    val username: String,
    val avatar: String,
    val postRef: String,
    val voteItem: String
) : FirebaseItem(ownerRef) {
    constructor() : this( "", "","", "", "")
}