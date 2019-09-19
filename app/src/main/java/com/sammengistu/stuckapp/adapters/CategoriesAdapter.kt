package com.sammengistu.stuckapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sammengistu.stuckapp.OnItemClickListener
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.utils.StringUtils
import org.jetbrains.anko.find


class CategoriesAdapter(private val itemClickListener: OnItemClickListener<String>,
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
        holder.text.text = StringUtils.capitilizeFirstLetter(dataset[position])
        holder.itemView.setOnClickListener { itemClickListener.onItemClicked(dataset[position]) }
    }

    override fun getItemCount() = dataset.size

    open class CategoryViewHolder(parentView: View) : RecyclerView.ViewHolder(parentView) {
        val icon: ImageView = parentView.find(R.id.icon)
        val text: TextView = parentView.find(R.id.category_text)
    }
}