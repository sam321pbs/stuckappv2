package com.sammengistu.stuckapp.views

import android.content.Context
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.collections.UserVotesCollection
import com.sammengistu.stuckfirebase.access.UserVoteAccess
import com.sammengistu.stuckfirebase.models.PostModel
import com.sammengistu.stuckfirebase.models.UserModel
import com.sammengistu.stuckfirebase.models.UserVoteModel
import com.sammengistu.stuckfirebase.repositories.UserRepository

abstract class VotableContainer(
    context: Context,
    var post: PostModel,
    var choiceItem: Triple<String, String, Int>,
    var userVote: UserVoteModel?,
    private val updateParentContainer: UpdateParentContainer?
) : RelativeLayout(context), DoubleTapGesture.DoubleTapListener {

    var mGestureDetector = DoubleTapGesture(context, this)

    interface UpdateParentContainer {
        fun updateContainer(userVote: UserVoteModel?)
    }

    init {
        applyStyleManually(context)
    }

    abstract fun onItemVotedOn(userVote: UserVoteModel?)

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return mGestureDetector.onTouchEvent(event)
    }

    override fun onDoubleTapped() {
        UserRepository.getUserInstance(context!!) { user ->
            if (user != null && allowUserToVote(user)) {
                val userVote = UserVoteModel(
                    user.ref,
                    user.username,
                    user.avatar,
                    post.ref,
                    choiceItem.first
                )
                UserVoteAccess().createItemInFB(userVote)
                UserVotesCollection.getInstance(context).addVoteToMap(userVote)
                onItemVotedOn(userVote)
                updateParentContainer?.updateContainer(userVote)
            }
        }
    }

    private fun allowUserToVote(user: UserModel?) =
        post.ref.isNotBlank() && userVote == null && user != null && post.ownerRef != user.ref

    private fun applyStyleManually(context: Context) {
        val attrs =
            intArrayOf(
                android.R.attr.layout_width, android.R.attr.layout_height,
                android.R.attr.layout_margin, android.R.attr.padding
            )
        val attrChoices =
            context.theme.obtainStyledAttributes(
                R.style.votable_choice_style,
                attrs
            )

        val params = LinearLayout.LayoutParams(
            attrChoices.getLayoutDimension(
                attrChoices.getIndex(0),
                ViewGroup.LayoutParams.MATCH_PARENT
            ),
            attrChoices.getLayoutDimension(
                attrChoices.getIndex(1),
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        val marginAndPadding = attrChoices.getDimension(attrChoices.getIndex(2), 10f)

        params.topMargin = marginAndPadding.toInt()
        params.marginStart = marginAndPadding.toInt()
        params.marginEnd = marginAndPadding.toInt()
        params.bottomMargin = marginAndPadding.toInt()
        setPadding(
            marginAndPadding.toInt(),
            marginAndPadding.toInt(),
            marginAndPadding.toInt(),
            marginAndPadding.toInt()
        )

        layoutParams = params
    }
}