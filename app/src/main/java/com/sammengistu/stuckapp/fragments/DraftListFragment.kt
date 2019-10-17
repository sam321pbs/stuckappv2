package com.sammengistu.stuckapp.fragments

class DraftListFragment : PostsListFragment() {

    override fun getEmptyMessage() = "No Drafts"

    override fun getType(): String = TYPE_DRAFT

    override fun getFragmentTag(): String = TAG

    override fun getFragmentTitle(): String = TITLE

    companion object {
        const val TITLE = "Drafts"
        val TAG: String = DraftListFragment::class.java.simpleName
    }
}