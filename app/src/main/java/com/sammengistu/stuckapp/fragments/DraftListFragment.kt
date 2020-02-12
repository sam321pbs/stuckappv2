package com.sammengistu.stuckapp.fragments

import com.sammengistu.stuckfirebase.constants.LOAD_TYPE_DRAFT

class DraftListFragment : PostsListFragment() {

    override fun getEmptyMessage() = "No Drafts"

    override fun getLoadType(): String = LOAD_TYPE_DRAFT

    override fun getFragmentTag(): String = TAG

    override fun getFragmentTitle(): String = TITLE

    companion object {
        const val TITLE = "Drafts"
        val TAG: String = DraftListFragment::class.java.simpleName
    }
}