package com.sammengistu.stuckapp

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewHelper {
    companion object {
        fun setupRecyclerView(
            context: Context, recyclerView: RecyclerView,
            viewAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>
        ) {
            val viewManager = LinearLayoutManager(context)
            recyclerView.apply {
                setHasFixedSize(true)
                layoutManager = viewManager
                adapter = viewAdapter
            }
        }
    }
}