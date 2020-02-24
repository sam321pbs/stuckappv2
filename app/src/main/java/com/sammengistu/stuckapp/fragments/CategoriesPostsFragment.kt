package com.sammengistu.stuckapp.fragments

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.sammengistu.stuckapp.activities.BaseActivity
import com.sammengistu.stuckapp.utils.StringUtils
import com.sammengistu.stuckfirebase.constants.LOAD_TYPE_CATEGORIES

class CategoriesPostsFragment : PostsListFragment() {

    private val args: CategoriesPostsFragmentArgs by navArgs()

    override fun getEmptyMessage() =
        "No posts in category:" +
                " \"${StringUtils.capitilizeFirstLetter(getPostCategory())}\" " +
                "\nYou can create a post in this category."

    override fun getLoadType(): String = LOAD_TYPE_CATEGORIES

    override fun getFragmentTag(): String = TAG

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as BaseActivity).title = getPostCategory()
    }

    override fun getPostCategory() = args.category

    companion object {
        private const val TAG: String = "CategoriesPostsFragment"
    }
}