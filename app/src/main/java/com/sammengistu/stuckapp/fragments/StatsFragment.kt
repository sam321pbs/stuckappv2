package com.sammengistu.stuckapp.fragments

import android.os.Bundle
import android.view.View
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.views.StatCardView
import com.sammengistu.stuckfirebase.repositories.UserRepository
import kotlinx.android.synthetic.main.fragment_stats.*

class StatsFragment: BaseFragment() {

    lateinit var votesCollectedCard: StatCardView
    lateinit var votesMadeCard: StatCardView
    lateinit var starsCollectedCard: StatCardView
    lateinit var totalPointsCard: StatCardView

    override fun getLayoutId(): Int = R.layout.fragment_stats

    override fun getFragmentTag(): String = TAG

    override fun getFragmentTitle(): String = TITLE

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        UserRepository.getUserInstance(context!!) { user ->
            if (user != null) {
                votesCollectedCard.setStat(user.totalReceivedVotes)
                votesMadeCard.setStat(user.totalMadeVotes)
                starsCollectedCard.setStat(user.totalReceivedStars)
                totalPointsCard.setStat(user.totalReceivedVotes + user.totalMadeVotes + user.totalReceivedStars)
            }
        }
    }

    private fun initViews() {
        votesCollectedCard = votes_collected
        votesMadeCard = votes_made
        starsCollectedCard = collected_stars
        totalPointsCard = total_points_view
    }

    companion object {
        const val TITLE = "Stats"
        val TAG = StatsFragment::class.java.simpleName
    }
}