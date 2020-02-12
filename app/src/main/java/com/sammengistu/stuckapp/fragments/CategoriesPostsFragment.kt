package com.sammengistu.stuckapp.fragments

import android.os.Bundle
import com.sammengistu.stuckapp.utils.StringUtils
import com.sammengistu.stuckfirebase.constants.LOAD_TYPE_CATEGORIES

class CategoriesPostsFragment : PostsListFragment() {

    private val TAG: String = CategoriesPostsFragment::class.java.simpleName

    override fun getEmptyMessage() =
            "No posts in category:" +
            " \"${StringUtils.capitilizeFirstLetter(getPostCategory())}\" " +
            "\nYou can create a post in this category."

    override fun getLoadType(): String = LOAD_TYPE_CATEGORIES

    override fun getFragmentTag(): String = TAG

    override fun getFragmentTitle() =
        if (getPostCategory().isBlank()) "Categories" else StringUtils.capitilizeFirstLetter(getPostCategory())

    companion object {
        fun newInstance(category: String): CategoriesPostsFragment {
            val bundle = Bundle()
            bundle.putString(EXTRA_CATEGORY, category)

            val fragment = CategoriesPostsFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}