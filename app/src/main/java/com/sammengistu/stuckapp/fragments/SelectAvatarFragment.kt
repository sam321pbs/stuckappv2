package com.sammengistu.stuckapp.fragments

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sammengistu.stuckapp.AssetImageUtils
import com.sammengistu.stuckapp.OnItemClickListener
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.adapters.AvatarsAdapter
import com.sammengistu.stuckapp.events.AssetsLoadedEvent
import com.sammengistu.stuckapp.events.OnAvatarSelected
import com.sammengistu.stuckapp.helpers.RecyclerViewHelper
import kotlinx.android.synthetic.main.fragment_select_avatar.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class SelectAvatarFragment : BaseFragment(), OnItemClickListener<Bitmap> {

    private lateinit var recyclerView: RecyclerView

    @Subscribe
    fun onAssetsLoaded(event: AssetsLoadedEvent) {
        setupRecyclerView()
    }

    override fun getLayoutId() = R.layout.fragment_select_avatar

    override fun getFragmentTag(): String = TAG

    override fun getFragmentTitle() = TITLE

    override fun onItemClicked(item: Bitmap) {
        EventBus.getDefault().post(OnAvatarSelected(item))
        activity!!.supportFragmentManager.popBackStack()
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        EventBus.getDefault().register(this)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (AssetImageUtils.mapOfHeadShots.isEmpty()) {
            AssetImageUtils.initListOfImages(activity!!)
        } else {
            setupRecyclerView()
        }
    }

    private fun setupRecyclerView() {
        progress_bar.visibility = View.GONE
        recyclerView = recycler_view
        val adapter =
            AvatarsAdapter(activity!!, this) as RecyclerView.Adapter<RecyclerView.ViewHolder>
        RecyclerViewHelper.setupWithGridManager(activity!!, recyclerView, adapter)
    }

    companion object {
        val TAG = SelectAvatarFragment::class.java.simpleName
        const val TITLE = "Select Avatar"
    }
}