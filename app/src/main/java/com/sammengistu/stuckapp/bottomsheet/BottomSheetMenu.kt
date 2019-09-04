package com.sammengistu.stuckapp.bottomsheet

import com.sammengistu.stuckfirebase.data.PostModel

interface BottomSheetMenu {
    fun showMenu(post: PostModel)
    fun hideMenu()
}