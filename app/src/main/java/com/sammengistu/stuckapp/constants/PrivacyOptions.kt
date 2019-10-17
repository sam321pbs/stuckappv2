package com.sammengistu.stuckapp.constants

enum class PrivacyOptions {
    PUBLIC,
    ANONYMOUS;

    override fun toString(): String {
        return super.toString().toLowerCase()
    }

    companion object {
        fun asList(): List<String> {
            return PrivacyOptions.values().map { it.toString() }
        }
    }
}