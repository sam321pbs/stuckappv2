package com.sammengistu.stuckapp.fragments

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import com.sammengistu.stuckapp.views.ChoiceCardView
import kotlinx.android.synthetic.main.fragment_new_post_text.*


class NewTextPostFragment : BaseFragment() {

    lateinit var mChoicesContainer : LinearLayout

    override fun getLayoutId(): Int {
        return com.sammengistu.stuckapp.R.layout.fragment_new_post_text
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mChoicesContainer = choices_container
        add_choice_fab.setOnClickListener {
            if (mChoicesContainer.childCount < 4) {
                val newChild = ChoiceCardView(this@NewTextPostFragment.activity!!.applicationContext)
                newChild.setHint("Choice ${mChoicesContainer.childCount + 1}")
                mChoicesContainer.addView(newChild)

                if (mChoicesContainer.childCount >= 4) {
                    add_choice_fab.visibility = View.GONE
                }
            }
        }
    }
}