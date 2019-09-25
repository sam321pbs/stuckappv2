package com.sammengistu.stuckapp.fragments

import android.os.Bundle
import android.view.View
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.helpers.UserPrefHelper
import com.sammengistu.stuckfirebase.UserHelper
import kotlinx.android.synthetic.main.fragment_notifications.*

class NotificationSettings : BaseFragment() {

    override fun getLayoutId(): Int = R.layout.fragment_notifications

    override fun getFragmentTag(): String = TAG

    override fun getFragmentTitle(): String = TITLE

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        UserHelper.getCurrentUser { user ->
            if (user != null) {
                notifications_votes_switch.isChecked = UserPrefHelper.getVotesPref(activity!!, user)
                notifications_comments_switch.isChecked = UserPrefHelper.getCommentsPref(activity!!, user)
            }
        }

        notifications_votes_switch.setOnCheckedChangeListener{ _, isChecked ->
            UserPrefHelper.addVotesPref(activity!!, isChecked)
        }

        notifications_comments_switch.setOnCheckedChangeListener{ _, isChecked ->
            UserPrefHelper.addCommentsPref(activity!!, isChecked)
        }
    }

    companion object {
        val TAG = NotificationSettings::class.java.simpleName
        const val TITLE = "Notifications"
    }
}