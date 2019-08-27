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

        val sortCategories = listOf(
            "popular",
            "text_only",
            "image_only",
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
            "education")
    }
}

class PrivacyChoice {
    companion object {
        val privacyChoices = listOf("public", "anonymous")
    }
}