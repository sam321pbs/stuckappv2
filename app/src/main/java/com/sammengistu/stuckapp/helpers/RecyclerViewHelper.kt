package com.sammengistu.stuckapp.helpers

import android.content.Context
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView



class RecyclerViewHelper {
    companion object {
        fun setupRecyclerView(
            context: Context, recyclerView: RecyclerView,
            viewAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>
        ) {
            val manager = LinearLayoutManager(context)
            recyclerView.apply {
                setHasFixedSize(true)
                layoutManager = manager
                adapter = viewAdapter
            }
            val dividerItemDecoration = DividerItemDecoration(
                recyclerView.context,
                manager.orientation
            )
            recyclerView.addItemDecoration(dividerItemDecoration)
        }
    }
}