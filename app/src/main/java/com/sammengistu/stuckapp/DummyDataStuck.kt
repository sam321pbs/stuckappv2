package com.sammengistu.stuckapp

import android.content.Context
import android.widget.Toast
import com.sammengistu.stuckapp.data.AppDatabase
import com.sammengistu.stuckapp.data.Post
import com.sammengistu.stuckapp.data.PostRepository
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class DummyDataStuck {
    companion object {
        val DUMMY_DATA = listOf(
            Post(
                0,
                "Sam_1",
                "sam321pbs",
                "15min ago",
                "avatar_loc",
                "Who is the best player in the NBA?",
                12,
                23,
                "Sports",
                "text",
                "", "",
                "Lebron James", "Steph Curry", "James Harden", "Lonzo Ball",
                30, 23, 14, 2
            ),
            Post(
                0,
                "Sam_1",
                "sam321pbs",
                "30min ago",
                "avatar_loc",
                "Should I have my honey in Jamaica or Bali?",
                0,
                4,
                "Travel",
                "text",
                "", "",
                "Jamaica", "Bali", "", "",
                30, 23, 0, 0
            ),
            Post(
                0,
                "Sam_1",
                "sam321pbs",
                "1 hour ago",
                "avatar_loc",
                "Which Avengers movies did you like best?",
                41,
                55,
                "Entertainment",
                "text",
                "", "",
                "The Avengers", "Avengers: Infinity War", "Avengers: Endgame", "",
                65, 12, 32, 0
            ),
            Post(
                0,
                "Sam_1",
                "sam321pbs",
                "1 day ago",
                "avatar_loc",
                "Which phone among these is the best in the market?",
                50,
                72,
                "Tech",
                "text",
                "", "",
                "iPhone XS", "Google Pixel 3", "Samsung S10", "",
                45, 36, 36, 0
            ),
            Post(
                0,
                "Sam_1",
                "sam321pbs",
                "2 day ago",
                "avatar_loc",
                "Which of these systems would you buy?",
                23,
                72,
                "Tech",
                "text",
                "", "",
                "Playstation 4", "Nintendo Switch", "Xbox One X", "",
                67, 55, 36, 0
            )
        )

        fun insertDummyData(context: Context) {
            doAsync {
                //Execute all the lon running tasks here
                for (post in DummyDataStuck.DUMMY_DATA)
                    PostRepository.getInstance(AppDatabase.getInstance(context).postsDao()).insertPost(post)

                uiThread {
                   Toast.makeText(context, "Dummy Data added", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}