package com.sammengistu.stuckapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.sammengistu.stuckapp.R

class UserProfileOptionsAdapter(
    private val context: Context,
    private val navController: NavController
) : RecyclerView.Adapter<UserProfileOptionsAdapter.OptionViewHolder>() {

    private val menuOptions = listOf(
        "Profile",
        "Stats",
        "Your Posts",
        "Drafts",
        "Settings"
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        OptionViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.item_profile_option, parent, false))

    override fun getItemCount() = menuOptions.size

    override fun onBindViewHolder(holder: OptionViewHolder, position: Int) {
        holder.textView.text = menuOptions[position]
        holder.itemView.setOnClickListener {
            when (position) {
                0 -> navController.navigate(R.id.profileFragment)
                1 -> navController.navigate(R.id.statsFragment)
                2 -> navController.navigate(R.id.userPostsListFragment)
                3 -> navController.navigate(R.id.draftListFragment)
                4 -> navController.navigate(R.id.settingsFragment)
            }
        }
    }

    class OptionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView = view.findViewById<TextView>(R.id.textView)
    }
}