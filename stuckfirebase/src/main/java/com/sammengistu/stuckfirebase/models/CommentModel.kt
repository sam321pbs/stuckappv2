package com.sammengistu.stuckfirebase.models

import androidx.room.Ignore
import com.google.firebase.firestore.Exclude

data class CommentModel(
    val postRef: String,
    val ownerRef: String,
    val message: String,
    val usersChoice: Int,
    var upVotes: Int = 0
) : FirebaseItem(ownerRef) {

    @Ignore
    @Exclude
    var owner: UserModel? = null
    constructor() :
            this("","", "", 0)

}