package com.sammengistu.stuckapp.bottomsheet

import android.content.Context
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.activities.MainActivity
import com.sammengistu.stuckapp.collections.UserStarredCollection
import com.sammengistu.stuckapp.constants.ReportReasons
import com.sammengistu.stuckapp.events.DataChangedEvent
import com.sammengistu.stuckapp.events.DeletedPostEvent
import com.sammengistu.stuckapp.fragments.HomeListFragmentDirections
import com.sammengistu.stuckapp.helpers.HiddenItemsHelper
import com.sammengistu.stuckfirebase.ErrorNotifier
import com.sammengistu.stuckfirebase.access.*
import com.sammengistu.stuckfirebase.models.*
import com.sammengistu.stuckfirebase.models.HiddenItemModel.Companion.TYPE_POST
import com.sammengistu.stuckfirebase.repositories.UserRepository
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.find
import org.jetbrains.anko.uiThread


class BottomSheetHelper(
    private val context: Context,
    private val bottomSheetLL: LinearLayout
) {

    val TAG = BottomSheetHelper::class.java.simpleName

    private var mBottomSheetBehavior: BottomSheetBehavior<LinearLayout> =
        BottomSheetBehavior.from(bottomSheetLL)

    // Empty post
    private var post: PostModel = PostModel()

    init {
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        mBottomSheetBehavior.isHideable = true
        addClickListenersToViews()
    }

    fun showMenu(post: PostModel) {
        this.post = post
        updateBottomSheet()
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    fun hideMenu() {
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun updateBottomSheet() {
        if (post.ref.isBlank()) {
            // Handle Draft Post
            bottomSheetLL.find<LinearLayout>(R.id.bottom_favorite_container)
                .visibility = View.GONE
            bottomSheetLL.find<LinearLayout>(R.id.bottom_comments_container)
                .visibility = View.GONE
            bottomSheetLL.find<LinearLayout>(R.id.bottom_report_container)
                .visibility = View.GONE
            bottomSheetLL.find<LinearLayout>(R.id.bottom_hide_container)
                .visibility = View.GONE
            bottomSheetLL.find<TextView>(R.id.bottom_sheet_title)
                .text = "Draft Options"
        } else {
            // Regular post
            bottomSheetLL.find<LinearLayout>(R.id.bottom_favorite_container)
                .visibility = View.VISIBLE
            bottomSheetLL.find<LinearLayout>(R.id.bottom_comments_container)
                .visibility = View.VISIBLE
            bottomSheetLL.find<LinearLayout>(R.id.bottom_report_container)
                .visibility = View.VISIBLE
            bottomSheetLL.find<LinearLayout>(R.id.bottom_hide_container)
                .visibility = View.VISIBLE
            bottomSheetLL.find<TextView>(R.id.bottom_sheet_title)
                .text = "Post Options"


            if (HiddenItemsHelper.containesRef(post.ref)) {
                bottomSheetLL.find<TextView>(R.id.menu_hide).text = "Show"
            } else {
                bottomSheetLL.find<TextView>(R.id.menu_hide).text = "Hide"
            }


            val userStar = UserStarredCollection.getInstance(context).getStarPost(post)
            if (userStar == null) {
                bottomSheetLL.find<TextView>(R.id.menu_favorite).text = "Favorite"
            } else {
                bottomSheetLL.find<TextView>(R.id.menu_favorite).text = "Remove from Favorites"
            }
        }

        UserRepository.getUserInstance(context) { user ->
            if (user != null) {
                // Handle delete option
                if ((post.ownerRef == user.ref || post.ref.isBlank())) {
                    bottomSheetLL.find<LinearLayout>(R.id.bottom_delete_container)
                        .visibility = View.VISIBLE
                } else {
                    bottomSheetLL.find<LinearLayout>(R.id.bottom_delete_container)
                        .visibility = View.GONE
                }
            }
        }
    }

    private fun addClickListenersToViews() {
        bottomSheetLL.find<ImageView>(R.id.close_btn)
            .setOnClickListener { hideMenu() }
        bottomSheetLL.find<TextView>(R.id.menu_delete_post)
            .setOnClickListener { deletePost() }
        bottomSheetLL.find<TextView>(R.id.menu_favorite)
            .setOnClickListener { starPost() }
        bottomSheetLL.find<TextView>(R.id.menu_comments)
            .setOnClickListener { showComments() }
        bottomSheetLL.find<TextView>(R.id.menu_report)
            .setOnClickListener { reportPost() }
        bottomSheetLL.find<TextView>(R.id.menu_hide)
            .setOnClickListener { hidePost() }
    }

    private fun hidePost() {
        if (HiddenItemsHelper.containesRef(post.ref)) {
            doAsync {
                val itemId = HiddenItemsHelper.getItem(post.ref)?._id
                if (itemId != null)
                    HiddenItemsAccess(
                        context
                    ).deleteItem(itemId)
            }
        } else {
            createHiddenItem()
        }
        hideMenu()
    }

    private fun createHiddenItem() {
        doAsync {
            UserRepository.getUserInstance(context) { user ->
                if (user != null) {
                    val item =
                        HiddenItemModel(
                            user.ref,
                            post.ref,
                            TYPE_POST
                        )
                    HiddenItemsAccess(
                        context
                    ).insertItem(item)
                }
            }
        }
    }

    private fun reportPost() {
        val arrayAdapter =
            ArrayAdapter<String>(context, android.R.layout.select_dialog_singlechoice)
        arrayAdapter.addAll(ReportReasons.getDisplayNames())

        AlertDialog.Builder(context)
            .setTitle("Reason for reporting")
            .setAdapter(arrayAdapter) { _, pos ->
                val reason = arrayAdapter.getItem(pos)
                UserRepository.getUserInstance(context) { user ->
                    if (user != null && reason != null) {
                        ReportAccess().createItemInFB(
                            ReportModel(reason, post.ref, user.ref)
                        )
                        createHiddenItem()
                        EventBus.getDefault().post(DataChangedEvent())
                        Toast.makeText(context, "Post has been reported", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
        hideMenu()
    }

    private fun showComments() {
        hideMenu()
        if (context is MainActivity) {
            val action = HomeListFragmentDirections.actionNavToCommentsFragment(post.ref, 0)
            context.nav_host_fragment.findNavController().navigate(action)
        }
    }

    private fun starPost() {
        UserRepository.getUserInstance(context) { user ->
            if (user != null) {
                val userStar = UserStarredCollection.getInstance(context).getStarPost(post)
                if (userStar == null) {
                    addPostToFavorites(user)
                } else {
                    removePostFromFavorites(userStar.ref)
                }
                hideMenu()
            } else {
                ErrorNotifier.notifyError(context, TAG, "Error liking post")
            }
        }
    }

    private fun addPostToFavorites(user: UserModel) {
        val starPost = StarPostModel(user.ref, post)
        StarPostAccess().createItemInFB(starPost,
            object : FirebaseItemAccess.OnItemCreated<StarPostModel> {
                override fun onSuccess(item: StarPostModel) {
                    UserStarredCollection.getInstance(context).addStarPostToMap(item)
                    post.totalStars = post.totalStars + 1
                    EventBus.getDefault().post(DataChangedEvent())
                    Toast.makeText(context, "Added to favorites!", Toast.LENGTH_SHORT).show()
                }

                override fun onFailed(e: Exception) {
                    ErrorNotifier.notifyError(context, TAG, "Error adding to favorites", e)
                }
            })
    }

    private fun removePostFromFavorites(starRef: String) {
        if (starRef.isBlank()) {
            return
        }
        StarPostAccess().deleteItemInFb(starRef,
            object : FirebaseItemAccess.OnItemDeleted {
                override fun onSuccess() {
                    UserStarredCollection.getInstance(context).removeStarPostFromMap(post)
                    post.totalStars = post.totalStars - 1
                    EventBus.getDefault().post(DataChangedEvent())
                    Toast.makeText(context, "Removed from favorites!", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onFailed(e: Exception) {
                    ErrorNotifier.notifyError(context, TAG, "Error removing from favorites", e)
                }
            })
    }

    private fun deletePost() {
        // check that it is users posts before deleting
        UserRepository.getUserInstance(context) {
            if (isUsersPost(it, post)) {
                val builder: AlertDialog.Builder = context.let { AlertDialog.Builder(it) }
                builder
                    .setMessage("Are you sure you want to delete this post?")
                    .setTitle("Delete Post")
                    .setPositiveButton("Delete") { _, _ ->
                        handleDeletePost(post.ref)
                        hideMenu()
                    }
                    ?.setNegativeButton("Cancel", null)
                builder.show()
            }
            hideMenu()
        }
    }

    private fun isUsersPost(user: UserModel?, post: PostModel) =
        user != null && post.ownerRef == user.ref

    private fun handleDeletePost(postRef: String) {
        if (postRef.isNotBlank()) {
            PostAccess().deleteItemInFb(postRef,
                object : FirebaseItemAccess.OnItemDeleted {
                    override fun onSuccess() {
                        EventBus.getDefault().post(DeletedPostEvent(postRef))
                        Toast.makeText(context, "Post deleted", Toast.LENGTH_SHORT).show()
                    }

                    override fun onFailed(e: Exception) {
                        ErrorNotifier.notifyError(context, "Delete Failed", TAG, e)
                    }
                })
        } else {
            deleteDraft(post)
        }
    }

    private fun deleteDraft(post: PostModel) {
        doAsync {
            try {
                val draftId = post._id
                if (draftId != null) {
                    DraftPostAccess(context).deletePost(draftId)
                }
                uiThread { Toast.makeText(context, "Post deleted", Toast.LENGTH_SHORT).show() }
            } catch (e: Exception) {
                ErrorNotifier.notifyError(context, TAG, "Error deleting post", e)
            }
        }
    }
}