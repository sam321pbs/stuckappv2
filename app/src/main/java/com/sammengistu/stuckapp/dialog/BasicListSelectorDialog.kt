package com.sammengistu.stuckapp.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.sammengistu.stuckapp.OnItemClickListener
import com.sammengistu.stuckapp.adapters.CategoriesAdapter
import com.sammengistu.stuckapp.helpers.RecyclerViewHelper

abstract class BasicListSelectorDialog : DialogFragment(), OnItemClickListener<String> {

    abstract fun getDialogTitle(): String

    abstract fun getListDataSet(): List<String>

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val containerView =
                inflater.inflate(com.sammengistu.stuckapp.R.layout.basic_list_view, null)

            RecyclerViewHelper.setupWithLinearManager(
                activity!!,
                containerView.findViewById(com.sammengistu.stuckapp.R.id.recycler_view),
                CategoriesAdapter(
                    this,
                    getListDataSet()
                ) as RecyclerView.Adapter<RecyclerView.ViewHolder>
            )
            builder.setView(containerView)
                .setNegativeButton(android.R.string.cancel) { _, _ ->
                    dialog?.cancel()
                }
                .setTitle(getDialogTitle())
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}