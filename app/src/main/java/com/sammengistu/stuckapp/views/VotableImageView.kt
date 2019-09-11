package com.sammengistu.stuckapp.views

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.UserVotesCollection
import com.sammengistu.stuckapp.helpers.ViewHelper
import com.sammengistu.stuckfirebase.data.PostModel
import com.sammengistu.stuckfirebase.data.UserVoteModel
import com.squareup.picasso.Picasso

class VotableImageView(
    context: Context,
    post: PostModel,
    choiceItem: Triple<String, String, Int>,
    userVote: UserVoteModel?,
    updateParentContainer: UpdateParentContainer
) : VotableContainer(context, post, choiceItem, userVote, updateParentContainer) {

    private val imageView = ImageView(context)
    private val votesTextView = TextView(context)
    private val progressBar = ProgressBar(context)

    init {
        buildView()
        buildVotesText()
        loadImage(choiceItem.second)
    }

    override fun onItemVotedOn(userVote: UserVoteModel?) {
        // Todo: start animation to show votes
        handleVotedItem(userVote, true)
    }

    fun setTotal(amount: Int) {
        votesTextView.text = amount.toString()
    }

    private fun loadImage(imageLoc: String) {
        post {
            Picasso.get()
                .load(imageLoc)
                .fit()
//                .centerCrop()
                .into(imageView)
        }
    }

    private fun buildView() {
        val params = getParams()
        layoutParams = params
        imageView.layoutParams = params

        val params2 = LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        params2.addRule(CENTER_IN_PARENT)
        progressBar.layoutParams = params2

//        imageView.setOnClickListener {
//            val drawable = imageView.drawable as BitmapDrawable
//            val bitmap = drawable.bitmap
//                FullScreenImageActivity.startActivity(
//                    context,
//                    ViewHelper.convertDrawableToBitmap(bitmap))
//        }

        val userVote = UserVotesCollection.getVoteForPost(post.ref)
        if (userVote == null) {
            votesTextView.visibility = View.GONE
        } else {
            votesTextView.visibility = View.VISIBLE
            // Todo: show vote and color if vote matches
        }

        addView(progressBar)
        addView(imageView)
    }

    private fun buildVotesText() {
        val votesParams = LayoutParams(80, 80)
        votesParams.marginEnd = 20
        votesParams.topMargin = 20
        votesParams.addRule(ALIGN_PARENT_END)
        votesTextView.layoutParams = votesParams
        votesTextView.gravity = Gravity.CENTER
        votesTextView.setBackgroundResource(R.drawable.gray_circle)
        votesTextView.setPadding(5, 5, 5, 5)
        votesTextView.textSize = 15f
        setTotal(choiceItem.third)
        handleVotedItem(userVote)

        addView(votesTextView)
    }

    private fun handleVotedItem(userVote: UserVoteModel?, isUpdate: Boolean = false) {
        if (userVote == null) {
            votesTextView.visibility = View.GONE
        } else {
            votesTextView.visibility = View.VISIBLE
            if (choiceItem.first.toInt() == userVote.voteItem) {
                votesTextView.setBackgroundResource(R.drawable.green_circle)
                if (isUpdate) {
                    setTotal(choiceItem.third + 1)
                }
            }
        }
    }

    private fun getParams(): ViewGroup.LayoutParams =
        ViewGroup.LayoutParams(MATCH_PARENT, ViewHelper.convertDpToPixel(250F, context).toInt())

    companion object {
        val TAG = VotableImageView::class.java.simpleName
    }
}