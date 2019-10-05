package com.sammengistu.stuckapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.sammengistu.stuckapp.events.AssetsLoadedEvent
import com.sammengistu.stuckapp.views.AvatarView
import com.sammengistu.stuckfirebase.ErrorNotifier
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.lang.ref.WeakReference
import java.util.*
import kotlin.collections.HashMap



class AssetImageUtils {
    companion object {
        val mapOfHeadShots = HashMap<String, Bitmap>()
        val TAG = AssetImageUtils::class.java.simpleName
        var isLoaded = false

        fun initListOfImages(context: Context) {
            doAsync {
                loadImages(context)
                isLoaded = mapOfHeadShots.isNotEmpty()
                uiThread {
                    EventBus.getDefault().post(AssetsLoadedEvent())
                }
            }
        }

        private fun loadImages(context: Context) {
            if (mapOfHeadShots.isEmpty()) {
                val assetManager = context.assets
                val directoryName = "avatar_replacer"
                val list = assetManager.list(directoryName)
                if (list != null) {
                    for (fileName in list) {
                        val filePath = directoryName.plus("/").plus(fileName)
                        val inputStream = assetManager.open(filePath)
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        mapOfHeadShots[filePath] = bitmap
                    }
                } else {
                    ErrorNotifier.notifyError(context, "Assets were empty")
                }
            }
        }

        fun getAvatar(key: String) = mapOfHeadShots[key]

        fun getRandomAvatar(): Bitmap? {
            if (mapOfHeadShots.isEmpty()) {
                Log.e(TAG, "List of images is empty")
                return null
            }
            val pos = Random().nextInt(mapOfHeadShots.size)
            return mapOfHeadShots.values.toList()[pos]
        }

        fun getRandomAvatarKey(): String? {
            if (mapOfHeadShots.isEmpty()) {
                Log.e(TAG, "List of images is empty")
                return null
            }
            val pos = Random().nextInt(mapOfHeadShots.size)
            return mapOfHeadShots.keys.toTypedArray()[pos]
        }

        fun addImageToAvatarView(context: Context, view: AvatarView) {
            val weakRef = WeakReference<AvatarView>(view)
            if (mapOfHeadShots.isEmpty()) {
                doAsync {
                    loadImages(context)
                    uiThread {
                        setImage(weakRef.get())
                    }
                }
            } else {
                setImage(view)
            }
        }

        private fun setImage(view: AvatarView?) {
            if (view != null) {
                val image = getRandomAvatar()
                if (image != null) {
                    view.setImageBitmap(image)
                } else {
                    TODO("get place holder")
                }
            }
        }
    }
}