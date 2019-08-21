package com.sammengistu.stuckapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.adapters.PostsAdapter
import com.sammengistu.stuckapp.utils.InjectorUtils
import com.sammengistu.stuckapp.viewmodels.PostListViewModel
import kotlinx.android.synthetic.main.fragment_post_list.*

class PostsListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: PostsAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    private val listViewModel: PostListViewModel by viewModels {
        InjectorUtils.providePostListViewModelFactory(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_post_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        subscribeUi(viewAdapter)
    }

    private fun setupRecyclerView() {
        viewManager = LinearLayoutManager(this.context)
        viewAdapter = PostsAdapter(emptyList())

        recyclerView = recycler_view.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter

        }
    }

    private fun subscribeUi(adapter: PostsAdapter) {
        listViewModel.posts.observe(viewLifecycleOwner) { posts ->
         adapter.swapData(posts)
        }
    }
}