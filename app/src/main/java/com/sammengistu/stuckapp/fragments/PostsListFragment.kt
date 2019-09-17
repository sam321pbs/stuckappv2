package com.sammengistu.stuckapp.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.sammengistu.stuckapp.AssetImageUtils
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.UserHelper
import com.sammengistu.stuckapp.adapters.PostsAdapter
import com.sammengistu.stuckapp.data.DraftPost
import com.sammengistu.stuckapp.events.AssetsLoadedEvent
import com.sammengistu.stuckapp.events.UserStarsLoadedEvent
import com.sammengistu.stuckapp.events.UserVotesLoadedEvent
import com.sammengistu.stuckapp.utils.InjectorUtils
import com.sammengistu.stuckfirebase.access.FirebaseItemAccess
import com.sammengistu.stuckfirebase.access.PostAccess
import com.sammengistu.stuckfirebase.access.StarPostAccess
import com.sammengistu.stuckfirebase.data.PostModel
import com.sammengistu.stuckfirebase.data.StarPost
import com.sammengistu.stuckfirebase.events.IncreaseCommentCountEvent
import com.sammengistu.stuckfirebase.viewmodels.PostListViewModel
import kotlinx.android.synthetic.main.fragment_post_list.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

abstract class PostsListFragment : BasePostListsFragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: PostsAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var swipeToRefreshLayout: SwipeRefreshLayout
    private var postsList = ArrayList<PostModel>()

    private val listViewModel: PostListViewModel by viewModels {
        InjectorUtils.providePostListViewModelFactory(requireContext())
    }

    abstract fun getType(): String
    abstract fun getEmptyMessage(): String

    @Subscribe
    fun onUserVotesLoaded(event: UserVotesLoadedEvent) {
        onDataUpdated()
    }

    @Subscribe
    fun onUserStarsLoaded(event: UserStarsLoadedEvent) {
        onDataUpdated()
    }

    @Subscribe
    fun onAssetsLoaded(event: AssetsLoadedEvent) {
        refreshAdapter(viewAdapter)
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

    override fun getLayoutId(): Int {
        return R.layout.fragment_post_list
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSwipeToRefresh()
        showEmptyMessage(true)
        hideMenu()
        EventBus.getDefault().register(this)
        if (AssetImageUtils.isLoaded) {
            refreshAdapter(viewAdapter)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }

    override fun onDataUpdated() {
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
        viewManager = LinearLayoutManager(this.context)
        viewAdapter = PostsAdapter(this.context!!, getType() == TYPE_DRAFT, mBottomSheetMenu)
        recyclerView = recycler_view.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    private fun refreshAdapter(adapter: PostsAdapter) {
        swipeToRefreshLayout.isRefreshing = true
        UserHelper.getCurrentUser { user ->
            if (user != null) {
                when (getType()) {
                    TYPE_FAVORITE -> StarPostAccess(user.ref).getItems(
                        getOnStarPostRetrievedListener(adapter)
                    )
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
                            adapter.swapData(convertDraftToPost(draftList))
                            swipeToRefreshLayout.isRefreshing = false
                        }
                    }
                    else -> PostAccess().getRecentPosts(getOnPostRetrievedListener(adapter))
                }
            }
        }
    }

    private fun convertDraftToPost(draftList: List<DraftPost>): List<PostModel> {
        val list = ArrayList<PostModel>()
        for (draft in draftList) {
            val post = PostModel(draft)
            post.draftId = draft.postId
            list.add(post)
        }
        return list
    }

    private fun getOnPostRetrievedListener(adapter: PostsAdapter): FirebaseItemAccess.OnItemRetrieved<PostModel> {
        return object :
            FirebaseItemAccess.OnItemRetrieved<PostModel> {
            override fun onSuccess(list: List<PostModel>) {
                updateAdapter(list, adapter)
            }

            override fun onFailed(e: Exception) {
                Toast.makeText(activity!!, "Failed to get posts", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getOnStarPostRetrievedListener(adapter: PostsAdapter):
            FirebaseItemAccess.OnItemRetrieved<StarPost> {
        return object :
            FirebaseItemAccess.OnItemRetrieved<StarPost> {
            override fun onSuccess(list: List<StarPost>) {
                updateAdapter(list, adapter)
            }

            override fun onFailed(e: Exception) {
                Toast.makeText(activity!!, "Failed to get posts", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateAdapter(
        list: List<PostModel>,
        adapter: PostsAdapter
    ) {
        postsList = list as ArrayList<PostModel>
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