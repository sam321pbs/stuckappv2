package com.sammengistu.stuckapp.helpers

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.observe
import com.sammengistu.stuckapp.events.DataChangedEvent
import com.sammengistu.stuckfirebase.database.InjectorUtils
import com.sammengistu.stuckfirebase.models.HiddenItemModel
import com.sammengistu.stuckfirebase.repositories.UserRepository
import com.sammengistu.stuckfirebase.viewmodels.HiddenItemsViewModel
import org.greenrobot.eventbus.EventBus

class HiddenItemsHelper(lifeCycleOwner: ComponentActivity) {

    private val listViewModel: HiddenItemsViewModel by lifeCycleOwner.viewModels {
        val ownerId = UserRepository.firebaseUserId
        InjectorUtils.provideHiddenItemsListFactory(lifeCycleOwner, ownerId)
    }

    init {
        listViewModel.items.observe(lifeCycleOwner) { hiddenItems ->
            addAll(hiddenItems)
            EventBus.getDefault().post(DataChangedEvent())
        }
    }

    companion object {
        private val items = HashMap<String, HiddenItemModel>()

        fun containesRef(ref: String) = items.contains(ref)

        fun getItem(ref: String) = items[ref]

        fun addAll(items: List<HiddenItemModel>) {
            this.items.clear()
            for (item in items) {
                this.items[item.itemRef] = item
            }
        }
    }
}