package com.sammengistu.stuckapp.fragments

import android.content.Context
import com.sammengistu.stuckapp.activities.MainActivity
import com.sammengistu.stuckapp.bottomsheet.BottomSheetMenu

abstract class BasePostListsFragment : BaseFragment() {

    lateinit var mBottomSheetMenu: BottomSheetMenu

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            mBottomSheetMenu = context
        }
    }
}