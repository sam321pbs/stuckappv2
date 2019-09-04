package com.sammengistu.stuckapp.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.sammengistu.stuckapp.DummyDataStuck
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.adapters.PostsAdapter
import com.sammengistu.stuckapp.data.PostAccess
import com.sammengistu.stuckapp.utils.InjectorUtils
import com.sammengistu.stuckfirebase.access.FirebaseItemAccess
import com.sammengistu.stuckfirebase.access.StarPostAccess
import com.sammengistu.stuckfirebase.data.PostModel
import com.sammengistu.stuckfirebase.viewmodels.PostListViewModel
import kotlinx.android.synthetic.main.fragment_post_list.*

class PostsListFragment : BasePostListsFragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: PostsAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var swipeToRefreshLayout: SwipeRefreshLayout

    private val listViewModel: PostListViewModel by viewModels {
        InjectorUtils.providePostListViewModelFactory(requireContext())
    }

    override fun getFragmentTag(): String {
        return TAG
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_post_list
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSwipeToRefresh()
        refreshAdapter(viewAdapter)
        hideMenu()
    }

    override fun onDataUpdated() {
        viewAdapter.notifyDataSetChanged()
    }

    private fun setupSwipeToRefresh() {
        swipeToRefreshLayout = swipe_to_refresh
        swipeToRefreshLayout.setOnRefreshListener {
            swipeToRefreshLayout.isRefreshing = true
            refreshAdapter(viewAdapter)
        }
    }

    private fun setupRecyclerView() {
        viewManager = LinearLayoutManager(this.context)
        viewAdapter = PostsAdapter(this.context!!, mBottomSheetMenu)
        recyclerView = recycler_view.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    private fun refreshAdapter(adapter: PostsAdapter) {
        val category = getPostCategory()
        when {
            getFavorites() -> StarPostAccess(DummyDataStuck.ownerId).getItems(getOnPostRetrievedListener(adapter))
            category.isNotEmpty() -> PostAccess().getPostsInCategory(category, getOnPostRetrievedListener(adapter))
            else -> PostAccess().getRecentPosts(getOnPostRetrievedListener(adapter))
            //        listViewModel.posts.observe(viewLifecycleOwner) { posts ->
            //         adapter.swapData(posts)
            //        }
        }
    }

    private fun getOnPostRetrievedListener(adapter: PostsAdapter): FirebaseItemAccess.OnItemRetrieved<PostModel> {
        return object :
            FirebaseItemAccess.OnItemRetrieved<PostModel> {
            override fun onSuccess(list: List<PostModel>) {
                adapter.swapData(list)
                swipeToRefreshLayout.isRefreshing = false
            }

            override fun onFailed() {
                Toast.makeText(activity!!, "Failed to get posts", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getPostCategory(): String = arguments?.getString(EXTRA_CATEGORY) ?: ""

    private fun getFavorites(): Boolean = arguments?.getBoolean(EXTRA_FAVORITES) ?: false

    companion object {
        val TAG: String = PostsListFragment::class.java.simpleName
        const val EXTRA_CATEGORY = "category"
        const val EXTRA_FAVORITES = "favorite"
        const val EXTRA_OWNER = "owner"

        fun newInstance(category: String): PostsListFragment {
            val bundle = Bundle()
            bundle.putString(EXTRA_CATEGORY, category)

            val fragment = PostsListFragment()
            fragment.arguments = bundle
            return fragment
        }

        fun newInstanceFavorites(): PostsListFragment {
            val bundle = Bundle()
            bundle.putBoolean(EXTRA_FAVORITES, true)

            val fragment = PostsListFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}