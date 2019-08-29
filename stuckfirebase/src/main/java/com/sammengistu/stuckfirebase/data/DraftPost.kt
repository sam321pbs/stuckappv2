package com.sammengistu.stuckapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude
import com.sammengistu.stuckfirebase.constants.PostType

@Entity(tableName = "posts")
data class DraftPost(
    @PrimaryKey(autoGenerate = true) val postId: Long,
    val ownerId: String,
    val userName: String,
    val createdAt: String,
    val avatar: String,
    val question: String,
    val totalStars: Int,
    val totalComments: Int,
    val privacy: String,
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
        privacy: String,
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
        privacy,
        category,
        PostType.TEXT.toString(),
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
        privacy: String,
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
        privacy,
        category,
        type,
        image1Loc, image2Loc,
        "", "", "", "",
        0, 0, 0, 0
    )

    @Exclude
    fun getChoicesToVoteList(): List<Triple<Int, String, Int>> {
        val list = mutableListOf<Triple<Int, String, Int>>()
        if (!choice1.isEmpty()) {
            list.add(Triple(1, choice1, vote1))
        }
        if (!choice2.isEmpty()) {
            list.add(Triple(2, choice2, vote2))
        }
        if (!choice3.isEmpty()) {
            list.add(Triple(3, choice3, vote3))
        }
        if (!choice4.isEmpty()) {
            list.add(Triple(4, choice4, vote4))
        }
        return list
    }
}