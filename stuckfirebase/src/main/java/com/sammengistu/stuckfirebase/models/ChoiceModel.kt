package com.sammengistu.stuckfirebase.models

data class ChoiceModel(
    val id: String,
    // Can be choice text or image location
    val data: String,
    val votes: Int)