package com.sammengistu.stuckfirebase.models

data class CommentVoteModel(
    val ownerRef: String,
    val commentRef: String,
    val postRef: String,
    val voteType: Int
) : FirebaseItem(ownerRef) {
    constructor() : this( "", "", "",0)
}