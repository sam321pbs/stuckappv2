package com.sammengistu.stuckapp.activities

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp
import com.sammengistu.stuckapp.OnItemClickListener
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.UserHelper
import com.sammengistu.stuckapp.UserVotesCollection
import com.sammengistu.stuckapp.constants.*
import com.sammengistu.stuckapp.events.UserUpdatedEvent
import com.sammengistu.stuckapp.fragments.*
import com.sammengistu.stuckapp.views.AvatarView
import com.sammengistu.stuckapp.views.StuckNavigationBar
import com.sammengistu.stuckfirebase.data.UserModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class MainActivity : LoggedInActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var navigationBar: StuckNavigationBar
    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var navigationView: NavigationView

    @Subscribe
    fun onUserProfileUpdated(event: UserUpdatedEvent) {
        UserHelper.getCurrentUser{setupNavHeader(it)}
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        FirebaseApp.initializeApp(this)
        navigationBar = stuck_navigation_bar
        navigationBar.onItemClicked = getOnNavItemClicked()
        setupDrawer()
        UserVotesCollection.loadUserVotes(getFirebaseUserId())
        addFragment(HomeListFragment())
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
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
            R.id.action_profile -> addFragment(ProfileFragment.newInstance(false))
            R.id.action_stats -> addFragment(StatsFragment())
            R.id.action_drafts -> addFragment(DraftListFragment())
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
        drawer.closeDrawers()
        return true
    }

    fun hideNavBar() {
        navigationBar.visibility = GONE
    }

    fun showNavBar() {
        navigationBar.visibility = VISIBLE
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

        navigationView = nav_view
        navigationView.setNavigationItemSelectedListener(this)

        UserHelper.getCurrentUser{setupNavHeader(it)}
    }

    private fun setupNavHeader(user: UserModel?) {
        if (user != null) {
            val parentView = navigationView.getHeaderView(0)
            parentView.findViewById<TextView>(R.id.nav_username).text = user.username
            parentView.findViewById<AvatarView>(R.id.avatar_view).loadImage(user.avatar)
        }
    }

    private fun getOnNavItemClicked(): OnItemClickListener<Int> {
        return object : OnItemClickListener<Int> {
            override fun onItemClicked(item: Int) {
                when (item) {
                    HOME -> addFragment(HomeListFragment())
                    CATEGORIES -> addFragment(CategoriesFragment())
                    FAVORITE -> addFragment(FavoritesListFragment())
                    ME -> addFragment(UserPostsListFragment())
                    CREATE -> {
                        val intentNewPost = Intent(this@MainActivity, NewPostActivity::class.java)
                        startActivity(intentNewPost)
                    }
                }
            }
        }
    }
}
