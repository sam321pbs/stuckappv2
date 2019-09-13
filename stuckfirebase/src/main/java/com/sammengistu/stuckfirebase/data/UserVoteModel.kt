package com.sammengistu.stuckfirebase.data

data class UserVoteModel(
    val ownerId: String,
    val ownerRef: String,
    val username: String,
    val avatar: String,
    val postRef: String,
    val postOwnerRef: String,
    val voteItem: String
) : FirebaseItem() {
    constructor() : this("", "", "", "","", "", "")
}