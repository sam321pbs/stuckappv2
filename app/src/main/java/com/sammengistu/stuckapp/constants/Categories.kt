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

    fun asList(): List<String> {
        val list = ArrayList<String>()
        listOf(values().)

    }
}