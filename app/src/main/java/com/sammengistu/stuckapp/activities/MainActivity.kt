package com.sammengistu.stuckapp.activities

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp
import com.sammengistu.stuckapp.AlarmHelper
import com.sammengistu.stuckapp.AssetImageUtils
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.bottomsheet.BottomSheetHelper
import com.sammengistu.stuckapp.collections.UserStarredCollection
import com.sammengistu.stuckapp.collections.UserVotesCollection
import com.sammengistu.stuckapp.events.ChangeBottomSheetStateEvent
import com.sammengistu.stuckapp.helpers.HiddenItemsHelper
import com.sammengistu.stuckapp.notification.StuckNotificationFactory
import com.sammengistu.stuckapp.setupWithNavController
import com.sammengistu.stuckapp.views.AvatarView
import com.sammengistu.stuckfirebase.access.DeviceTokenAccess
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

class MainActivity : LoggedInActivity() {

    private lateinit var navigationView: NavigationView
    private lateinit var bottomSheetHelper: BottomSheetHelper
    private lateinit var invisibleCover: View
    private lateinit var bottomNavigationView: BottomNavigationView

    private var currentNavController: LiveData<NavController>? = null
    private val appBarNavController : NavController
        get() = findNavController(R.id.nav_host_fragment)

    private lateinit var appBarConfiguration: AppBarConfiguration

    private val userViewModel: UserViewModel by viewModels {
        InjectorUtils.provideUserFactory(this)
    }

    private val onDestinationChangedListener =
        NavController.OnDestinationChangedListener { controller, destination, arguments ->
            val dest: String = try {
                resources.getResourceName(destination.id)
            } catch (e: Resources.NotFoundException) {
                Integer.toString(destination.id)
            }
            if (destination.id == R.id.profileFragment ||
                destination.id == R.id.statsFragment ||
                destination.id == R.id.settingsFragment ||
                destination.id == R.id.newPostTypeFragment ||
                destination.id == R.id.newImagePostFragment ||
                destination.id == R.id.newTextPostFragment ||
                destination.id == R.id.commentsFragment ||
                destination.id == R.id.notificationSettingsFragment
            ) {
                bottomNavigationView.visibility = View.GONE
            } else {
                bottomNavigationView.visibility = View.VISIBLE
            }

            Log.d(TAG, "Navigated to $dest")
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
        UserRepository.getUserInstance(this) { onUserLoaded(it) }

        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.cancelAll()

        navigationView = nav_view
        setupActionBar(appBarNavController)
        setupBottomSheet()
        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        }
        appBarNavController.addOnDestinationChangedListener(onDestinationChangedListener)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        // Now that BottomNavigationBar has restored its instance state
        // and its selectedItemId, we can proceed with setting up the
        // BottomNavigationBar with Navigation
        setupBottomNavigationBar()
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val retValue = super.onCreateOptionsMenu(menu)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        if (navigationView == null) {
            //android needs to know what menu I need
            menuInflater.inflate(R.menu.menu_main, menu)
            return true
        }
        return retValue
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return item!!.onNavDestinationSelected(appBarNavController) || super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp() =
        NavigationUI.navigateUp(appBarNavController, drawer_layout) || super.onSupportNavigateUp()

    override fun onBackPressed() {
        //the code is beautiful enough without comments
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun goHome() { bottomNavigationView.selectedItemId = R.id.nav_home }

    private fun setupActionBar(navController: NavController) {
        setupNavigation(navController)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    private fun setupNavigation(navController: NavController) {
        val sideNavView = findViewById<NavigationView>(R.id.nav_view)
        sideNavView?.setupWithNavController(navController)
        val drawerLayout: DrawerLayout? = findViewById(R.id.drawer_layout)

        val homeDestinations = setOf(
            R.id.homeListFragment,
            R.id.categoriesFragment,
            R.id.newPostTypeFragment,
            R.id.favoritesListFragment,
            R.id.userPostsListFragment,
            R.id.profileFragment,
            R.id.statsFragment,
            R.id.draftListFragment,
            R.id.settingsFragment)

        val appBarConfigurationBuilder = AppBarConfiguration.Builder(homeDestinations)
        appBarConfigurationBuilder.setDrawerLayout(drawerLayout)
        appBarConfiguration = appBarConfigurationBuilder.build()
    }

    private fun onUserLoaded(user: UserModel?) {
        if (user != null) {
            UserStarredCollection.getInstance(this)
            UserVotesCollection.getInstance(this)
            DeviceTokenAccess(user.ref).checkTokenExists(this)
            HiddenItemsHelper(user.ref, this)

            userViewModel.userLiveData.observe(this) { user ->
                Log.d(TAG, "Received user db changes")
                if (user != null) {
                    UserRepository.currentUser = user
                    setupNavHeader(user)
                }
            }
            userViewModel.setUserRef(user.ref)
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

    private fun setupBottomSheet() {
        invisibleCover = invisible_view
        invisibleCover.setOnClickListener { hideBottomSheet() }
        bottomSheetHelper = BottomSheetHelper(this, bottom_sheet)
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

    private fun setupNavHeader(user: UserModel?) {
        if (user != null) {
            val parentView = navigationView.getHeaderView(0)
            parentView.findViewById<TextView>(R.id.nav_username).text = user.username
            parentView.findViewById<AvatarView>(R.id.avatar_view).loadImage(user.avatar)
        }
    }

    private fun setupBottomNavigationBar() {
        bottomNavigationView = findViewById(R.id.stuck_navigation_bar)

        val navGraphIds = listOf(
            R.navigation.nav_home,
            R.navigation.nav_categories,
            R.navigation.nav_create,
            R.navigation.nav_favorites,
            R.navigation.nav_me)

        // Setup the bottom navigation view with a list of navigation graphs
        val controller = bottomNavigationView.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_fragment,
            intent = intent
        )

        // Whenever the selected controller changes, setup the action bar.
        controller.observe(this, Observer { navController ->
            navController.addOnDestinationChangedListener(onDestinationChangedListener)
            setupActionBarWithNavController(navController, appBarConfiguration)
        })

        currentNavController = controller
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
