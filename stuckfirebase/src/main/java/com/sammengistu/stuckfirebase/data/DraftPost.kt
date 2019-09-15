package com.sammengistu.stuckapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sammengistu.stuckfirebase.constants.PostType

@Entity(tableName = "posts")
data class DraftPost(
    @PrimaryKey(autoGenerate = true) val postId: Long,
    val question: String,
    val privacy: String,
    val category: String,
    val type: String,
    val image1Loc: String,
    val image2Loc: String,
    val choice1: String,
    val choice2: String,
    val choice3: String,
    val choice4: String
) {

    /**
     * For new text posts
     */
    constructor(
        question: String,
        privacy: String,
        category: String,
        choice1: String, choice2: String, choice3: String, choice4: String
    ) : this(
        0,
        question,
        privacy,
        category,
        PostType.TEXT.toString(),
        "", "",
        choice1, choice2, choice3, choice4
    )

    /**
     * For new image posts
     */
    constructor(
        question: String,
        privacy: String,
        category: String,
        type: String,
        image1Loc: String, image2Loc: String
    ) : this(
        0,
        question,
        privacy,
        category,
        type,
        image1Loc, image2Loc,
        "", "", "", ""
    )
}