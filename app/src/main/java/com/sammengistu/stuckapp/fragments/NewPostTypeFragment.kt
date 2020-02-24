package com.sammengistu.stuckapp.fragments

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckfirebase.AnalyticsHelper
import com.sammengistu.stuckfirebase.constants.AnalyticEventType
import kotlinx.android.synthetic.main.fragment_new_post_type.*

class NewPostTypeFragment : BaseFragment() {

    override fun getFragmentTag(): String = TAG

    override fun getLayoutId(): Int = R.layout.fragment_new_post_type

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        image_post_choice_container.setOnClickListener {
            AnalyticsHelper.postSelectEvent(
                activity!!,
                AnalyticEventType.CLICK,
                "start_create_image_text_post"
            )
            val action = NewPostTypeFragmentDirections
                .actionNewPostTypeFragmentToNewImagePostFragment()
            findNavController().navigate(action)
        }

        text_post_choice_container.setOnClickListener {
            AnalyticsHelper.postSelectEvent(
                activity!!,
                AnalyticEventType.CLICK,
                "start_create_text_post"
            )
            val action = NewPostTypeFragmentDirections
                .actionNewPostTypeFragmentToNewTextPostFragment()
            findNavController().navigate(action)
        }
    }

    companion object {
        private const val TAG: String = "NewPostTypeFragment"
    }
}