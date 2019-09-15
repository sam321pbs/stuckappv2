package com.sammengistu.stuckapp.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.events.SaveDraftEvent
import com.sammengistu.stuckapp.fragments.NewPostTypeFragment
import kotlinx.android.synthetic.main.toolbar_layout.*
import org.greenrobot.eventbus.EventBus

class NewPostActivity : LoggedInActivity() {

    private lateinit var draftIcon: MenuItem

    override fun getLayoutId(): Int {
        return R.layout.activity_base
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        addFragment(NewPostTypeFragment())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.new_post_menu, menu)
        draftIcon = menu.findItem(R.id.save_draft)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.save_draft) {
            EventBus.getDefault().post(SaveDraftEvent())
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    fun showDraftIcon() {
        draftIcon.isVisible = true
    }

    fun hideDraftIcon() {
        draftIcon.isVisible = false
    }
}