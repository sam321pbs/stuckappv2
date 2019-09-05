package com.sammengistu.stuckapp

import android.content.Context
import android.widget.Toast
import com.sammengistu.stuckapp.constants.PrivacyOptions
import com.sammengistu.stuckapp.data.AppDatabase
import com.sammengistu.stuckapp.data.DraftPost
import com.sammengistu.stuckapp.data.PostRepository
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class DummyDataStuck {
    companion object {
        val userId = "Sam_1"

        val DUMMY_DATA = listOf(
            DraftPost(
                0,
                userId,
                "sam321pbs",
                "15min ago",
                "avatar_loc",
                "Who is the best player in the NBA?",
                12,
                23,
                PrivacyOptions.PUBLIC.toString(),
                "Sports",
                "text",
                "", "",
                "Lebron James", "Steph Curry", "James Harden", "Lonzo Ball",
                30, 23, 14, 2
            ),
            DraftPost(
                0,
                userId,
                "sam321pbs",
                "30min ago",
                "avatar_loc",
                "Should I have my honey in Jamaica or Bali?",
                0,
                4,
                PrivacyOptions.PUBLIC.toString(),
                "Travel",
                "text",
                "", "",
                "Jamaica", "Bali", "", "",
                30, 23, 0, 0
            ),
            DraftPost(
                0,
                userId,
                "sam321pbs",
                "1 hour ago",
                "avatar_loc",
                "Which Avengers movies did you like best?",
                41,
                55,
                PrivacyOptions.PUBLIC.toString(),
                "Entertainment",
                "text",
                "", "",
                "The Avengers", "Avengers: Infinity War", "Avengers: Endgame", "",
                65, 12, 32, 0
            ),
            DraftPost(
                0,
                userId,
                "sam321pbs",
                "1 day ago",
                "avatar_loc",
                "Which phone among these is the best in the market?",
                50,
                72,
                PrivacyOptions.PUBLIC.toString(),
                "Tech",
                "text",
                "", "",
                "iPhone XS", "Google Pixel 3", "Samsung S10", "",
                45, 36, 36, 0
            ),
            DraftPost(
                0,
                userId,
                "sam321pbs",
                "2 day ago",
                "avatar_loc",
                "Which of these systems would you buy?",
                23,
                72,
                PrivacyOptions.PUBLIC.toString(),
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