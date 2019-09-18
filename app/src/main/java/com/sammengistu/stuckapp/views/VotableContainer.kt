package com.sammengistu.stuckapp.views

import android.content.Context
import android.view.MotionEvent
import android.widget.RelativeLayout
import com.sammengistu.stuckapp.UserHelper
import com.sammengistu.stuckapp.UserVotesCollection
import com.sammengistu.stuckfirebase.access.UserVoteAccess
import com.sammengistu.stuckfirebase.data.PostModel
import com.sammengistu.stuckfirebase.data.UserModel
import com.sammengistu.stuckfirebase.data.UserVoteModel

abstract class VotableContainer(
    context: Context,
    var post: PostModel,
    var choiceItem: Triple<String, String, Int>,
    var userVote: UserVoteModel?,
    private val updateParentContainer: UpdateParentContainer
) : RelativeLayout(context), DoubleTapGesture.DoubleTapListener {

    interface UpdateParentContainer {
        fun updateContainer(userVote: UserVoteModel?)
    }

    var mGestureDetector = DoubleTapGesture(context, this)

    abstract fun onItemVotedOn(userVote: UserVoteModel?)

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return mGestureDetector.onTouchEvent(event)
    }

    override fun onDoubleTapped() {
        UserHelper.getCurrentUser { user ->
            if (allowUserToVote(user)) {
                    val userVote = UserVoteModel(
                        user!!.userId,
                        user.ref,
                        user.username,
                        user.avatar,
                        post.ref,
                        post.ownerRef,
                        choiceItem.first
                    )
                    UserVoteAccess().createItemInFB(userVote)
                    UserVotesCollection.addVoteToMap(userVote)
                    onItemVotedOn(userVote)
                    updateParentContainer.updateContainer(userVote)
            }
        }
    }

    private fun allowUserToVote(user: UserModel?) =
        post.ref.isNotBlank() && userVote == null && user != null && post.ownerId != user.userId
}