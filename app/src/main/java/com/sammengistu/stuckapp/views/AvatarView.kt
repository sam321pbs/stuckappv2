package com.sammengistu.stuckapp.views

import android.content.Context
import android.util.AttributeSet
import com.sammengistu.stuckapp.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class AvatarView(context: Context, attrs: AttributeSet?) : CircleImageView(context, attrs) {

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.AvatarView,
            0, 0
        ).apply {
            try {
                val src = getResourceId(
                    R.styleable.AvatarView_avatarSrc,
                    R.mipmap.peter_griffan_head_shot
                )
                loadImage(src)
            } finally {
                recycle()
            }
        }
    }

    fun loadImage(url: String) {
        if (url.isNotBlank()) {
            post {
                Picasso.get()
                    .load(url)
                    .fit()
//                .centerCrop()
                    .into(this)
            }
        }
    }

    fun loadImage(resourceId: Int) {
        post {
            Picasso.get()
                .load(resourceId)
                .fit()
//                .centerCrop()
                .into(this)
        }
    }
}