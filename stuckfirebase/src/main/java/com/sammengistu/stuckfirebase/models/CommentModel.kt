package com.sammengistu.stuckfirebase.models

data class CommentModel(
    val postRef: String,
    val ownerRef: String,
    val message: String,
    val usersChoice: Int,
    var upVotes: Int = 0
) : FirebaseItem(ownerRef) {
    constructor() :
            this("","", "", 0)

}