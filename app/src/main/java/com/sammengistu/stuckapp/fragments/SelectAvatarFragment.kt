package com.sammengistu.stuckapp.fragments

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sammengistu.stuckapp.OnItemClickListener
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.adapters.AvatarsAdapter
import com.sammengistu.stuckapp.events.OnAvatarSelected
import com.sammengistu.stuckapp.helpers.RecyclerViewHelper
import kotlinx.android.synthetic.main.fragment_select_avatar.*
import org.greenrobot.eventbus.EventBus

class SelectAvatarFragment : BaseFragment(), OnItemClickListener<Bitmap> {

    private lateinit var recyclerView: RecyclerView

    override fun getLayoutId() = R.layout.fragment_select_avatar

    override fun getFragmentTag(): String = TAG

    override fun getFragmentTitle() = TITLE

    override fun onItemClicked(item: Bitmap) {
        EventBus.getDefault().post(OnAvatarSelected(item))
        activity!!.supportFragmentManager.popBackStack()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = recycler_view
        val adapter = AvatarsAdapter(activity!!, this) as RecyclerView.Adapter<RecyclerView.ViewHolder>
        RecyclerViewHelper.setupWithGridManager(activity!!, recyclerView, adapter)
    }

    companion object {
        val TAG = SelectAvatarFragment::class.java.simpleName
        const val TITLE = "Select Avatar"
    }
}