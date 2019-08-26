package com.sammengistu.stuckapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.bottomsheet.BottomSheetHelper
import com.sammengistu.stuckapp.bottomsheet.BottomSheetMenu
import com.sammengistu.stuckapp.data.Post
import com.sammengistu.stuckapp.fragments.PostsListFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_sheet_post_view.*

class MainActivity : BaseActivity(), BottomSheetMenu {

    lateinit var mBottomSheetHelper: BottomSheetHelper

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        addFragment(PostsListFragment())
        setupFab()
        mBottomSheetHelper = BottomSheetHelper(this, bottom_sheet)
        hideMenu()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun showMenu(post: Post) {
        mBottomSheetHelper.showMenu(post)
    }

    override fun hideMenu() {
        mBottomSheetHelper.hideMenu()
    }

    private fun setupFab() {
        fab.setOnClickListener { view ->
            val intentNewPost = Intent(this@MainActivity, NewPostActivity::class.java)
            startActivity(intentNewPost)
        }
        fab.visibility = View.VISIBLE
    }
}
