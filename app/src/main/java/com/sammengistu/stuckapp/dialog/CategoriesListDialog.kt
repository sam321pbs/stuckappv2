package com.sammengistu.stuckapp.dialog

import com.sammengistu.stuckapp.constants.Categories
import com.sammengistu.stuckapp.events.CategorySelectedEvent
import org.greenrobot.eventbus.EventBus


class CategoriesListDialog : BasicListSelectorDialog() {
    override fun getDialogTitle(): String {
        return "Select a category"
    }

    override fun getListDataSet(): List<String> {
        return Categories.asListRemoveSortCategories()
    }

    override fun onItemClicked(item: String) {
        EventBus.getDefault().post(CategorySelectedEvent(item))
        dismiss()
    }

    companion object {
        val TAG = CategoriesListDialog::class.java.simpleName
    }
}