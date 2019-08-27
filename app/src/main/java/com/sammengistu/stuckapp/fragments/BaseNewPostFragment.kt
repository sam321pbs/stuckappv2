package com.sammengistu.stuckapp.fragments

import android.os.Bundle
import android.view.View
import com.sammengistu.stuckapp.constants.Category
import com.sammengistu.stuckapp.constants.PrivacyChoice
import com.sammengistu.stuckapp.dialog.CategoriesListDialog
import com.sammengistu.stuckapp.dialog.PostPrivacyDialog
import com.sammengistu.stuckapp.events.CategorySelectedEvent
import com.sammengistu.stuckapp.events.PrivacySelectedEvent
import kotlinx.android.synthetic.main.new_post_basic_detail_card.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

abstract class BaseNewPostFragment : BaseFragment() {

    var mSelectedCategory: String = Category.categories[0]
    var mSelectedPrivacy: String = PrivacyChoice.privacyChoices[0]

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

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }
}