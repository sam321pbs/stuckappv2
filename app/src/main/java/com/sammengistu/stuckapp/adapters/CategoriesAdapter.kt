package com.sammengistu.stuckapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.RecyclerViewHelper
import com.sammengistu.stuckapp.views.IconToTextView
import org.jetbrains.anko.find


class CategoriesAdapter(private val itemClickListener: RecyclerViewHelper.OnItemClickListener<String>,
                        private val dataset: List<String>) :
    RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_category,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.iconToTextView.setText(dataset[position])
        holder.iconToTextView.setOnClickListener { itemClickListener.onItemClicked(dataset[position]) }
    }

    override fun getItemCount() = dataset.size

    open class CategoryViewHolder(parentView: View) : RecyclerView.ViewHolder(parentView) {
        val iconToTextView: IconToTextView = parentView.find(R.id.category_icon_text)
    }
}