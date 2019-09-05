package com.sammengistu.stuckapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import com.google.firebase.FirebaseApp
import com.sammengistu.stuckapp.DummyDataStuck
import com.sammengistu.stuckapp.OnItemClickListener
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.constants.CATEGORIES
import com.sammengistu.stuckapp.constants.CREATE
import com.sammengistu.stuckapp.constants.FAVORITE
import com.sammengistu.stuckapp.constants.HOME
import com.sammengistu.stuckapp.fragments.CategoriesFragment
import com.sammengistu.stuckapp.fragments.PostsListFragment
import com.sammengistu.stuckapp.views.StuckNavigationBar
import com.sammengistu.stuckapp.UserVotesCollection
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    private lateinit var mNavigationBar: StuckNavigationBar
    lateinit var mUserVotesCollection: UserVotesCollection

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

        UserVotesCollection.loadUserVotes(DummyDataStuck.ownerId)
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

    fun hideNavBar() {
        mNavigationBar.visibility = GONE
    }

    fun showNavBar() {
        mNavigationBar.visibility = VISIBLE
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
                    FAVORITE -> addFragment(PostsListFragment.newInstanceFavorites())
                }
            }
        }
    }

    private fun setupFab() {
        fab.setOnClickListener { view ->
            val intentNewPost = Intent(this@MainActivity, NewPostActivity::class.java)
            startActivity(intentNewPost)
        }
        fab.visibility = GONE
    }
}
