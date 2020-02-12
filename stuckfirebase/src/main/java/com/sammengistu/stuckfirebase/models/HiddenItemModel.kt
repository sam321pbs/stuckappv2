package com.sammengistu.stuckfirebase.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hidden_items")
data class HiddenItemModel(
    @PrimaryKey(autoGenerate = true) val _id: Long,
    val ownerRef: String,
    val itemRef: String,
    val type: String) {

    constructor(ownerRef: String,
                itemRef: String,
                type: String): this(0L, ownerRef, itemRef, type)

    companion object {
        const val TYPE_POST = "post"
    }
}