package com.sammengistu.stuckfirebase.constants

enum class PostType {
    PORTRAIT,
    LANDSCAPE,
    TEXT;

    override fun toString(): String {
        return super.toString().toLowerCase()
    }}
