package com.sammengistu.stuckapp.constants

import com.sammengistu.stuckapp.utils.StringUtils

enum class ReportReasons {
    INAPPROPRIATE,
    SPAM,
    OFFENSIVE;

    override fun toString() = super.toString().toLowerCase()

    companion object {
        fun getDisplayNames(): List<String> {
            val arrayList = ArrayList<String>()
            arrayList.add(StringUtils.capitilizeFirstLetter(INAPPROPRIATE.toString()))
            arrayList.add(StringUtils.capitilizeFirstLetter(SPAM.toString()))
            arrayList.add(StringUtils.capitilizeFirstLetter(OFFENSIVE.toString()))
            return arrayList
        }
    }
}