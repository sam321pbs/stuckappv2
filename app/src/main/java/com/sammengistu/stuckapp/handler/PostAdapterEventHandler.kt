package com.sammengistu.stuckapp.handler

import android.content.Context
import android.view.View
import androidx.navigation.NavController
import com.sammengistu.stuckapp.activities.BaseActivity
import com.sammengistu.stuckapp.events.ChangeBottomSheetStateEvent
import com.sammengistu.stuckapp.fragments.HomeListFragmentDirections
import com.sammengistu.stuckapp.fragments.ProfileViewFragment
import com.sammengistu.stuckapp.helpers.HiddenItemsHelper
import com.sammengistu.stuckfirebase.access.HiddenItemsAccess
import com.sammengistu.stuckfirebase.models.PostModel
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.doAsync

class PostAdapterEventHandler(private val context: Context,
                              private val navController: NavController,
                              private val post: PostModel) {

    fun showProfile(view: View) {
        if (context is BaseActivity) {
            context.addFragment(ProfileViewFragment.newInstance(post.ownerRef))
        }
    }

    fun showPostMenu(view: View) {
        EventBus.getDefault().post(ChangeBottomSheetStateEvent(true, post))
    }

    fun showComments(view: View) {
        val action = HomeListFragmentDirections
            .actionNavToCommentsFragment(post.ref, 0)
        navController.navigate(action)
    }

    fun showPost(view: View) {
        doAsync {
            val itemId = HiddenItemsHelper.getItem(post.ref)?._id
            if (itemId != null)
                HiddenItemsAccess(context).deleteItem(itemId)
        }
    }
}