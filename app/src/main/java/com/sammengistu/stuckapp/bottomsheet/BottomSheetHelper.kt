package com.sammengistu.stuckapp.bottomsheet

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.activities.CommentsActivity
import com.sammengistu.stuckapp.adapters.NotifyAdapter
import com.sammengistu.stuckapp.collections.UserStarredCollection
import com.sammengistu.stuckfirebase.ErrorNotifier
import com.sammengistu.stuckfirebase.UserHelper
import com.sammengistu.stuckfirebase.access.FirebaseItemAccess
import com.sammengistu.stuckfirebase.access.PostAccess
import com.sammengistu.stuckfirebase.access.StarPostAccess
import com.sammengistu.stuckfirebase.access.UserAccess
import com.sammengistu.stuckfirebase.data.PostModel
import com.sammengistu.stuckfirebase.data.StarPostModel
import com.sammengistu.stuckfirebase.data.UserModel
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.find
import org.jetbrains.anko.uiThread

class BottomSheetHelper(
    private val context: Context,
    private val userId: String,
    private val bottomSheetLL: LinearLayout,
    private val notifyAdapter: NotifyAdapter
) :
    BottomSheetMenu {

    val TAG = BottomSheetHelper::class.java.simpleName

    private var mBottomSheetBehavior: BottomSheetBehavior<LinearLayout> =
        BottomSheetBehavior.from(bottomSheetLL)
    private var post: PostModel? = null

    init {
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        mBottomSheetBehavior.isHideable = true
        addClickListenersToViews()
    }

    override fun showMenu(post: PostModel) {
        this.post = post
        updateBottomSheet()
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun hideMenu() {
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
            bottomSheetLL.find<TextView>(R.id.bottom_sheet_title)
                .text = "Post Options"

            if (post != null) {
                val userStar = UserStarredCollection.getStarPost(post!!)
                if (userStar == null) {
                    bottomSheetLL.find<TextView>(R.id.menu_favorite).text = "Favorite"
                } else {
                    bottomSheetLL.find<TextView>(R.id.menu_favorite).text = "Remove from Favorites"
                }
            }

            // Todo: hide delete if it is not users post
        }

        if (post != null && (post!!.ownerId == userId || post!!.ref.isBlank())) {
            bottomSheetLL.find<LinearLayout>(R.id.bottom_delete_container)
                .visibility = View.VISIBLE
        } else {
            bottomSheetLL.find<LinearLayout>(R.id.bottom_delete_container)
                .visibility = View.GONE
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
    }

    private fun reportPost() {
        TODO("report post")
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
                        UserAccess().incrementTotalStars(item.ownerRef)
                        UserStarredCollection.addStarPostToMap(item)
                        post!!.totalStars = post!!.totalStars + 1
                        notifyAdapter.onDataUpdated()
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
                        UserAccess().decrementTotalStars(post!!.ownerRef)
                        UserStarredCollection.removeStarPostFromMap(post!!)
                        post!!.totalStars = post!!.totalStars - 1
                        notifyAdapter.onDataUpdated()
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
                    if (post!!.ref.isNotBlank()) {
                        PostAccess().deleteItemInFb(post!!.ref,
                            object : FirebaseItemAccess.OnItemDeleted {
                                override fun onSuccess() {
                                    //Todo: hide post that was deleted
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
                    hideMenu()
                }
                ?.setNegativeButton("Cancel", null)
            builder.show()
        }
        hideMenu()
    }

    private fun deleteDraft(post: PostModel) {
        doAsync {
            try {
                PostAccess.deletePost(context, post.draftId)
                uiThread { Toast.makeText(context, "Post deleted", Toast.LENGTH_SHORT).show() }
            } catch (e: Exception) {
                ErrorNotifier.notifyError(context, TAG, "Error deleting post", e)
            }
        }
    }
}