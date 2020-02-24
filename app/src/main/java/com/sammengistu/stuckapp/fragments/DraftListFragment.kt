package com.sammengistu.stuckapp.fragments

import com.sammengistu.stuckfirebase.constants.LOAD_TYPE_DRAFT

class DraftListFragment : PostsListFragment() {

    override fun getEmptyMessage() = "No Drafts"

    override fun getLoadType(): String = LOAD_TYPE_DRAFT

    override fun getFragmentTag(): String = TAG

    companion object {
        private const val TAG: String = "DraftListFragment"
    }
}