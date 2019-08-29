package com.sammengistu.stuckapp.bottomsheet

import com.sammengistu.stuckfirebase.data.Post

interface BottomSheetMenu {
    fun showMenu(post: Post)
    fun hideMenu()
}