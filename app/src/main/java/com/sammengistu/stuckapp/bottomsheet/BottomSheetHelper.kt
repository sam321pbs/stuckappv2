package com.sammengistu.stuckapp.bottomsheet

import android.content.Context
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.activities.CommentsActivity
import com.sammengistu.stuckapp.collections.UserStarredCollection
import com.sammengistu.stuckapp.constants.ReportReasons
import com.sammengistu.stuckapp.events.DataChangedEvent
import com.sammengistu.stuckapp.events.DeletedPostEvent
import com.sammengistu.stuckapp.helpers.HiddenItemsHelper
import com.sammengistu.stuckfirebase.ErrorNotifier
import com.sammengistu.stuckfirebase.UserHelper
import com.sammengistu.stuckfirebase.access.FirebaseItemAccess
import com.sammengistu.stuckfirebase.access.PostAccess
import com.sammengistu.stuckfirebase.access.ReportAccess
import com.sammengistu.stuckfirebase.access.StarPostAccess
import com.sammengistu.stuckfirebase.database.HiddenItemModel
import com.sammengistu.stuckfirebase.database.HiddenItemModel.Companion.TYPE_POST
import com.sammengistu.stuckfirebase.database.access.DraftPostAccess
import com.sammengistu.stuckfirebase.database.access.HiddenItemsAccess
import com.sammengistu.stuckfirebase.models.PostModel
import com.sammengistu.stuckfirebase.models.ReportModel
import com.sammengistu.stuckfirebase.models.StarPostModel
import com.sammengistu.stuckfirebase.models.UserModel
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
    private var post: PostModel? = null

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
        if (post != null && post!!.ref.isBlank()) {
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

            if (post != null) {
                if (HiddenItemsHelper.containesRef(post!!.ref)) {
                    bottomSheetLL.find<TextView>(R.id.menu_hide).text = "Show"
                } else {
                    bottomSheetLL.find<TextView>(R.id.menu_hide).text = "Hide"
                }
            }

            if (post != null) {
                val userStar = UserStarredCollection.getStarPost(post!!)
                if (userStar == null) {
                    bottomSheetLL.find<TextView>(R.id.menu_favorite).text = "Favorite"
                } else {
                    bottomSheetLL.find<TextView>(R.id.menu_favorite).text = "Remove from Favorites"
                }
            }
        }

        UserHelper.getCurrentUser { user ->
            if (user != null) {
                // Handle delete option
                if (post != null && (post!!.ownerId == user.userId || post!!.ref.isBlank())) {
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
        if (post != null) {
            if (HiddenItemsHelper.containesRef(post!!.ref)) {
                doAsync {
                    val itemId = HiddenItemsHelper.getItem(post!!.ref)?._id
                    if (itemId != null)
                        HiddenItemsAccess(context).deleteItem(itemId)
                }
            } else {
                createHiddenItem()
            }
        }
        hideMenu()
    }

    private fun createHiddenItem() {
        doAsync {
            UserHelper.getCurrentUser { user ->
                if (user != null) {
                    val item = HiddenItemModel(user.userId, user.ref, post!!.ref, TYPE_POST)
                    HiddenItemsAccess(context).insertItem(item)
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
                UserHelper.getCurrentUser { user ->
                    if (user != null && reason != null && post != null) {
                        ReportAccess().createItemInFB(
                            ReportModel(reason, post!!.ref, user.ref, user.userId)
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
        CommentsActivity.startActivity(context, post!!.ref, post!!.ownerId, post!!.ownerRef, 0)
    }

    private fun starPost() {
        if (post != null) {
            UserHelper.getCurrentUser { user ->
                if (user != null) {
                    val userStar = UserStarredCollection.getStarPost(post!!)
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
    }

    private fun addPostToFavorites(user: UserModel) {
        val starPost = StarPostModel(user.ref, user.userId, post!!)
        StarPostAccess().createItemInFB(starPost,
            object : FirebaseItemAccess.OnItemCreated<StarPostModel> {
                override fun onSuccess(item: StarPostModel) {
                    if (post != null) {
                        UserStarredCollection.addStarPostToMap(item)
                        post!!.totalStars = post!!.totalStars + 1
                        EventBus.getDefault().post(DataChangedEvent())
                        Toast.makeText(context, "Added to favorites!", Toast.LENGTH_SHORT).show()
                    }
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
                    if (post != null) {
                        UserStarredCollection.removeStarPostFromMap(post!!)
                        post!!.totalStars = post!!.totalStars - 1
                        EventBus.getDefault().post(DataChangedEvent())
                        Toast.makeText(context, "Removed from favorites!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailed(e: Exception) {
                    ErrorNotifier.notifyError(context, TAG, "Error removing from favorites", e)
                }
            })
    }

    private fun deletePost() {
        // check that it is users posts before deleting
        val userId = UserHelper.getFirebaseUserId()
        if (post != null && (post!!.ownerId == userId || post!!.ref.isBlank())) {
            val builder: AlertDialog.Builder = context.let { AlertDialog.Builder(it) }
            builder
                .setMessage("Are you sure you want to delete this post?")
                .setTitle("Delete Post")
                .setPositiveButton("Delete") { _, _ ->
                    handleDeletePost()
                    hideMenu()
                }
                ?.setNegativeButton("Cancel", null)
            builder.show()
        }
        hideMenu()
    }

    private fun handleDeletePost() {
        if (post!!.ref.isNotBlank()) {
            PostAccess().deleteItemInFb(post!!.ref,
                object : FirebaseItemAccess.OnItemDeleted {
                    override fun onSuccess() {
                        EventBus.getDefault().post(DeletedPostEvent(post!!.ref))
                        Toast.makeText(context, "Post deleted", Toast.LENGTH_SHORT)
                            .show()
                    }

                    override fun onFailed(e: Exception) {
                        ErrorNotifier.notifyError(context, "Delete Failed", TAG, e)
                    }
                })
        } else {
            deleteDraft(post!!)
        }
    }

    private fun deleteDraft(post: PostModel) {
        doAsync {
            try {
                DraftPostAccess(context).deletePost(post.draftId)
                uiThread { Toast.makeText(context, "Post deleted", Toast.LENGTH_SHORT).show() }
            } catch (e: Exception) {
                ErrorNotifier.notifyError(context, TAG, "Error deleting post", e)
            }
        }
    }
}