package com.sammengistu.stuckapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
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
import com.sammengistu.stuckfirebase.UserHelper
import com.sammengistu.stuckfirebase.access.FirebaseItemAccess
import com.sammengistu.stuckfirebase.access.PostAccess
import com.sammengistu.stuckfirebase.access.StarPostAccess
import com.sammengistu.stuckfirebase.database.InjectorUtils
import com.sammengistu.stuckfirebase.database.model.DraftPostModel
import com.sammengistu.stuckfirebase.events.IncreaseCommentCountEvent
import com.sammengistu.stuckfirebase.models.PostModel
import com.sammengistu.stuckfirebase.models.StarPostModel
import com.sammengistu.stuckfirebase.viewmodels.PostListViewModel
import kotlinx.android.synthetic.main.fragment_post_list.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

abstract class PostsListFragment : BaseFragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: PostsAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var swipeToRefreshLayout: SwipeRefreshLayout
    private var postsList = ArrayList<PostModel>()
    private var lastCreatedAt: Any? = 0L

    private val listViewModel: PostListViewModel by viewModels {
        val ownerId = UserHelper.getFirebaseUserId()
        InjectorUtils.provideDraftPostListFactory(requireContext(), ownerId)
    }

    abstract fun getType(): String
    abstract fun getEmptyMessage(): String

    @Subscribe
    fun onDataChanged(event: DataChangedEvent) { onDataUpdated()
        Log.d(TAG, "On data changed")
    }

    @Subscribe
    fun onAssetsLoaded(event: AssetsLoadedEvent) = refreshAdapter(viewAdapter)

    @Subscribe
    fun onPostDeleted(event: DeletedPostEvent) {
        for (post in postsList) {
            if (post.ref == event.ref) {
                postsList.remove(post)
                onDataUpdated()
                break
            }
        }
    }

    @Subscribe
    fun onIncreaseCommentCount(event: IncreaseCommentCountEvent) {
        var refresh = false
        for (post in postsList) {
            if (post.ref == event.postRef) {
                post.totalComments += 1
                refresh = true
                break
            }
        }
        if (refresh) {
            viewAdapter.swapData(postsList)
        }
    }

    override fun getLayoutId() = R.layout.fragment_post_list

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSwipeToRefresh()
        showEmptyMessage(true)
        EventBus.getDefault().register(this)
        if (AssetImageUtils.isLoaded) {
            refreshAdapter(viewAdapter)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }

    fun onDataUpdated() {
        viewAdapter.notifyDataSetChanged()
    }

    fun getPostCategory(): String = arguments?.getString(EXTRA_CATEGORY) ?: ""

    private fun setupSwipeToRefresh() {
        swipeToRefreshLayout = swipe_to_refresh
        swipeToRefreshLayout.setOnRefreshListener {
            swipeToRefreshLayout.isRefreshing = true
            refreshAdapter(viewAdapter)
        }
    }

    private fun setupRecyclerView() {
        val viewMode = when {
            getType() == TYPE_DRAFT -> PostsAdapter.VIEW_MODE_DRAFTS
            getType() == TYPE_FAVORITE -> PostsAdapter.VIEW_MODE_FAVORITES
            else -> PostsAdapter.VIEW_MODE_NORMAL
        }

        viewManager = LinearLayoutManager(this.context)
        viewAdapter = PostsAdapter(this.context!!, viewMode)
        recyclerView = recycler_view.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        val scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && postsList.isNotEmpty()
                    && postsList.size > 30) {
                    loadMoreItems(viewAdapter)
                }
            }
        }
        recyclerView.addOnScrollListener(scrollListener)
    }

    private fun loadMoreItems(adapter: PostsAdapter) {
        Log.d(TAG, "Loading more items")
        swipeToRefreshLayout.isRefreshing = true
        UserHelper.getCurrentUser { user ->
            if (user != null) {
                when (getType()) {
                    TYPE_FAVORITE -> StarPostAccess().getUsersStarredPostsBefore(
                        user.ref, lastCreatedAt,
                        getOnStarPostRetrievedListener(adapter, addItems = true)
                    )
                    TYPE_CATEGORIES -> PostAccess().getPostsInCategory(
                        getPostCategory(),
                        lastCreatedAt,
                        getOnPostRetrievedListener(adapter, addItems = true)
                    )
                    TYPE_USER -> PostAccess().getOwnerPosts(
                        user.userId,
                        lastCreatedAt,
                        getOnPostRetrievedListener(adapter, addItems = true)
                    )
                    TYPE_DRAFT -> swipeToRefreshLayout.isRefreshing = false
                    else -> PostAccess().getRecentPosts(
                        lastCreatedAt,
                        getOnPostRetrievedListener(adapter, addItems = true)
                    )
                }
            }
        }
    }

    private fun refreshAdapter(adapter: PostsAdapter) {
        swipeToRefreshLayout.isRefreshing = true
        UserHelper.getCurrentUser { user ->
            if (user != null) {
                when (getType()) {
                    TYPE_FAVORITE -> {
//                        StarPostAccess().getUsersStarredPosts(
//                            user.ref,
//                            getOnStarPostRetrievedListener(adapter)
//                        )
                        listViewModel.posts.observe(viewLifecycleOwner) { draftList ->
                            updateAdapter(convertDraftToPost(draftList), adapter, false)
                        }
                    }
                    TYPE_CATEGORIES -> PostAccess().getPostsInCategory(
                        getPostCategory(),
                        getOnPostRetrievedListener(adapter)
                    )
                    TYPE_USER -> PostAccess().getOwnerPosts(
                        user.userId,
                        getOnPostRetrievedListener(adapter)
                    )
                    TYPE_DRAFT -> {
                        listViewModel.posts.observe(viewLifecycleOwner) { draftList ->
                            updateAdapter(convertDraftToPost(draftList), adapter, false)
                        }
                    }
                    else -> PostAccess().getRecentPosts(getOnPostRetrievedListener(adapter))
                }
            }
        }
    }

    private fun convertDraftToPost(draftList: List<DraftPostModel>): List<PostModel> {
        val list = ArrayList<PostModel>()
        for (draft in draftList) {
            val post = PostModel(draft)
            post.draftId = draft.postId
            list.add(post)
        }
        return list
    }

    private fun getOnPostRetrievedListener(
        adapter: PostsAdapter,
        addItems: Boolean = false
    ): FirebaseItemAccess.OnItemsRetrieved<PostModel> {
        return object :
            FirebaseItemAccess.OnItemsRetrieved<PostModel> {
            override fun onSuccess(list: List<PostModel>) {
                updateAdapter(list, adapter, addItems)
            }

            override fun onFailed(e: Exception) {
                swipeToRefreshLayout.isRefreshing = false
                ErrorNotifier.notifyError(activity, TAG, "Error retrieving posts", e)
            }
        }
    }

    private fun getOnStarPostRetrievedListener(adapter: PostsAdapter, addItems: Boolean = false):
            FirebaseItemAccess.OnItemsRetrieved<StarPostModel> {
        return object :
            FirebaseItemAccess.OnItemsRetrieved<StarPostModel> {
            override fun onSuccess(list: List<StarPostModel>) {
                updateAdapter(list, adapter, addItems)
            }

            override fun onFailed(e: Exception) {
                swipeToRefreshLayout.isRefreshing = false
                ErrorNotifier.notifyError(activity, TAG, "Error retrieving posts", e)
            }
        }
    }

    private fun updateAdapter(
        list: List<PostModel>,
        adapter: PostsAdapter,
        addItems: Boolean
    ) {
        if (list.isNotEmpty()) {
            lastCreatedAt = list[list.size - 1].createdAt ?: 0L
        }
        if (addItems) {
            postsList.addAll(list)
        } else {
            postsList = list as ArrayList<PostModel>
        }
        adapter.swapData(postsList)
        swipeToRefreshLayout.isRefreshing = false
        showEmptyMessage(postsList.isEmpty())
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
        val TAG: String = PostsListFragment::class.java.simpleName
        const val TYPE_HOME = "home"
        const val TYPE_FAVORITE = "favorite"
        const val TYPE_CATEGORIES = "categories"
        const val TYPE_USER = "user"
        const val TYPE_DRAFT = "draft"
        const val EXTRA_CATEGORY = "extra_category"
    }
}