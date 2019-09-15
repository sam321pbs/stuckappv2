package com.sammengistu.stuckapp.fragments

import android.os.Bundle
import android.view.View
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.UserHelper
import com.sammengistu.stuckapp.views.StatCardView
import kotlinx.android.synthetic.main.fragment_stats.*

class StatsFragment: BaseFragment() {

    lateinit var votesCollectedCard: StatCardView
    lateinit var votesMadeCard: StatCardView
    lateinit var starsCollectedCard: StatCardView
    lateinit var mostPopularCard: StatCardView

    override fun getLayoutId(): Int = R.layout.fragment_stats

    override fun getFragmentTag(): String = TAG

    override fun getFragmentTitle(): String = TITLE

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        UserHelper.getCurrentUser { user ->
            if (user != null) {
                votesCollectedCard.setStat(user.totalReceivedVotes)
                votesMadeCard.setStat(user.totalMadeVotes)
                starsCollectedCard.setStat(user.totalReceivedStars)
            }
        }
    }

    private fun initViews() {
        votesCollectedCard = votes_collected
        votesMadeCard = votes_made
        starsCollectedCard = collected_stars
        mostPopularCard = most_popular_post
    }

    companion object {
        const val TITLE = "Stats"
        val TAG = StatsFragment::class.java.simpleName
    }
}