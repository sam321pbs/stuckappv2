package com.sammengistu.stuckapp.fragments

import android.os.Bundle
import android.view.View
import com.sammengistu.stuckfirebase.UserHelper
import com.sammengistu.stuckapp.adapters.NotifyAdapter
import com.sammengistu.stuckapp.bottomsheet.BottomSheetHelper
import com.sammengistu.stuckapp.bottomsheet.BottomSheetMenu
import com.sammengistu.stuckfirebase.data.PostModel
import kotlinx.android.synthetic.main.bottom_sheet_post_view.*

abstract class BasePostListsFragment : BaseFragment(), BottomSheetMenu, NotifyAdapter {

    lateinit var mBottomSheetMenu: BottomSheetMenu
    private lateinit var mBottomSheetHelper: BottomSheetHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBottomSheetMenu = this

        UserHelper.getCurrentUser { user ->
            if (user != null) {
                mBottomSheetHelper = BottomSheetHelper(activity!!, user.userId, bottom_sheet, this)
            }
        }
    }

    override fun showMenu(post: PostModel) {
//        if (activity is MainActivity) {
//            (activity as MainActivity).hideNavBar()
//        }
        mBottomSheetHelper.showMenu(post)
    }

    override fun hideMenu() {
//        if (activity is MainActivity) {
//            (activity as MainActivity).showNavBar()
//        }
        mBottomSheetHelper.hideMenu()
    }
}