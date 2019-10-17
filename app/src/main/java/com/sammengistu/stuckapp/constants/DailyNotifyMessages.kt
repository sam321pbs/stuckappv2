package com.sammengistu.stuckapp.constants

import kotlin.random.Random

class DailyNotifyMessages {
    companion object {
        val messages = listOf(
            "New posts have been made on Stuck.",
            "Have something on your mind?",
            "Can't decide breakfast for tomorrow?",
            "New posts! Help some users on Stuck",
            "Interested in traveling between a few places? Get some feedback!"
        )

        fun getRandomMessage() = messages[Random.nextInt(messages.size)]
    }
}