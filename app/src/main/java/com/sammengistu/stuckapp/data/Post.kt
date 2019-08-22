package com.sammengistu.stuckapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sammengistu.stuckapp.constants.PostType

@Entity(tableName = "posts")
data class Post(
    @PrimaryKey(autoGenerate = true) val postId: Long,
    val ownerId: String,
    val userName: String,
    val createdAt: String,
    val avatar: String,
    val question: String,
    val totalStars: Int,
    val totalComments: Int,
    val category: String,
    val type: String,
    val image1Loc: String,
    val image2Loc: String,
    val choice1: String,
    val choice2: String,
    val choice3: String,
    val choice4: String,
    val vote1: Int,
    val vote2: Int,
    val vote3: Int,
    val vote4: Int
) {

    /**
     * For new text posts
     */
    constructor(
        ownerId: String,
        userName: String,
        question: String,
        category: String,
        choice1: String, choice2: String, choice3: String, choice4: String
    ) : this(
        0,
        ownerId,
        userName,
        "",
        "",
        question,
        0, 0,
        category,
        PostType.TEXT,
        "", "",
        choice1, choice2, choice3, choice4,
        0, 0, 0, 0
    )

    /**
     * For new image posts
     */
    constructor(
        ownerId: String,
        userName: String,
        question: String,
        category: String,
        type: String,
        image1Loc: String, image2Loc: String
    ) : this(
        0,
        ownerId,
        userName,
        "",
        "",
        question,
        0, 0,
        category,
        type,
        image1Loc, image2Loc,
        "", "", "", "",
        0, 0, 0, 0
    )
}