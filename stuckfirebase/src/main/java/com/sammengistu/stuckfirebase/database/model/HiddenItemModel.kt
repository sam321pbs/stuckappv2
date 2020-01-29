package com.sammengistu.stuckfirebase.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hidden_items")
data class HiddenItemModel(
    @PrimaryKey(autoGenerate = true) val _id: Long,
    val ownerId: String,
    val ownerRef: String,
    val itemRef: String,
    val type: String) {

    constructor(ownerId: String,
                ownerRef: String,
                itemRef: String,
                type: String): this(0L, ownerId, ownerRef, itemRef, type)

    companion object {
        const val TYPE_POST = "post"
    }
}