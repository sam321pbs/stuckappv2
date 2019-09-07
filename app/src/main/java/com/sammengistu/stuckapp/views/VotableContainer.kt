package com.sammengistu.stuckapp.views

import android.content.Context
import android.view.MotionEvent
import android.widget.RelativeLayout
import com.sammengistu.stuckapp.UserVotesCollection
import com.sammengistu.stuckfirebase.access.UserStatsAccess
import com.sammengistu.stuckfirebase.access.UserVoteAccess
import com.sammengistu.stuckfirebase.data.PostModel
import com.sammengistu.stuckfirebase.data.UserVoteModel

abstract class VotableContainer(
    context: Context,
    var owner: String,
    var post: PostModel,
    var choiceItem: Triple<String, String, Int>,
    var userVote: UserVoteModel?,
    val updateParentContainer: UpdateParentContainer
)
: RelativeLayout(context), DoubleTapGesture.DoubleTapListener {

    interface UpdateParentContainer {
        fun updateContainer(userVote: UserVoteModel?)
    }

    var mGestureDetector = DoubleTapGesture(context, this)

    abstract fun onItemVotedOn(userVote: UserVoteModel?)

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return mGestureDetector.onTouchEvent(event)
    }

    override fun onDoubleTapped() {
        if (userVote == null) {
            val userVote = UserVoteModel(owner, post.ref, choiceItem.first.toInt())
            UserVoteAccess().createItemInFB(userVote)
            UserVotesCollection.addVoteToMap(userVote)
            UserStatsAccess.incrementCollectedVote(post.ownerId)
            onItemVotedOn(userVote)
            updateParentContainer.updateContainer(userVote)
        }
    }
}