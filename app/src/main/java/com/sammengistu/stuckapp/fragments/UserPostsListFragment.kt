package com.sammengistu.stuckapp.fragments

class UserPostsListFragment : PostsListFragment() {

    override fun getType(): String = TYPE_USER

    override fun getFragmentTag(): String = TAG

    override fun getFragmentTitle(): String = TITLE

    companion object {
        const val TITLE = "My Posts"
        val TAG: String = UserPostsListFragment::class.java.simpleName
    }
}