package com.sammengistu.stuckapp.constants

enum class Categories {
    GENERAL,
    OPINION,
    BUSINESS,
    SPORTS,
    TECHNOLOGY,
    ENTERTAINMENT,
    TRAVEL,
    SCIENCE,
    FOOD,
    POLITICS,
    HISTORY,
    STYLE,
    LIFESTYLE,
    EDUCATION,
    POPULAR,
    TEXT_ONLY,
    IMAGE_ONLY;

    override fun toString(): String {
        return super.toString().toLowerCase()
    }

    companion object {
        fun asList(): List<String> {
            return values().map { it.toString() }
        }

        fun asListRemoveSortCategories(): List<String> {
            return values().filter {
                (it != POPULAR && it != TEXT_ONLY && it != IMAGE_ONLY)
            }.map { it.toString() }
        }
    }
}