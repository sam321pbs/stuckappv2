package com.sammengistu.stuckapp.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.adapters.PostsAdapter
import com.sammengistu.stuckapp.utils.InjectorUtils
import com.sammengistu.stuckapp.viewmodels.PostListViewModel
import com.sammengistu.stuckfirebase.FirestoreHelper
import com.sammengistu.stuckfirebase.data.Post
import kotlinx.android.synthetic.main.fragment_post_list.*

class PostsListFragment : BasePostListsFragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: PostsAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun getFragmentTag(): String {
        return TAG
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_post_list
    }

    private val listViewModel: PostListViewModel by viewModels {
        InjectorUtils.providePostListViewModelFactory(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        subscribeUi(viewAdapter)
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

    private fun subscribeUi(adapter: PostsAdapter) {
        val category = getPostCategory()
        if (category.isNotEmpty()) {
            FirestoreHelper.getPostsInCategory(category, object : FirestoreHelper.OnItemRetrieved<Post> {
                override fun onSuccess(list: List<Post>) {
                    adapter.swapData(list)
                }

                override fun onFailed() {
                    Toast.makeText(activity!!, "Failed to get posts", Toast.LENGTH_SHORT).show()
                }

            })
        } else {
            FirestoreHelper.getRecentPosts(object : FirestoreHelper.OnItemRetrieved<Post> {
                override fun onSuccess(list: List<Post>) {
                    adapter.swapData(list)
                }

                override fun onFailed() {
                    Toast.makeText(activity!!, "Failed to get posts", Toast.LENGTH_SHORT).show()
                }

            })
//        listViewModel.posts.observe(viewLifecycleOwner) { posts ->
//         adapter.swapData(posts)
//        }
        }
    }

    private fun getPostCategory(): String = arguments?.getString(EXTRA_CATEGORY) ?: ""

    companion object {
        val TAG: String = PostsListFragment::class.java.simpleName
        const val EXTRA_CATEGORY = "category"

        fun newInstance(category: String): PostsListFragment {
            val bundle = Bundle()
            bundle.putString(EXTRA_CATEGORY, category)

            val fragment = PostsListFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}