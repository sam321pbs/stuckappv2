package com.sammengistu.stuckfirebase.data

data class CommentModel(
    val postRef: String,
    val ownerId: String,
    val username: String,
    val avatar: String,
    val message: String,
    val usersChoice: Int,
    var upVotes: Int = 0
) : FirebaseItem() {
    constructor():
            this("", "", "", "", "", 0)

}