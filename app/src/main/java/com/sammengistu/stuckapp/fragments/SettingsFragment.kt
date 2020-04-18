package com.sammengistu.stuckapp.fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.collections.UserStarredCollection
import com.sammengistu.stuckapp.collections.UserVotesCollection
import com.sammengistu.stuckfirebase.repositories.UserRepository
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : BaseFragment() {

    private lateinit var notificationTView: TextView
    private lateinit var deleteAccountTView: TextView
    private lateinit var logoutTView: TextView

    override fun getLayoutId() = R.layout.fragment_settings
    override fun getFragmentTag(): String = TAG

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notificationTView = notifications_setting
        deleteAccountTView = delete_account
        logoutTView = logout
        notificationTView.setOnClickListener {
            val action = SettingsFragmentDirections.actionSettingsFragmentToNotificationSettingsFragment()
            findNavController().navigate(action)
        }
        deleteAccountTView.setOnClickListener { showDeleteAccountAlertDialog() }
        logoutTView.setOnClickListener {
            UserVotesCollection.getInstance(context!!).clearList()
            UserStarredCollection.getInstance(context!!).clearList()
            UserRepository.logUserOut()
        }
    }

    private fun showDeleteAccountAlertDialog() {
        AlertDialog.Builder(activity!!)
            .setTitle("Delete Account")
            .setMessage("Are you sure you want to delete your account?")
            .setNegativeButton("Delete") { _, _ ->
                progress_bar.visibility = View.VISIBLE
                UserRepository.deleteUserAccount(activity!!)
            }
            .setPositiveButton("Cancel", null)
            .show()
    }

    companion object {
        const val TAG = "SettingsFragment"
    }
}