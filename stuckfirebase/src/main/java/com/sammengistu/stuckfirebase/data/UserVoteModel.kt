package com.sammengistu.stuckfirebase.data

data class UserVoteModel(val ownerId: String, val postRef: String, val voteItem: Int) : FirebaseItem() {
    constructor() : this("", "", 0)
}