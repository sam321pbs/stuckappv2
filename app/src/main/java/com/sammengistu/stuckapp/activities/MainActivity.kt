package com.sammengistu.stuckapp.activities

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp
import com.sammengistu.stuckapp.AssetImageUtils
import com.sammengistu.stuckapp.OnItemClickListener
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.bottomsheet.BottomSheetHelper
import com.sammengistu.stuckapp.collections.UserStarredCollection
import com.sammengistu.stuckapp.collections.UserVotesCollection
import com.sammengistu.stuckapp.constants.*
import com.sammengistu.stuckapp.events.ChangeBottomSheetStateEvent
import com.sammengistu.stuckapp.events.UserUpdatedEvent
import com.sammengistu.stuckapp.fragments.*
import com.sammengistu.stuckapp.helpers.HiddenItemsHelper
import com.sammengistu.stuckapp.views.AvatarView
import com.sammengistu.stuckapp.views.StuckNavigationBar
import com.sammengistu.stuckfirebase.AnalyticsHelper
import com.sammengistu.stuckfirebase.UserHelper
import com.sammengistu.stuckfirebase.access.DeviceTokenAccess
import com.sammengistu.stuckfirebase.constants.AnalyticEventType
import com.sammengistu.stuckfirebase.models.PostModel
import com.sammengistu.stuckfirebase.models.UserModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_sheet_post_view.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class MainActivity : LoggedInActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var navigationBar: StuckNavigationBar
    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var navigationView: NavigationView
    private lateinit var bottomSheetHelper: BottomSheetHelper
    private lateinit var invisibleCover: View

    @Subscribe
    fun onUserProfileUpdated(event: UserUpdatedEvent) {
        UserHelper.getCurrentUser{setupNavHeader(it)}
    }

    @Subscribe
    fun onChangeBottomSheet(event: ChangeBottomSheetStateEvent) {
        if (event.show && event.post != null) {
            showBottomSheet(event.post)
        } else {
            hideBottomSheet()
        }
    }

    override fun getLayoutId() = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        FirebaseApp.initializeApp(this)
        AssetImageUtils.initListOfImages(this)
        navigationBar = stuck_navigation_bar
        navigationBar.onItemClicked = getOnNavItemClicked()
        setupDrawer()
        UserHelper.getCurrentUser { user ->
            if (user != null) {
                UserStarredCollection.loadUserStars(user.ref)
                UserVotesCollection.loadUserVotes(user.userId)
                DeviceTokenAccess(user.ref).checkTokenExists(this)
            }
        }

        bottomSheetHelper = BottomSheetHelper(this, bottom_sheet)
        invisibleCover = invisible_view
        invisibleCover.setOnClickListener { hideBottomSheet() }
        HiddenItemsHelper(this)
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
            R.id.action_home -> addFragment(HomeListFragment())
            R.id.action_profile -> addFragment(ProfileFragment.newInstance(false))
            R.id.action_stats -> {
                AnalyticsHelper.postSelectEvent(
                    this,
                    AnalyticEventType.CLICK,
                    "view_stats_fragment"
                )
                addFragment(StatsFragment())
            }
            R.id.action_drafts -> {
                AnalyticsHelper.postSelectEvent(
                    this,
                    AnalyticEventType.CLICK,
                    "view_drafts_fragment"
                )
                addFragment(DraftListFragment())
            }
//            R.id.action_favorite -> Toast.makeText(
//                this,
//                "Clicked Favorites",
//                Toast.LENGTH_SHORT
//            ).show()
            R.id.action_settings -> addFragment(SettingsFragment())
        }
        drawer.closeDrawers()
        return true
    }

    fun showBottomSheet(post: PostModel) {
        if (invisibleCover != null) {
            invisibleCover.visibility = View.VISIBLE
        }
        bottomSheetHelper.showMenu(post)
    }

    fun hideBottomSheet() {
        if (invisibleCover != null) {
            invisibleCover.visibility = View.GONE
        }
        bottomSheetHelper.hideMenu()
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
