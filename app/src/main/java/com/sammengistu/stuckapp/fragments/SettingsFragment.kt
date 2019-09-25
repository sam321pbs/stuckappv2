package com.sammengistu.stuckapp.fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckfirebase.UserHelper
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : BaseFragment() {

    lateinit var notificationItem: TextView
    lateinit var deleteAccountItem: TextView
    lateinit var logoutItem: TextView

    override fun getLayoutId() = R.layout.fragment_settings

    override fun getFragmentTag() = TAG

    override fun getFragmentTitle() = TITLE

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notificationItem = notifications_setting
        deleteAccountItem = delete_account
        logoutItem = logout
        notificationItem.setOnClickListener { addFragment(NotificationSettings()) }
        deleteAccountItem.setOnClickListener { showDeleteAccountAlertDialog() }
        logoutItem.setOnClickListener { UserHelper.logUserOut() }
    }

    private fun showDeleteAccountAlertDialog() {
        AlertDialog.Builder(activity!!)
            .setTitle("Delete Account")
            .setMessage("Are you sure you want to delete your account?")
            .setNegativeButton("Delete") { _, _ ->
                UserHelper.deleteUserAccount(activity!!)
            }
            .setPositiveButton("Cancel", null)
            .show()
    }

    companion object {
        val TAG = SettingsFragment::class.java.simpleName
        const val TITLE = "Settings"
    }
}