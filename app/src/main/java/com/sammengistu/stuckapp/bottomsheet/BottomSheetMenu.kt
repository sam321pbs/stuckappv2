package com.sammengistu.stuckapp.bottomsheet

import com.sammengistu.stuckapp.data.Post

interface BottomSheetMenu {
    fun showMenu(post: Post)
    fun hideMenu()
}