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
import com.sammengistu.stuckapp.collections.UserVotesCollection
import com.sammengistu.stuckapp.helpers.ViewHelper
import com.sammengistu.stuckfirebase.UserHelper
import com.sammengistu.stuckfirebase.models.PostModel
import com.sammengistu.stuckfirebase.models.UserVoteModel
import com.squareup.picasso.Picasso
import java.io.File

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
        if (post.ref.isBlank()) {
            loadImageFromFile(choiceItem.second)
        } else {
            loadImage(choiceItem.second)
        }
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

    private fun loadImageFromFile(imageLoc: String) {
        post {
            val file = File(imageLoc)
            Picasso.get()
                .load(file)
                .fit()
//                .centerCrop()
                .into(imageView)
        }
    }

    private fun isUsersPost() =
        UserHelper.currentUser != null && UserHelper.currentUser!!.ref == post.ownerRef

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
        votesTextView.setBackgroundResource(R.drawable.circle_gray)
        votesTextView.setPadding(5, 5, 5, 5)
        votesTextView.textSize = 15f
        setTotal(choiceItem.third)
        handleVotedItem(userVote)

        addView(votesTextView)
    }

    private fun handleVotedItem(userVote: UserVoteModel?, isUpdate: Boolean = false) {
        if (isUsersPost()) {
            votesTextView.visibility = View.VISIBLE
        } else if (userVote == null) {
            votesTextView.visibility = View.GONE
        } else {
            votesTextView.visibility = View.VISIBLE
            if (choiceItem.first == userVote.voteItem) {
                votesTextView.setBackgroundResource(R.drawable.circle_green)
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