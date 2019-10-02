package com.sammengistu.stuckapp.adapters

import android.content.Context
import android.graphics.Bitmap
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sammengistu.stuckapp.AssetImageUtils
import com.sammengistu.stuckapp.OnItemClickListener
import com.sammengistu.stuckapp.views.AvatarView

class AvatarsAdapter(val context: Context,
                     val onItemClickListener: OnItemClickListener<Bitmap>)
    : RecyclerView.Adapter<AvatarsAdapter.AvatarViewHolder>() {
    private val listOfAvatars = ArrayList(AssetImageUtils.mapOfHeadShots.values)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvatarViewHolder =
        AvatarViewHolder(AvatarView(context))

    override fun getItemCount() = listOfAvatars.size

    override fun onBindViewHolder(holder: AvatarViewHolder, position: Int) {
        val bitmap = listOfAvatars[position]
        holder.view.setImageBitmap(bitmap)
        holder.view.setOnClickListener { onItemClickListener.onItemClicked(bitmap) }
    }

    class AvatarViewHolder(val view: AvatarView): RecyclerView.ViewHolder(view)
}