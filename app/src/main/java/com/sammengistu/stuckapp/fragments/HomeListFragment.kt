package com.sammengistu.stuckapp.fragments

class HomeListFragment : PostsListFragment() {

    override fun getEmptyMessage() = "No Posts.\nYou can be the first to create one!"

    override fun getType(): String = TYPE_HOME

    override fun getFragmentTag(): String = TAG

    override fun getFragmentTitle(): String = TITLE

    companion object {
        const val TITLE = "Home"
        val TAG: String = HomeListFragment::class.java.simpleName
    }
}