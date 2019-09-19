package com.sammengistu.stuckapp.fragments

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sammengistu.stuckapp.OnItemClickListener
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.adapters.CategoriesAdapter
import com.sammengistu.stuckapp.constants.Categories
import com.sammengistu.stuckapp.helpers.RecyclerViewHelper
import kotlinx.android.synthetic.main.basic_list_view.*

class CategoriesFragment : BaseFragment(), OnItemClickListener<String> {

    override fun getFragmentTitle(): String = TITLE

    override fun getFragmentTag() = TAG

    override fun onItemClicked(item: String) = addFragment(CategoriesListFragment.newInstance(item))

    override fun getLayoutId() = R.layout.basic_list_view

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

    companion object {
        const val TITLE = "Categories"
        val TAG: String = CategoriesFragment::class.java.simpleName
    }
}