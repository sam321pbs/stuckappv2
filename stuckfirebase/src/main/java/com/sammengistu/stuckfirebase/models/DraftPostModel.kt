package com.sammengistu.stuckfirebase.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class DraftPostModel(
    @PrimaryKey(autoGenerate = true) val _id: Int?,
    val ownerRef: String,
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
)