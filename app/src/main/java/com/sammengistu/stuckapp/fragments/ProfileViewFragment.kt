package com.sammengistu.stuckapp.fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.views.AvatarView
import com.sammengistu.stuckapp.views.DisplayFormItemView
import com.sammengistu.stuckapp.views.StatCardView
import com.sammengistu.stuckfirebase.ErrorNotifier
import com.sammengistu.stuckfirebase.database.InjectorUtils
import com.sammengistu.stuckfirebase.models.UserModel
import com.sammengistu.stuckfirebase.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.fragment_profile_view.*
import kotlinx.android.synthetic.main.fragment_stats.*

private val TAG = ProfileViewFragment::class.java.simpleName
private const val TITLE = "Profile"
private const val EXTRA_USER_ID = "extra_user_id"

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
        loadUser()
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

    private fun loadUser() {
        val userId = arguments?.getString(EXTRA_USER_ID)
        if (userId != null) {
            val userViewModel: UserViewModel by viewModels {
                InjectorUtils.provideUserFactory(requireContext())
            }
            userViewModel.userLiveData.observe(viewLifecycleOwner) { users ->
                when {
                    users == null -> {
                        activity!!.supportFragmentManager.popBackStack()
                        ErrorNotifier.notifyError(activity, "Error loading user")
                    }
                    users.isEmpty() -> {
                        activity!!.supportFragmentManager.popBackStack()
                        ErrorNotifier.notifyError(activity, "Can't find user")
                    }
                    else -> {
                        populateFields(users[0])
                    }
                }
            }
        }
    }

    private fun populateFields(user: UserModel) {
        if (user.avatar.isNotBlank()) {
            avatarView.loadImage(user.avatar)
        }
        usernameField.text = user.username
        descriptionField.text = user.bio
        nameField.text = user.name
        occupationField.setText(user.occupation)
        educationField.setText(user.education)
        statCollectedStars.setStat(user.totalReceivedStars)
        statVoteCollected.setStat(user.totalReceivedVotes)
        statVoteMade.setStat(user.totalMadeVotes)
        statTotalPoints.setStat(user.totalReceivedStars + user.totalReceivedVotes + user.totalMadeVotes)
    }

    companion object {
        fun newInstance(userId : String): ProfileViewFragment {
            val bundle = Bundle()
            bundle.putString(EXTRA_USER_ID, userId)

            val frag = ProfileViewFragment()
            frag.arguments = bundle
            return frag
        }
    }
}