package com.sammengistu.stuckapp.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.sammengistu.stuckapp.RecyclerViewHelper
import com.sammengistu.stuckapp.adapters.CategoriesAdapter
import com.sammengistu.stuckapp.events.CategorySelectedEvent
import org.greenrobot.eventbus.EventBus


class CategoriesListDialog : DialogFragment(), RecyclerViewHelper.OnItemClickListener<String> {

    override fun onItemClicked(item: String) {
        EventBus.getDefault().post(CategorySelectedEvent(item))
        dismiss()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val containerView = inflater.inflate(com.sammengistu.stuckapp.R.layout.dialog_list, null)

            RecyclerViewHelper.setupRecyclerView(
                activity!!,
                containerView.findViewById(com.sammengistu.stuckapp.R.id.recycler_view),
                CategoriesAdapter(this) as RecyclerView.Adapter<RecyclerView.ViewHolder>
            )
            builder.setView(containerView)
                .setNegativeButton(android.R.string.cancel) { _, _ ->
                    dialog?.cancel()
                }
                .setTitle("Select a category")
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    companion object {
        val TAG = CategoriesListDialog::class.java.simpleName
    }
}