package com.sammengistu.stuckfirebase.models

data class CommentVoteModel(
    val ownerId: String,
    val ownerRef: String,
    val username: String,
    val avatar: String,
    val commentRef: String,
    val postRef: String,
    val voteType: Int
) : FirebaseItem(ownerId, ownerRef) {
    constructor() : this("", "", "", "", "", "",0)
}