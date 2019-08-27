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
    val privacy: String,
    val category: String,
    val type: String,
    val image1Loc: String,
    val image2Loc: String,
    protected val choice1: String,
    protected val choice2: String,
    protected val choice3: String,
    protected val choice4: String,
    protected val vote1: Int,
    protected val vote2: Int,
    protected val vote3: Int,
    protected val vote4: Int
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

    fun getTotalVotes(): Int {
        return vote1.plus(vote2).plus(vote3).plus(vote4)
    }

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