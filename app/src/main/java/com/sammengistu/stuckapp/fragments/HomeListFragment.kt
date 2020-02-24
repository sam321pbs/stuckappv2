package com.sammengistu.stuckapp.fragments

import com.sammengistu.stuckfirebase.constants.LOAD_TYPE_RECENT

class HomeListFragment : PostsListFragment() {

    override fun getEmptyMessage() = "No Posts.\nYou can be the first to create one!"

    override fun getLoadType(): String = LOAD_TYPE_RECENT

    override fun getFragmentTag(): String = TAG

    companion object {
        private const val TAG: String = "HomeListFragment"
    }
}