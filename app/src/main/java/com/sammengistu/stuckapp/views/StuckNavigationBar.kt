package com.sammengistu.stuckapp.views

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import com.sammengistu.stuckapp.OnItemClickListener
import com.sammengistu.stuckapp.R
import com.sammengistu.stuckapp.constants.*

class StuckNavigationBar(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    lateinit var onItemClicked: OnItemClickListener<Int>

    init {
        buildChildren()
    }

    private fun buildChildren() {
        val homeView = VerticalIconToTextView(context, null)
        val categoriesView = VerticalIconToTextView(context, null)
        val createView = VerticalIconToTextView(context, null)
        val favView = VerticalIconToTextView(context, null)
        val myPostsView = VerticalIconToTextView(context, null)

        homeView.setText("Home")
        categoriesView.setText("Categories")
        createView.setText("Create")
        favView.setText("Favorite")
        myPostsView.setText("Me")

        homeView.setIcon(R.drawable.ic_home_blue_400_24dp)
        categoriesView.setIcon(R.drawable.ic_subject_blue_400_24dp)
        createView.setIcon(R.drawable.ic_create_blue_400_24dp)
        favView.setIcon(R.drawable.ic_star_blue_400_24dp)
        myPostsView.setIcon(R.drawable.ic_person_blue_400_24dp)

        val params = LayoutParams(0, LayoutParams.WRAP_CONTENT)
        params.gravity = Gravity.CENTER
        params.weight = 1f

        homeView.layoutParams = params
        categoriesView.layoutParams = params
        createView.layoutParams = params
        favView.layoutParams = params
        myPostsView.layoutParams = params

        homeView.setOnClickListener { onItemClicked.onItemClicked(HOME) }
        categoriesView.setOnClickListener { onItemClicked.onItemClicked(CATEGORIES) }
        createView.setOnClickListener { onItemClicked.onItemClicked(CREATE) }
        favView.setOnClickListener { onItemClicked.onItemClicked(FAVORITE) }
        myPostsView.setOnClickListener { onItemClicked.onItemClicked(ME) }

        addView(homeView)
        addView(categoriesView)
        addView(createView)
        addView(favView)
        addView(myPostsView)
    }
}