package com.sammengistu.stuckapp.fragments

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.children
import com.sammengistu.stuckapp.UserHelper
import com.sammengistu.stuckapp.constants.Categories
import com.sammengistu.stuckapp.constants.PrivacyOptions
import com.sammengistu.stuckapp.dialog.CategoriesListDialog
import com.sammengistu.stuckapp.dialog.PostPrivacyDialog
import com.sammengistu.stuckapp.events.CategorySelectedEvent
import com.sammengistu.stuckapp.events.PrivacySelectedEvent
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

abstract class BaseNewPostFragment : BaseFragment() {

    val IMAGE_1 = "image_1"
    val IMAGE_2 = "image_2"
    val CHOICES_VIEW = "choices_view"

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

        UserHelper.getCurrentUser {
            if (it != null) {
                avatar_view.loadImage(it.avatar)
            }
        }
    }

    fun createImagePost(type: PostType, bitmap1: Bitmap?, bitmap2: Bitmap?) {
        val data = mapOf(Pair(IMAGE_1, bitmap1), Pair(IMAGE_2, bitmap2))
        createPost(type, data)
    }

    fun createTextPost(view: LinearLayout) {
        val data = mapOf(Pair(CHOICES_VIEW, view))
        createPost(PostType.TEXT, data)
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
                            val data1 = data[CHOICES_VIEW]
                            val choiceContainer = if (data1 is LinearLayout) data1 else null
                            for (choiceView in choiceContainer!!.children) {
                                if (choiceView is ChoiceCardView) {
                                    post.addChoice(choiceView.getChoiceText())
                                }
                            }
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

            // Todo: handle drafts
//            doAsync {
//                uiThread {
//
//                }
//
//                 PostAccess.insertPost(activity!!.applicationContext, post)
//                uiThread {
//
//                }
//            }
        }
    }

    private fun getOnItemCreatedCallback(): FirebaseItemAccess.OnItemCreated<PostModel> {
        return object : FirebaseItemAccess.OnItemCreated<PostModel> {
            override fun onSuccess(item: PostModel) {
                handleItemCreated()
            }

            override fun onFailed(e: java.lang.Exception) {
                handleFailedToCreateItem("Failed to create post in Firebase", e)
            }
        }
    }

    private fun handleItemCreated() {
        progress_bar.visibility = View.GONE
        Toast.makeText(activity!!, "Post has been created", Toast.LENGTH_SHORT)
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
        user: UserModel,
        type: PostType
    ): PostModel {
        return PostModel(
            user.userId,
            user.ref,
            user.username,
            user.avatar,
            question.text.toString(),
            mSelectedPrivacy,
            mSelectedCategory,
            type.toString()
        )
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    companion object {
        val TAG = BaseNewPostFragment::class.java.simpleName
    }
}