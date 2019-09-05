package com.sammengistu.stuckapp.bottomsheet

import android.content.Context
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sammengistu.stuckapp.DummyDataStuck
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.activities.CommentsActivity
import com.sammengistu.stuckapp.adapters.NotifyAdapter
import com.sammengistu.stuckapp.data.PostAccess
import com.sammengistu.stuckfirebase.access.StarPostAccess
import com.sammengistu.stuckfirebase.data.PostModel
import org.jetbrains.anko.find

class BottomSheetHelper(private val context: Context,
                        private val bottomSheetLL: LinearLayout,
                        private val notifyAdapter: NotifyAdapter):
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
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun hideMenu() {
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun addClickListenersToViews() {
        bottomSheetLL.find<ImageView>(R.id.close_btn)
            .setOnClickListener { hideMenu() }
        bottomSheetLL.find<TextView>(R.id.menu_delete_post)
            .setOnClickListener { deletePost() }
        bottomSheetLL.find<TextView>(R.id.menu_favorite)
            .setOnClickListener { starPost() }
        bottomSheetLL.find<TextView>(R.id.menu_view_comments)
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
            StarPostAccess(DummyDataStuck.userId).createItemInFB(mPost!!)
            mPost!!.totalStars = mPost!!.totalStars + 1
            notifyAdapter.onDataUpdated()
            hideMenu()
            // todo: call firebase cloud function
        }
    }

    private fun deletePost() {
        // Todo: Change to handle server post vs db post/ also check that it is users posts before deleting
        if (mPost != null && DummyDataStuck.userId == mPost!!.ownerId) {
            PostAccess().deleteItemInFb(mPost!!.ref)
        }
//        if (mPost != null) {
//            val postCopy = mPost!!.copy()
//            val builder: AlertDialog.Builder? = context.let { AlertDialog.Builder(it) }
//
//            builder
//                ?.setMessage("Are you sure you want to delete this post?")
//                ?.setTitle("Delete DraftPost")
//                ?.setPositiveButton("Delete") { _, _ ->
//                    doAsync {
//                        try {
//                            PostAccess.deletePost(context, postCopy)
//                        } catch (e: Exception) {
//                            Log.e(TAG, "Error deleting post", e)
//                        }
//                    }
//                }
//                ?.setNegativeButton("Cancel", null)
//            builder?.show()
//        }
        hideMenu()
    }
}