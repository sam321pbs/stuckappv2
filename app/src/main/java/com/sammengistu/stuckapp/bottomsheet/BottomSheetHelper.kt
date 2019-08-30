package com.sammengistu.stuckapp.bottomsheet

import android.content.Context
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sammengistu.stuckapp.DummyDataStuck
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckfirebase.FirestoreHelper
import com.sammengistu.stuckfirebase.constants.POSTS
import com.sammengistu.stuckfirebase.data.Post
import org.jetbrains.anko.find

class BottomSheetHelper(private val context: Context, private val bottomSheetLL: LinearLayout):
    BottomSheetMenu {

    val TAG = BottomSheetHelper::class.java.simpleName

    private var mBottomSheetBehavior: BottomSheetBehavior<LinearLayout> =
        BottomSheetBehavior.from(bottomSheetLL)
    private var mPost: Post? = null

    init {
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        mBottomSheetBehavior.isHideable = true
        addClickListenersToViews()
    }

    override fun showMenu(post: Post) {
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
            .setOnClickListener { favoritePost() }
        bottomSheetLL.find<TextView>(R.id.menu_view_comments)
            .setOnClickListener { showComments() }
        bottomSheetLL.find<TextView>(R.id.menu_report)
            .setOnClickListener { reportPost() }
    }

    private fun reportPost() {
        TODO("report post")
    }

    private fun showComments() {
        TODO("show comments")
    }

    private fun favoritePost() {
        TODO("favorite post")
    }

    private fun deletePost() {
        // Todo: Change to handle server post vs db post/ also check that it is users posts before deleting
        if (mPost != null && DummyDataStuck.ownerId == mPost!!.ownerId) {
            FirestoreHelper.deleteItem(POSTS, mPost!!.ref)
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