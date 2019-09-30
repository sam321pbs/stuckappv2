package com.sammengistu.stuckapp.bottomsheet

import com.sammengistu.stuckfirebase.models.PostModel

interface BottomSheetMenu {
    fun showMenu(post: PostModel)
    fun hideMenu()
}