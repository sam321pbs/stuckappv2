package com.sammengistu.stuckapp.fragments

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.children
import com.sammengistu.stuckapp.constants.Categories
import com.sammengistu.stuckapp.constants.PrivacyOptions
import com.sammengistu.stuckapp.data.PostAccess
import com.sammengistu.stuckapp.dialog.CategoriesListDialog
import com.sammengistu.stuckapp.dialog.PostPrivacyDialog
import com.sammengistu.stuckapp.events.CategorySelectedEvent
import com.sammengistu.stuckapp.events.PrivacySelectedEvent
import com.sammengistu.stuckapp.views.ChoiceCardView
import com.sammengistu.stuckfirebase.constants.PostType
import com.sammengistu.stuckfirebase.data.PostModel
import kotlinx.android.synthetic.main.fragment_new_image_post.*
import kotlinx.android.synthetic.main.new_post_basic_detail_card.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

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
            doAsync {
                uiThread {
                    progress_bar.visibility = View.VISIBLE
                }

                var success = false
                try {
                    val post = PostModel(
                        "Sam_1",
                        username.text.toString(),
                        "ava",
                        question.text.toString(),
                        mSelectedPrivacy,
                        mSelectedCategory,
                        type.toString()
                    )

                    if (data.containsKey(IMAGE_1) && data.containsKey(IMAGE_2)) {
                        val data1 = data[IMAGE_1]
                        val data2 = data[IMAGE_2]
                        val bitmap1 = if (data1 is Bitmap) data1 else null
                        val bitmap2 = if (data2 is Bitmap) data2 else null
                        PostAccess().createImagePost(post, bitmap1!!, bitmap2!!)
                    } else if (data.containsKey(CHOICES_VIEW)) {
                        val data1 = data[CHOICES_VIEW]
                        val choiceContainer = if (data1 is LinearLayout) data1 else null
                        for (choiceView in choiceContainer!!.children) {
                            if (choiceView is ChoiceCardView) {
                                post.addChoice(choiceView.getChoiceText())
                            }
                        }
                        PostAccess().createItemInFB(post)
                    }
//                    PostAccess.insertPost(activity!!.applicationContext, post)
                    success = true
                } catch (e: Exception) {
                    Log.e(NewImagePostFragment.TAG, "Failed to create post", e)
                }

                uiThread {
                    progress_bar.visibility = View.GONE
                    if (success) {
                        Toast.makeText(activity!!, "DraftPost has been created", Toast.LENGTH_SHORT)
                            .show()
                        activity!!.finish()
                    } else {
                        Toast.makeText(activity!!, "DraftPost failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
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
}