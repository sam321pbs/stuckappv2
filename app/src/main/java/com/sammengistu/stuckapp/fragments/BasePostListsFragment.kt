package com.sammengistu.stuckapp.fragments

import android.os.Bundle
import android.view.View
import com.sammengistu.stuckapp.adapters.NotifyAdapter
import com.sammengistu.stuckapp.bottomsheet.BottomSheetHelper
import com.sammengistu.stuckapp.bottomsheet.BottomSheetMenu
import com.sammengistu.stuckfirebase.models.PostModel
import kotlinx.android.synthetic.main.bottom_sheet_post_view.*
import kotlinx.android.synthetic.main.fragment_post_list.*

abstract class BasePostListsFragment : BaseFragment(), BottomSheetMenu, NotifyAdapter {

    lateinit var bottomSheetMenu: BottomSheetMenu
    private lateinit var bottomSheetHelper: BottomSheetHelper
    private lateinit var invisibleCover: View

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomSheetMenu = this
        bottomSheetHelper = BottomSheetHelper(activity!!, bottom_sheet, this)
        invisibleCover = invisible_view
        invisibleCover.setOnClickListener { hideMenu() }
    }

    override fun showMenu(post: PostModel) {
//        if (activity is MainActivity) {
//            (activity as MainActivity).hideNavBar()
//        }
        if (invisibleCover != null) {
            invisibleCover.visibility = View.VISIBLE
        }
        bottomSheetHelper.showMenu(post)
    }

    override fun hideMenu() {
//        if (activity is MainActivity) {
//            (activity as MainActivity).showNavBar()
//        }
        if (invisibleCover != null) {
            invisibleCover.visibility = View.GONE
        }
        bottomSheetHelper.hideMenu()
    }
}