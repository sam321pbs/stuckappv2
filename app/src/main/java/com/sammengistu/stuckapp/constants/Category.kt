package com.sammengistu.stuckapp.constants

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