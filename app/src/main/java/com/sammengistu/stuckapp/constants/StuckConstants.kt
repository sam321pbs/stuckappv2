package com.sammengistu.stuckapp.constants

const val DATABASE_NAME = "stuck-db"

class Category {
    companion object {
        val categories = listOf(
            "general",
            "opinion",
            "business",
            "sports",
            "technology",
            "entertainment",
            "travel",
            "science",
            "food",
            "politics",
            "history",
            "style",
            "lifestyle",
            "education"
        )

        val sortCategories = listOf("popular", categories)
    }
}

class PrivacyChoice {
    companion object {
        val privacyChoices = listOf("public", "anonymous")
    }
}