package com.sammengistu.stuckapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.sammengistu.stuckapp.AssetImageUtils
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.adapters.PostsAdapter
import com.sammengistu.stuckapp.events.AssetsLoadedEvent
import com.sammengistu.stuckapp.events.DataChangedEvent
import com.sammengistu.stuckapp.events.DeletedPostEvent
import com.sammengistu.stuckfirebase.ErrorNotifier
import com.sammengistu.stuckfirebase.constants.LOAD_TYPE_CATEGORIES
import com.sammengistu.stuckfirebase.constants.LOAD_TYPE_DRAFT
import com.sammengistu.stuckfirebase.constants.LOAD_TYPE_FAVORITE
import com.sammengistu.stuckfirebase.constants.LOAD_TYPE_USER
import com.sammengistu.stuckfirebase.database.InjectorUtils
import com.sammengistu.stuckfirebase.events.IncreaseCommentCountEvent
import com.sammengistu.stuckfirebase.models.DraftPostModel
import com.sammengistu.stuckfirebase.models.PostModel
import com.sammengistu.stuckfirebase.repositories.UserRepository
import com.sammengistu.stuckfirebase.utils.DateUtils
import com.sammengistu.stuckfirebase.viewmodels.DraftViewModel
import com.sammengistu.stuckfirebase.viewmodels.PostListViewModel
import kotlinx.android.synthetic.main.fragment_post_list.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

abstract class PostsListFragment : BaseFragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: PostsAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var swipeToRefreshLayout: SwipeRefreshLayout
    private var lastCreatedAt: Any = 0L
    private var addItems = false

    private val listViewModel: PostListViewModel by viewModels {
        InjectorUtils.providePostListFactory(requireContext(), getLoadType())
    }

    private val draftListViewModel: DraftViewModel by viewModels {
        InjectorUtils.provideDraftPostListFactory(requireContext())
    }

    abstract fun getLoadType(): String
    abstract fun getEmptyMessage(): String

    @Subscribe
    fun onDataChanged(event: DataChangedEvent) {
        onDataUpdated()
        Log.d(TAG, "On data changed")
    }

    @Subscribe
    fun onAssetsLoaded(event: AssetsLoadedEvent) = refreshAdapter()

    @Subscribe
    fun onPostDeleted(event: DeletedPostEvent) {
        viewAdapter.removePost(event.ref)
    }

    @Subscribe
    fun onIncreaseCommentCount(event: IncreaseCommentCountEvent) {
        viewAdapter.updateCommentCountOnPost(event.postRef)
    }

    override fun getLayoutId() = R.layout.fragment_post_list

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSwipeToRefresh()
        showEmptyMessage(true)
        EventBus.getDefault().register(this)
        if (AssetImageUtils.isLoaded) {
            refreshAdapter()
        }

        if (getLoadType() == LOAD_TYPE_DRAFT) {
            draftListViewModel.draftsViewModel.observe(viewLifecycleOwner) { posts ->
                if (posts == null) {
                    ErrorNotifier.notifyError(activity, TAG, "Error retrieving posts")
                } else {
                    updateAdapter(convertDraftToPost(posts), addItems)
                }
            }
        } else {
            listViewModel.postsViewModel.observe(viewLifecycleOwner) { posts ->
                if (posts == null) {
                    ErrorNotifier.notifyError(activity, TAG, "Error retrieving posts")
                } else {
                    updateAdapter(posts, addItems)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }

    private fun onDataUpdated() {
        viewAdapter.notifyDataSetChanged()
    }

    /**
     * Override me
     */
    open fun getPostCategory(): String = ""

    private fun setupSwipeToRefresh() {
        swipeToRefreshLayout = swipe_to_refresh
        swipeToRefreshLayout.setOnRefreshListener {
            swipeToRefreshLayout.isRefreshing = true
            refreshAdapter()
        }
    }

    private fun setupRecyclerView() {
        val viewMode = when {
            getLoadType() == LOAD_TYPE_DRAFT -> PostsAdapter.VIEW_MODE_DRAFTS
            getLoadType() == LOAD_TYPE_FAVORITE -> PostsAdapter.VIEW_MODE_FAVORITES
            else -> PostsAdapter.VIEW_MODE_NORMAL
        }

        viewManager = LinearLayoutManager(this.context)
        viewAdapter = PostsAdapter(this.context!!, findNavController(), viewMode)
        recyclerView = recycler_view.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        val scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && viewAdapter.itemCount > 30) {
                    loadMoreItems()
                }
            }
        }
        recyclerView.addOnScrollListener(scrollListener)
    }

    private fun loadMoreItems() {
        addItems = true
        Log.d(TAG, "Loading more items")
        swipeToRefreshLayout.isRefreshing = true
        UserRepository.getUserInstance(context!!) { user ->
            if (user != null) {
                when (getLoadType()) {
                    LOAD_TYPE_FAVORITE -> listViewModel.setData(user.ref, lastCreatedAt)
                    LOAD_TYPE_CATEGORIES -> listViewModel.setData(getPostCategory(), lastCreatedAt)
                    LOAD_TYPE_USER -> listViewModel.setData(user.ref, lastCreatedAt)
                    LOAD_TYPE_DRAFT -> { /* do nothnig */ }
                    else -> listViewModel.setData(lastCreatedAt)
                }
            }
        }
    }

    private fun refreshAdapter() {
        addItems = false
        swipeToRefreshLayout.isRefreshing = true
        UserRepository.getUserInstance(context!!) { user ->
            if (user != null) {
                when (getLoadType()) {
                    LOAD_TYPE_FAVORITE -> listViewModel.setData(user.ref)
                    LOAD_TYPE_CATEGORIES -> listViewModel.setData(getPostCategory())
                    LOAD_TYPE_USER -> listViewModel.setData(user.ref)
                    LOAD_TYPE_DRAFT -> { draftListViewModel.setData(user.ref) }
                    else -> listViewModel.setData()
                }
            }
        }
    }

    private fun convertDraftToPost(draftList: List<DraftPostModel>): List<PostModel> {
        val list = ArrayList<PostModel>()
        for (draft in draftList) {
            val post = PostModel(draft)
            post.draftId = draft._id
            list.add(post)
        }
        return list
    }

    private fun updateAdapter(
        posts: List<PostModel>,
        addItems: Boolean
    ) {
        if (posts.isNotEmpty()) {
            lastCreatedAt = posts[posts.size - 1].createdAt ?: DateUtils.getMaxDate()
        }

        viewAdapter.swapData(posts, addItems)
        swipeToRefreshLayout.isRefreshing = false
        showEmptyMessage(viewAdapter.isEmpty())
    }

    private fun showEmptyMessage(isEmpty: Boolean) {
        val emptyMessageView = empty_list_message
        if (emptyMessageView != null) {
            if (isEmpty) {
                emptyMessageView.visibility = View.VISIBLE
                emptyMessageView.setText(getEmptyMessage())
            } else {
                emptyMessageView.visibility = View.GONE
            }
        }
    }

    companion object {
        private const val TAG: String = "PostsListFragment"
    }
}