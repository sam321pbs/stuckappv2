package com.sammengistu.stuckapp.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.sammengistu.stuckapp.OnItemClickListener
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.adapters.CategoriesAdapter
import com.sammengistu.stuckapp.constants.Categories
import com.sammengistu.stuckapp.fragments.PostsListFragment.Companion.EXTRA_CATEGORY
import com.sammengistu.stuckapp.helpers.RecyclerViewHelper
import kotlinx.android.synthetic.main.basic_list_view.*

class CategoriesFragment : BaseFragment(), OnItemClickListener<String> {

    companion object {
        val TAG: String = CategoriesFragment::class.java.simpleName
    }

    override fun getFragmentTag(): String {
        return TAG
    }

    override fun onItemClicked(category: String) {
        Toast.makeText(context, category, Toast.LENGTH_SHORT).show()
        addFragment(PostsListFragment.newInstance(EXTRA_CATEGORY, category))
    }

    override fun getLayoutId(): Int {
        return R.layout.basic_list_view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        RecyclerViewHelper.setupRecyclerView(
            activity!!, recycler_view,
            CategoriesAdapter(
                this,
                Categories.asList()
            ) as RecyclerView.Adapter<RecyclerView.ViewHolder>
        )
    }
}