package com.sammengistu.stuckapp.dialog

import com.sammengistu.stuckapp.constants.PrivacyOptions
import com.sammengistu.stuckapp.events.PrivacySelectedEvent
import org.greenrobot.eventbus.EventBus

class PostPrivacyDialog : BasicListSelectorDialog() {
    override fun getDialogTitle() = "Select post Privacy"

    override fun getListDataSet() = PrivacyOptions.asList()

    override fun onItemClicked(item: String) {
        EventBus.getDefault().post(PrivacySelectedEvent(item))
        dismiss()
    }

    companion object {
        val TAG = PostPrivacyDialog::class.java.simpleName
    }
}