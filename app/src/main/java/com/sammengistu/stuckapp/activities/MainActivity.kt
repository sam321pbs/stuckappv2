package com.sammengistu.stuckapp.activities

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp
import com.sammengistu.stuckapp.AlarmHelper
import com.sammengistu.stuckapp.AssetImageUtils
import com.sammengistu.stuckapp.OnItemClickListener
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.bottomsheet.BottomSheetHelper
import com.sammengistu.stuckapp.collections.UserStarredCollection
import com.sammengistu.stuckapp.collections.UserVotesCollection
import com.sammengistu.stuckapp.constants.*
import com.sammengistu.stuckapp.events.ChangeBottomSheetStateEvent
import com.sammengistu.stuckapp.fragments.*
import com.sammengistu.stuckapp.helpers.HiddenItemsHelper
import com.sammengistu.stuckapp.notification.StuckNotificationFactory
import com.sammengistu.stuckapp.views.AvatarView
import com.sammengistu.stuckapp.views.StuckNavigationBar
import com.sammengistu.stuckapp.views.VerticalIconToTextView
import com.sammengistu.stuckfirebase.AnalyticsHelper
import com.sammengistu.stuckfirebase.access.DeviceTokenAccess
import com.sammengistu.stuckfirebase.constants.AnalyticEventType
import com.sammengistu.stuckfirebase.database.InjectorUtils
import com.sammengistu.stuckfirebase.models.PostModel
import com.sammengistu.stuckfirebase.models.UserModel
import com.sammengistu.stuckfirebase.repositories.UserRepository
import com.sammengistu.stuckfirebase.viewmodels.UserViewModel
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

    private val userViewModel: UserViewModel by viewModels {
        InjectorUtils.provideUserFactory(this)
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
        UserRepository.getUserInstance(this) { onUserLoaded(it) }

        bottomSheetHelper = BottomSheetHelper(this, bottom_sheet)
        invisibleCover = invisible_view
        invisibleCover.setOnClickListener { hideBottomSheet() }
        HiddenItemsHelper(this)
        addFragment(HomeListFragment())
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.cancelAll()
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    override fun onResume() {
        super.onResume()
        handleStarterIntent()
        AlarmHelper.cancelDailyNotifier(this)
    }

    override fun onPause() {
        super.onPause()
        AlarmHelper.setDailyNotifier(this)
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
            R.id.action_settings -> addFragment(SettingsFragment())
        }
        drawer.closeDrawers()
        return true
    }

    override fun onFragmentChanged(topFragment: Fragment) {
        super.onFragmentChanged(topFragment)
        when (topFragment) {
            is HomeListFragment -> updateNavBar(navigationBar.homeView)
            is CategoriesPostsFragment -> updateNavBar(navigationBar.categoriesView)
            is CategoriesFragment -> updateNavBar(navigationBar.categoriesView)
            is FavoritesListFragment -> updateNavBar(navigationBar.favView)
            is UserPostsListFragment -> updateNavBar(navigationBar.myPostsView)
            else -> {
                navigationBar.deselectViews()
                navigationBar.visibility = View.GONE
            }
        }
    }

    private fun onUserLoaded(user: UserModel?) {
        if (user != null) {
            UserStarredCollection.getInstance(this)
            UserVotesCollection.getInstance(this)
            DeviceTokenAccess(user.ref).checkTokenExists(this)

            userViewModel.userLiveData.observe(this) { users ->
                Log.d(TAG, "Received user db changes")
                if (!users.isNullOrEmpty()) {
                    UserRepository.currentUser = users[0]
                    setupNavHeader(users[0])
                }
            }
            userViewModel.setUserId(user.userId)
        }
    }

    private fun handleStarterIntent() {
        if (intent != null &&
            intent.extras != null &&
            !intent.getStringExtra(StuckNotificationFactory.EXTRA_POST_REF).isNullOrBlank()) {
            val postRef = intent.getStringExtra(StuckNotificationFactory.EXTRA_POST_REF)
            Log.d(TAG, "Main found ref $postRef")
            intent = null
            val postViewIntent = Intent(this, PostViewActivity::class.java)
            postViewIntent.putExtra(StuckNotificationFactory.EXTRA_POST_REF, postRef)
            startActivity(postViewIntent)
        }
    }

    private fun updateNavBar(view: VerticalIconToTextView) {
        navigationBar.selectView(view)
        navigationBar.visibility = View.VISIBLE
    }

    private fun showBottomSheet(post: PostModel) {
        if (invisibleCover != null) {
            invisibleCover.visibility = View.VISIBLE
        }
        bottomSheetHelper.showMenu(post)
    }

    private fun hideBottomSheet() {
        if (invisibleCover != null) {
            invisibleCover.visibility = View.GONE
        }
        bottomSheetHelper.hideMenu()
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

    companion object {
        val TAG = MainActivity::class.java.simpleName
    }
}
