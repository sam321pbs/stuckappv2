package com.sammengistu.stuckapp.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.sammengistu.stuckapp.OnItemClickListener
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.RecyclerViewHelper
import com.sammengistu.stuckapp.adapters.CategoriesAdapter
import com.sammengistu.stuckapp.constants.Category
import kotlinx.android.synthetic.main.basic_list_view.*

class CategoriesFragment : BaseFragment(), OnItemClickListener<String> {
    override fun onItemClicked(item: String) {
        Toast.makeText(context, item, Toast.LENGTH_SHORT).show()
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
                Category.sortCategories
            ) as RecyclerView.Adapter<RecyclerView.ViewHolder>
        )
    }
}