package com.sammengistu.stuckapp.activities

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp
import com.sammengistu.stuckapp.DummyDataStuck
import com.sammengistu.stuckapp.OnItemClickListener
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.UserVotesCollection
import com.sammengistu.stuckapp.constants.*
import com.sammengistu.stuckapp.fragments.CategoriesFragment
import com.sammengistu.stuckapp.fragments.PostsListFragment
import com.sammengistu.stuckapp.fragments.PostsListFragment.Companion.EXTRA_FAVORITES
import com.sammengistu.stuckapp.fragments.PostsListFragment.Companion.EXTRA_USER
import com.sammengistu.stuckapp.views.StuckNavigationBar
import com.sammengistu.stuckfirebase.data.UserModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : LoggedInActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var navigationBar: StuckNavigationBar
    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        addFragment(PostsListFragment(), TITLE_HOME)
        supportActionBar?.title = TITLE_HOME
        FirebaseApp.initializeApp(this)
        setupFab()
        navigationBar = stuck_navigation_bar
        navigationBar.onItemClicked = getOnNavItemClicked()
        setupDrawer()

        UserVotesCollection.loadUserVotes(getFirebaseUserId())
    }

    private fun setupDrawer() {
        drawer = drawer_layout
        toggle = ActionBarDrawerToggle(
            this,
            drawer,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp)

        val navigationView: NavigationView = nav_view
        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toggle.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_profile -> Toast.makeText(
                this,
                "Clicked Profile",
                Toast.LENGTH_SHORT
            ).show()
            R.id.action_stats -> Toast.makeText(this, "Clicked Stats", Toast.LENGTH_SHORT).show()
            R.id.action_drafts -> Toast.makeText(this, "Clicked Drafts", Toast.LENGTH_SHORT).show()
            R.id.action_favorite -> Toast.makeText(
                this,
                "Clicked Favorites",
                Toast.LENGTH_SHORT
            ).show()
            R.id.action_settings -> Toast.makeText(
                this,
                "Clicked Settings",
                Toast.LENGTH_SHORT
            ).show()
        }
        return true
    }

    fun hideNavBar() {
        navigationBar.visibility = GONE
    }

    fun showNavBar() {
        navigationBar.visibility = VISIBLE
    }

    private fun getOnNavItemClicked(): OnItemClickListener<Int> {
        return object : OnItemClickListener<Int> {
            override fun onItemClicked(item: Int) {
                when (item) {
                    HOME -> {
                        addFragment(PostsListFragment(), TITLE_HOME)
                    }
                    CATEGORIES -> {
                        addFragment(CategoriesFragment(), TITLE_CATEGORIES)
                    }
                    CREATE -> {
                        val intentNewPost = Intent(this@MainActivity, NewPostActivity::class.java)
                        startActivity(intentNewPost)
                    }
                    FAVORITE -> {
                        addFragment(PostsListFragment.newInstance(EXTRA_FAVORITES, "true"), TITLE_FAVORITE)
                    }
                    ME -> {
                        addFragment(
                            PostsListFragment.newInstance(
                                EXTRA_USER,
                                getFirebaseUserId()
                            ),
                            TITLE_ME
                        )
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
        fab.visibility = GONE
    }

    companion object {
        const val TITLE_HOME = "Home"
        const val TITLE_CATEGORIES = "Categories"
        const val TITLE_FAVORITE = "Favorite"
        const val TITLE_ME = "My Posts"

        var currentUser: UserModel? = null
    }
}
