package com.sammengistu.stuckapp.fragments

import android.os.Bundle

class CategoriesListFragment : PostsListFragment() {
    override fun getType(): String = TYPE_CATEGORIES

    override fun getFragmentTag(): String = TAG

    override fun getFragmentTitle(): String {
        if (getPostCategory().isNotBlank()) {
            var title = getPostCategory()
            val firstLetter = title[0].toString().toUpperCase()
            title = firstLetter + title.substring(1, title.length)
            return title
        }
        return TITLE
    }

    companion object {
        const val TITLE = "Categories"
        val TAG: String = CategoriesListFragment::class.java.simpleName

        fun newInstance(category: String): CategoriesListFragment {
            val bundle = Bundle()
            bundle.putString(EXTRA_CATEGORY, category)

            val fragment = CategoriesListFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}