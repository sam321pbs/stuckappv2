package com.sammengistu.stuckapp.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.events.SaveDraftEvent
import com.sammengistu.stuckapp.fragments.NewImagePostFragment
import com.sammengistu.stuckapp.fragments.NewPostTypeFragment
import com.sammengistu.stuckapp.fragments.NewTextPostFragment
import com.sammengistu.stuckfirebase.constants.PostType
import kotlinx.android.synthetic.main.toolbar_layout.*
import org.greenrobot.eventbus.EventBus

class NewPostActivity : LoggedInActivity() {

    private var draftIcon: MenuItem? = null
    private var hideDraftIcon = false

    override fun getLayoutId(): Int {
        return R.layout.activity_base
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)

        val postType = intent.getStringExtra(EXTRA_POST_TYPE)
        val postId = intent.getLongExtra(EXTRA_POST_ID, -1L)
        if (postType != null) {
            if (postType == PostType.TEXT.toString()) {
                addFragment(NewTextPostFragment.newInstance(postId))
            } else {
                addFragment(NewImagePostFragment.newInstance(postId))
            }
        } else {
            addFragment(NewPostTypeFragment())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.new_post_menu, menu)
        draftIcon = menu.findItem(R.id.save_draft)
        if (hideDraftIcon) hideDraftIcon() else showDraftIcon()
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

    override fun onFragmentChanged(topFragment: Fragment) {
        super.onFragmentChanged(topFragment)
        if (topFragment is NewPostTypeFragment) {
            hideDraftIcon()
        } else {
            showDraftIcon()
        }
    }

    fun showDraftIcon() {
        if (draftIcon == null) {
            hideDraftIcon = false
        }
        draftIcon?.isVisible = true
    }

    fun hideDraftIcon() {
        if (draftIcon == null) {
            hideDraftIcon = true
        }
        draftIcon?.isVisible = false
    }

    companion object {
        const val EXTRA_POST_TYPE = "extra_post_type"
        const val EXTRA_POST_ID = "extra_post_id"
    }
}