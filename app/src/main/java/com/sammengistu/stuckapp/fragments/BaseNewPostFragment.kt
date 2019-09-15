package com.sammengistu.stuckapp.fragments

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.children
import com.sammengistu.stuckapp.ErrorNotifier
import com.sammengistu.stuckapp.UserHelper
import com.sammengistu.stuckapp.activities.NewPostActivity
import com.sammengistu.stuckapp.constants.Categories
import com.sammengistu.stuckapp.constants.PrivacyOptions
import com.sammengistu.stuckapp.data.DraftPost
import com.sammengistu.stuckapp.dialog.CategoriesListDialog
import com.sammengistu.stuckapp.dialog.PostPrivacyDialog
import com.sammengistu.stuckapp.events.CategorySelectedEvent
import com.sammengistu.stuckapp.events.PrivacySelectedEvent
import com.sammengistu.stuckapp.utils.ImageStorageUtils
import com.sammengistu.stuckapp.views.ChoiceCardView
import com.sammengistu.stuckfirebase.access.FirebaseItemAccess
import com.sammengistu.stuckfirebase.access.PostAccess
import com.sammengistu.stuckfirebase.constants.PostType
import com.sammengistu.stuckfirebase.data.PostModel
import com.sammengistu.stuckfirebase.data.UserModel
import kotlinx.android.synthetic.main.fragment_new_image_post.*
import kotlinx.android.synthetic.main.new_post_basic_detail_card.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*

abstract class BaseNewPostFragment : BaseFragment() {

    val IMAGE_1 = "image_1"
    val IMAGE_2 = "image_2"
    val CHOICES_VIEW = "choices_view"
    var draft: DraftPost? = null

    var mSelectedCategory: String = Categories.GENERAL.toString()
    var mSelectedPrivacy: String = PrivacyOptions.PUBLIC.toString()

    abstract fun fieldsValidated(): Boolean

    @Subscribe
    fun onCategorySelected(event: CategorySelectedEvent) {
        mSelectedCategory = event.category
        category_choice.setText(mSelectedCategory)
    }

    @Subscribe
    fun onPrivacySelected(event: PrivacySelectedEvent) {
        mSelectedPrivacy = event.choice
        privacy_choice.setText(mSelectedPrivacy)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        category_choice.setOnClickListener {
            CategoriesListDialog().show(
                activity!!.supportFragmentManager,
                CategoriesListDialog.TAG
            )
        }

        privacy_choice.setOnClickListener {
            PostPrivacyDialog().show(
                activity!!.supportFragmentManager,
                PostPrivacyDialog.TAG
            )
        }

        username.text = "username"

        UserHelper.getCurrentUser {
            if (it != null) {
                avatar_view.loadImage(it.avatar)
                username.text = it.username
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (activity is NewPostActivity) {
            (activity as NewPostActivity).showDraftIcon()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (activity is NewPostActivity) {
            (activity as NewPostActivity).hideDraftIcon()
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    fun createImagePost(type: PostType, bitmap1: Bitmap?, bitmap2: Bitmap?) {
        val data = mapOf(Pair(IMAGE_1, bitmap1), Pair(IMAGE_2, bitmap2))
        createPost(type, data)
    }

    fun createTextPost(view: LinearLayout) {
        val data = mapOf(Pair(CHOICES_VIEW, view))
        createPost(PostType.TEXT, data)
    }

    fun draftImagePost(type: PostType, bitmap1: Bitmap?, bitmap2: Bitmap?) {
        val data = mapOf(Pair(IMAGE_1, bitmap1), Pair(IMAGE_2, bitmap2))
        saveAsDraft(type, data)
    }

    fun draftTextPost(view: LinearLayout) {
        val data = mapOf(Pair(CHOICES_VIEW, view))
        saveAsDraft(PostType.TEXT, data)
    }

    protected fun updateViewFromDraft(draft: DraftPost) {
        question.setText(draft.question)
        mSelectedCategory = draft.category
        mSelectedPrivacy = draft.privacy
        category_choice.setText(mSelectedCategory)
        privacy_choice.setText(mSelectedPrivacy)
    }

    private fun createPost(type: PostType, data: Map<String, Any?>) {
        if (fieldsValidated()) {
            UserHelper.getCurrentUser {
                progress_bar.visibility = View.VISIBLE

                if (it != null) {
                    try {
                        val post = buildPost(it, type)

                        if (data.containsKey(IMAGE_1) && data.containsKey(IMAGE_2)) {
                            val data1 = data[IMAGE_1]
                            val data2 = data[IMAGE_2]
                            val bitmap1 = if (data1 is Bitmap) data1 else null
                            val bitmap2 = if (data2 is Bitmap) data2 else null
                            PostAccess().createImagePost(
                                post,
                                bitmap1!!,
                                bitmap2!!,
                                getOnItemCreatedCallback()
                            )
                        } else if (data.containsKey(CHOICES_VIEW)) {
                            addChoicesToPost(data, post)
                            PostAccess().createItemInFB(
                                post,
                                getOnItemCreatedCallback()
                            )
                        }
                    } catch (e: Exception) {
                        handleFailedToCreateItem("Exception building post", e)
                    }
                } else {
                    handleFailedToCreateItem("User was null")
                }
            }
        }
    }

    private fun saveAsDraft(type: PostType, data: Map<String, Any?>) {
        val post = buildPost(null, type)

        if (data.containsKey(CHOICES_VIEW)) {
            addChoicesToPost(data, post)
        }

        doAsync {
            uiThread {
                progress_bar.visibility = View.VISIBLE
            }
            saveImagesToLocalStorage(data, post)
            PostAccess.insertPost(activity!!.applicationContext, post.toDraft())
            uiThread {
                handleItemCreated("Draft Saved!")
            }
        }
    }

    private fun saveImagesToLocalStorage(
        data: Map<String, Any?>,
        post: PostModel
    ) {
        if (data.containsKey(IMAGE_1) && data.containsKey(IMAGE_2)) {
            val data1 = data[IMAGE_1]
            val data2 = data[IMAGE_2]
            val bitmap1 = if (data1 is Bitmap) data1 else null
            val bitmap2 = if (data2 is Bitmap) data2 else null

            val image1Loc = ImageStorageUtils.saveToInternalStorage(
                activity!!,
                bitmap1!!,
                "${UUID.randomUUID()}"
            )
            val image2Loc = ImageStorageUtils.saveToInternalStorage(
                activity!!,
                bitmap2!!,
                "${UUID.randomUUID()}"
            )

            post.addImage(image1Loc)
            post.addImage(image2Loc)
        }
    }

    private fun addChoicesToPost(
        data: Map<String, Any?>,
        post: PostModel
    ) {
        val data1 = data[CHOICES_VIEW]
        val choiceContainer = if (data1 is LinearLayout) data1 else null
        for (choiceView in choiceContainer!!.children) {
            if (choiceView is ChoiceCardView) {
                post.addChoice(choiceView.getChoiceText())
            }
        }
    }

    private fun getOnItemCreatedCallback(): FirebaseItemAccess.OnItemCreated<PostModel> {
        return object : FirebaseItemAccess.OnItemCreated<PostModel> {
            override fun onSuccess(item: PostModel) {
                handleItemCreated("Post has been created")
            }

            override fun onFailed(e: java.lang.Exception) {
                handleFailedToCreateItem("Failed to create post in Firebase", e)
            }
        }
    }

    private fun handleItemCreated(message: String) {
        deleteDraft()
        progress_bar.visibility = View.GONE
        Toast.makeText(activity!!, message, Toast.LENGTH_SHORT)
            .show()
        activity!!.finish()
    }

    private fun handleFailedToCreateItem(message: String) {
        progress_bar.visibility = View.GONE
        Toast.makeText(activity!!, message, Toast.LENGTH_SHORT).show()
    }

    private fun handleFailedToCreateItem(message: String, e: java.lang.Exception) {
        progress_bar.visibility = View.GONE
        Toast.makeText(activity!!, message, Toast.LENGTH_SHORT).show()
        Log.e(TAG, message, e)
    }

    private fun buildPost(
        user: UserModel? = null,
        type: PostType
    ): PostModel {
        return PostModel(
            user?.userId ?: "",
            user?.ref ?: "",
            user?.username ?: "",
            user?.avatar ?: "",
            question.text.toString(),
            mSelectedPrivacy,
            mSelectedCategory,
            type.toString()
        )
    }

    private fun deleteDraft() {
        if (draft != null) {
            doAsync {
                try {
                    PostAccess.deletePost(context!!, draft!!.postId)
                } catch (e: Exception) {
                    ErrorNotifier.notifyError(context!!, TAG, "Error deleting post", e)
                }
            }
        }
    }

    companion object {
        val TAG = BaseNewPostFragment::class.java.simpleName
    }
}