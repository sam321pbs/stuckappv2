package com.sammengistu.stuckfirebase.data

data class CommentVoteModel(
    val ownerId: String,
    val ownerRef: String,
    val username: String,
    val avatar: String,
    val commentRef: String,
    val postRef: String,
    val voteType: Int
) : FirebaseItem() {
    constructor() : this("", "", "", "", "", "",0)
}