package com.sammengistu.stuckapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class Post(
    @PrimaryKey @ColumnInfo(name = "id") val postId: String,
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
    val vote4: Int) {

    companion object Type {
        const val PORTRAIT = "portrait"
        const val LANDSCAPE = "landscape"
        const val TEXT = "text"
    }
}