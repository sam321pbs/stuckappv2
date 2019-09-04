package com.sammengistu.stuckapp.views

import android.content.Context
import android.view.MotionEvent
import android.widget.RelativeLayout
import com.sammengistu.stuckfirebase.access.UserVoteAccess
import com.sammengistu.stuckfirebase.data.UserVoteModel

abstract class VotableContainer(
    context: Context,
    var owner: String,
    var postId: String,
    var votePos: Int
)
: RelativeLayout(context), DoubleTapGesture.DoubleTapListener {

    var mGestureDetector = DoubleTapGesture(context, this)

    abstract fun onItemVotedOn()

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return mGestureDetector.onTouchEvent(event)
    }

    override fun onDoubleTapped() {
//        Log.d(VotableChoiceView.TAG, "$owner voted on $postId choice $votePos")
        UserVoteAccess(owner).createItemInFB(UserVoteModel(owner, postId, votePos))
        onItemVotedOn()
    }
}