package com.sammengistu.stuckfirebase.constants

enum class AnalyticEventType {
    CLICK,
    CREATE;

    override fun toString(): String {
        return super.toString().toLowerCase()
    }
}