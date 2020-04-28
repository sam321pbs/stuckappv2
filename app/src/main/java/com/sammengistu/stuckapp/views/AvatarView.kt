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
//                val src = getResourceId(
//                    R.styleable.AvatarView_avatarSrc,
//                    R.mipmap.peter_griffan_head_shot
//                )
//                loadImage(src)
            } finally {
                recycle()
            }
        }
    }

    constructor(context: Context) : this(context, null)

    fun loadImage(url: String?) {
        if (!url.isNullOrBlank()) {
            post {
                Picasso.get()
                    .load(url)
                    .fit()
                    .placeholder(R.drawable.ic_person_grey_500_36dp)
//                .centerCrop()
                    .into(this)

            }
        }
    }

    fun loadImage(resourceId: Int) {
        post {
            Picasso.get()
                .load(resourceId)
                .placeholder(R.drawable.ic_person_grey_500_36dp)
                .fit()
//                .centerCrop()
                .into(this)
        }
    }
}