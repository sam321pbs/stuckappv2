package com.sammengistu.stuckapp.bottomsheet

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sammengistu.stuckapp.ErrorNotifier
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.UserHelper
import com.sammengistu.stuckapp.activities.CommentsActivity
import com.sammengistu.stuckapp.adapters.NotifyAdapter
import com.sammengistu.stuckfirebase.access.FirebaseItemAccess
import com.sammengistu.stuckfirebase.access.PostAccess
import com.sammengistu.stuckfirebase.access.StarPostAccess
import com.sammengistu.stuckfirebase.access.UserAccess
import com.sammengistu.stuckfirebase.data.PostModel
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
    private var mPost: PostModel? = null

    init {
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        mBottomSheetBehavior.isHideable = true
        addClickListenersToViews()
    }

    override fun showMenu(post: PostModel) {
        mPost = post
        updateBottomSheet()
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun hideMenu() {
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun updateBottomSheet() {
        if (mPost != null && mPost!!.ref.isBlank()) {
            bottomSheetLL.find<LinearLayout>(R.id.bottom_favorite_container)
                .visibility = View.GONE
            bottomSheetLL.find<LinearLayout>(R.id.bottom_comments_container)
                .visibility = View.GONE
            bottomSheetLL.find<LinearLayout>(R.id.bottom_report_container)
                .visibility = View.GONE
            bottomSheetLL.find<TextView>(R.id.bottom_sheet_title)
                .text = "Draft Options"
        } else {
            bottomSheetLL.find<LinearLayout>(R.id.bottom_favorite_container)
                .visibility = View.VISIBLE
            bottomSheetLL.find<LinearLayout>(R.id.bottom_comments_container)
                .visibility = View.VISIBLE
            bottomSheetLL.find<LinearLayout>(R.id.bottom_report_container)
                .visibility = View.VISIBLE
            bottomSheetLL.find<TextView>(R.id.bottom_sheet_title)
                .text = "Post Options"
        }

        if (mPost != null && (mPost!!.ownerId == userId || mPost!!.ref.isBlank())) {
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
        CommentsActivity.startActivity(context, mPost!!.ref, 0)
    }

    private fun starPost() {
        if (mPost != null) {
            UserHelper.getCurrentUser { user ->
                if (user != null) {
                    StarPostAccess(user.ref).createItemInFB(mPost!!,
                        object : FirebaseItemAccess.OnItemCreated<PostModel> {
                            override fun onSuccess(item: PostModel) {
                                UserAccess().incrementTotalStars(item.ownerRef)
                            }

                            override fun onFailed(e: Exception) {
                                ErrorNotifier.notifyError(context, TAG, "Error liking post", e)
                            }
                        })
                    mPost!!.totalStars = mPost!!.totalStars + 1
                    notifyAdapter.onDataUpdated()
                    hideMenu()
                } else {
                    ErrorNotifier.notifyError(context, TAG, "Error liking post")
                }
            }
        }
    }

    private fun unstarPost() {
        TODO("Implement this")
    }

    private fun deletePost() {
        // Todo: Change to handle server post vs db post/ also check that it is users posts before deleting
        val userId = UserHelper.getFirebaseUserId()
        if (mPost != null && (mPost!!.ownerId == userId || mPost!!.ref.isBlank())) {
            val builder: AlertDialog.Builder = context.let { AlertDialog.Builder(it) }
            builder
                .setMessage("Are you sure you want to delete this post?")
                .setTitle("Delete Post")
                .setPositiveButton("Delete") { _, _ ->
                    if (mPost!!.ref.isNotBlank()) {
                        PostAccess().deleteItemInFb(mPost!!.ref,
                            object : FirebaseItemAccess.OnItemDeleted {
                                override fun onSuccess() {
                                    //Todo: hide post that was deleted
                                    Toast.makeText(context, "Post deleted", Toast.LENGTH_SHORT).show()
                                }

                                override fun onFailed(e: Exception) {
                                    ErrorNotifier.notifyError(context, "Delete Failed", TAG, e)
                                }

                            })
                    } else {
                        deleteDraft(mPost!!)
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