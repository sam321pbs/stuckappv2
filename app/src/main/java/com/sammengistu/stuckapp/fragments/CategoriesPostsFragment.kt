package com.sammengistu.stuckapp.fragments

import android.os.Bundle
import com.sammengistu.stuckapp.utils.StringUtils

class CategoriesPostsFragment : PostsListFragment() {

    override fun getEmptyMessage() =
            "No posts in category:" +
            " \"${StringUtils.capitilizeFirstLetter(getPostCategory())}\" " +
            "\nYou can create a post in this category."

    override fun getType(): String = TYPE_CATEGORIES

    override fun getFragmentTag(): String = TAG

    override fun getFragmentTitle() =
        if (getPostCategory().isBlank()) TITLE else StringUtils.capitilizeFirstLetter(getPostCategory())

    companion object {
        const val TITLE = "Categories"
        val TAG: String = CategoriesPostsFragment::class.java.simpleName

        fun newInstance(category: String): CategoriesPostsFragment {
            val bundle = Bundle()
            bundle.putString(EXTRA_CATEGORY, category)

            val fragment = CategoriesPostsFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}