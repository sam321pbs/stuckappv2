package com.sammengistu.stuckapp.utils

import android.view.View

interface CreatePostItem {
    fun fieldsValidated(): Boolean
    fun createPost(view: View)
}