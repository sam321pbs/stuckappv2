package com.sammengistu.stuckapp

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.sammengistu.stuckapp.data.AppDatabase
import com.sammengistu.stuckapp.data.Post
import com.sammengistu.stuckapp.data.PostRepository
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            insertDummyData()
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    private fun insertDummyData() {
        doAsync {
            //Execute all the lon running tasks here
            var post = Post(
                "1",
                "Sam",
                "Sam",
                "Sam",
                "Sam",
                "Sam",
                1,
                1,
                "Sam",
                "Sam",
                "Sam",
                "Sam",
                "Sam", "Sam", "Sam", "Sam",
                1,1,1,1)
            PostRepository.getInstance(AppDatabase.getInstance(this@MainActivity.applicationContext).postsDao()).insertPost(post)

            uiThread {
                Snackbar.make(parent_view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
