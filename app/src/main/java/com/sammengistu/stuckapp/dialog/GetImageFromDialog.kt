package com.sammengistu.stuckapp.dialog

import com.sammengistu.stuckapp.events.GetPhotoFromEvent
import org.greenrobot.eventbus.EventBus

class GetImageFromDialog : BasicListSelectorDialog() {

    override fun getDialogTitle() = TITLE

    override fun getListDataSet() = listOfChoices

    override fun onItemClicked(item: String) {
        EventBus.getDefault().post(GetPhotoFromEvent(item))
        dismiss()
    }

    companion object {
        const val TITLE = "Pick images from"
        val TAG = GetImageFromDialog::class.java.simpleName
        val listOfChoices = listOf(
            "Select from photos",
            "Select avatar"
        )
    }
}