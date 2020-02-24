package com.sammengistu.stuckapp.fragments

import com.sammengistu.stuckfirebase.constants.LOAD_TYPE_USER

class UserPostsListFragment : PostsListFragment() {
    override fun getEmptyMessage() = "You don't have any posts.\nCreate a post to see it here."

    override fun getLoadType(): String = LOAD_TYPE_USER

    override fun getFragmentTag(): String = TAG

    companion object {
        const val TAG: String = "UserPostsListFragment"
    }
}