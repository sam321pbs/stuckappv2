package com.sammengistu.stuckapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.firebase.FirebaseApp
import com.sammengistu.stuckapp.OnItemClickListener
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.bottomsheet.BottomSheetHelper
import com.sammengistu.stuckapp.bottomsheet.BottomSheetMenu
import com.sammengistu.stuckapp.constants.CATEGORIES
import com.sammengistu.stuckapp.constants.CREATE
import com.sammengistu.stuckapp.constants.HOME
import com.sammengistu.stuckapp.fragments.CategoriesFragment
import com.sammengistu.stuckapp.fragments.PostsListFragment
import com.sammengistu.stuckapp.views.StuckNavigationBar
import com.sammengistu.stuckfirebase.data.Post
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_sheet_post_view.*

class MainActivity : BaseActivity(), BottomSheetMenu {

    private lateinit var mBottomSheetHelper: BottomSheetHelper
    private lateinit var mNavigationBar: StuckNavigationBar

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        addFragment(PostsListFragment())
        FirebaseApp.initializeApp(this)
        setupFab()
        mNavigationBar = stuck_navigation_bar
        mNavigationBar.onItemClicked = getOnNavItemClicked()
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

    private fun getOnNavItemClicked(): OnItemClickListener<Int> {
        return object : OnItemClickListener<Int> {
            override fun onItemClicked(item: Int) {
                when(item) {
                    HOME -> addFragment(PostsListFragment())
                    CATEGORIES -> addFragment(CategoriesFragment())
                    CREATE -> {
                        val intentNewPost = Intent(this@MainActivity, NewPostActivity::class.java)
                        startActivity(intentNewPost)
                    }
                }
            }
        }
    }

    private fun setupFab() {
        fab.setOnClickListener { view ->
            val intentNewPost = Intent(this@MainActivity, NewPostActivity::class.java)
            startActivity(intentNewPost)
        }
        fab.visibility = View.GONE
    }
}
