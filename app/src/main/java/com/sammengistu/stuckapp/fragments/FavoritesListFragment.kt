package com.sammengistu.stuckapp.fragments

class FavoritesListFragment : PostsListFragment() {

    override fun getEmptyMessage() = "No Favorites.\nFavorite a post to see it here."

    override fun getType(): String = TYPE_FAVORITE

    override fun getFragmentTag(): String = TAG

    override fun getFragmentTitle(): String = TITLE

    companion object {
        val TAG = FavoritesListFragment::class.java.simpleName
        const val TITLE = "Favorites"
    }
}