package com.sammengistu.stuckapp.fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.views.AvatarView
import com.sammengistu.stuckapp.views.DisplayFormItemView
import com.sammengistu.stuckapp.views.StatCardView
import com.sammengistu.stuckfirebase.ErrorNotifier
import com.sammengistu.stuckfirebase.access.FirebaseItemAccess
import com.sammengistu.stuckfirebase.access.UserAccess
import com.sammengistu.stuckfirebase.data.UserModel
import kotlinx.android.synthetic.main.fragment_profile_view.*
import kotlinx.android.synthetic.main.fragment_stats.*

class ProfileViewFragment : BaseFragment() {

    lateinit var avatarView: AvatarView
    lateinit var usernameField: TextView
    lateinit var descriptionField: TextView
    lateinit var nameField: TextView
    lateinit var occupationField: DisplayFormItemView
    lateinit var educationField: DisplayFormItemView
    lateinit var statVoteCollected: StatCardView
    lateinit var statVoteMade: StatCardView
    lateinit var statCollectedStars: StatCardView
    lateinit var statTotalPoints: StatCardView
    
    override fun getLayoutId() = R.layout.fragment_profile_view

    override fun getFragmentTag() = TAG

    override fun getFragmentTitle() = TITLE

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        populateFields()
    }

    private fun initViews() {
        avatarView = avatar_view
        usernameField = username
        descriptionField = description
        nameField = name_field
        occupationField = occupation_field
        educationField = education_field
        statVoteCollected = votes_collected
        statVoteMade = votes_made
        statCollectedStars = collected_stars
        statTotalPoints = total_points_view

        statCollectedStars.setStat(0)
        statVoteCollected.setStat(0)
        statVoteMade.setStat(0)
        statTotalPoints.setStat(0)
    }

    private fun populateFields() {
        val userId = arguments?.getString(EXTRA_USER_ID)
        if (userId != null) {
            UserAccess().getItemsWhereEqual("userId", userId,
                object : FirebaseItemAccess.OnItemRetrieved<UserModel> {
                    override fun onSuccess(list: List<UserModel>) {
                        if (list.isNotEmpty()) {
                            val user = list[0]
                            if (user.avatar.isNotBlank()) {
                                avatarView.loadImage(user.avatar)
                            }
                            usernameField.setText(user.username)
                            descriptionField.setText(user.bio)
                            nameField.setText(user.name)
                            occupationField.setText(user.occupation)
                            educationField.setText(user.education)
                            statCollectedStars.setStat(user.totalReceivedStars)
                            statVoteCollected.setStat(user.totalReceivedVotes)
                            statVoteMade.setStat(user.totalMadeVotes)
                            statTotalPoints.setStat(user.totalReceivedStars + user.totalReceivedVotes + user.totalMadeVotes)
                        } else {
                            activity!!.supportFragmentManager.popBackStack()
                            ErrorNotifier.notifyError(activity, "Error loading user")
                        }
                    }

                    override fun onFailed(e: Exception) {
                        ErrorNotifier.notifyError(activity!!, TAG, "Error loading profile", e)
                    }
                })
        }
    }

    companion object {
        val TAG = ProfileViewFragment::class.java.simpleName
        const val TITLE = "Profile"
        const val EXTRA_USER_ID = "extra_user_id"

        fun newInstance(userId : String): ProfileViewFragment {
            val bundle = Bundle()
            bundle.putString(EXTRA_USER_ID, userId)

            val frag = ProfileViewFragment()
            frag.arguments = bundle
            return frag
        }
    }
}