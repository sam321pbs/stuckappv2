package com.sammengistu.stuckapp.handler

import android.content.Context
import android.view.View
import com.sammengistu.stuckapp.activities.BaseActivity
import com.sammengistu.stuckapp.activities.CommentsActivity
import com.sammengistu.stuckapp.events.ChangeBottomSheetStateEvent
import com.sammengistu.stuckapp.fragments.ProfileViewFragment
import com.sammengistu.stuckapp.helpers.HiddenItemsHelper
import com.sammengistu.stuckfirebase.access.HiddenItemsAccess
import com.sammengistu.stuckfirebase.models.PostModel
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.doAsync

class PostAdapterEventHandler(private val context: Context, private val post: PostModel) {
    fun showProfile(view: View) {
        if (context is BaseActivity) {
            context.addFragment(ProfileViewFragment.newInstance(post.ownerId))
        }
    }

    fun showPostMenu(view: View) {
        EventBus.getDefault().post(ChangeBottomSheetStateEvent(true, post))
    }

    fun showComments(view: View) {
        CommentsActivity.startActivity(context, post.ref, post.ownerId, post.ownerRef, 0)
    }

    fun showPost(view: View) {
        doAsync {
            val itemId = HiddenItemsHelper.getItem(post.ref)?._id
            if (itemId != null)
                HiddenItemsAccess(context).deleteItem(itemId)
        }
    }
}