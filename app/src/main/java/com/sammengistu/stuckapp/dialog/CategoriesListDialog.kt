package com.sammengistu.stuckapp.dialog

import com.sammengistu.stuckapp.constants.Category.Companion.categories
import com.sammengistu.stuckapp.events.CategorySelectedEvent
import org.greenrobot.eventbus.EventBus


class CategoriesListDialog : BasicListSelectorDialog() {
    override fun getDialogTitle(): String {
        return "Select a category"
    }

    override fun getListDataSet(): List<String> {
        return categories
    }

    override fun onItemClicked(item: String) {
        EventBus.getDefault().post(CategorySelectedEvent(item))
        dismiss()
    }

    companion object {
        val TAG = CategoriesListDialog::class.java.simpleName
    }
}