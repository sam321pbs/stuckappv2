package com.sammengistu.stuckapp.views

import android.content.Context
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.collections.PostVotesCollection
import com.sammengistu.stuckfirebase.access.PostVoteAccess
import com.sammengistu.stuckfirebase.models.ChoiceModel
import com.sammengistu.stuckfirebase.models.UserModel
import com.sammengistu.stuckfirebase.models.PostVoteModel
import com.sammengistu.stuckfirebase.repositories.UserRepository

abstract class VotableContainer(
    context: Context,
    val postRef: String,
    val postOwnerRef: String,
    var choice: ChoiceModel,
    var postVote: PostVoteModel?
) : RelativeLayout(context), DoubleTapGesture.DoubleTapListener {

    var mGestureDetector = DoubleTapGesture(context, this)

    private var onItemVotedOnListener: OnItemVotedOnListener? = null

    interface OnItemVotedOnListener {
        fun onItemVotedOn(postVote: PostVoteModel)
    }

    init {
        applyStyleManually(context)
    }

    abstract fun onNewVoteCreated(postVote: PostVoteModel?)

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return mGestureDetector.onTouchEvent(event)
    }

    override fun onDoubleTapped() {
        UserRepository.getUserInstance(context!!) { user ->
            if (user != null && allowUserToVote(user)) {
                val postVote = PostVoteModel(
                    user.ref,
                    postRef,
                    postOwnerRef,
                    choice.id
                )
                PostVoteAccess().createItemInFB(postVote)
                PostVotesCollection.getInstance(context).addVoteToMap(postVote)
                onNewVoteCreated(postVote)
                onItemVotedOnListener?.onItemVotedOn(postVote)
            }
        }
    }

    fun setOnItemVotedListener(listener: OnItemVotedOnListener) {
        onItemVotedOnListener = listener
    }

    private fun allowUserToVote(user: UserModel?) =
        postRef.isNotBlank() && postVote == null && user != null && postOwnerRef != user.ref

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