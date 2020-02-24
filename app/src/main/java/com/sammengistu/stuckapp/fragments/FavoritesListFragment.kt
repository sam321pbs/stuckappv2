package com.sammengistu.stuckapp.fragments

import com.sammengistu.stuckfirebase.constants.LOAD_TYPE_FAVORITE

class FavoritesListFragment : PostsListFragment() {

    override fun getEmptyMessage() = "No Favorites.\nFavorite a post to see it here."

    override fun getLoadType(): String = LOAD_TYPE_FAVORITE

    override fun getFragmentTag(): String = TAG

    companion object {
        private const val TAG = "FavoritesListFragment"
    }
}