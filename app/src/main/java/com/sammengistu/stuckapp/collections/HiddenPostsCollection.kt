package com.sammengistu.stuckapp.collections

class HiddenPostsCollection {
    companion object {
        private val postRefs = HashSet<String>()

        fun addRef(ref: String) = postRefs.add(ref)

        fun removeRef(ref: String) = postRefs.remove(ref)

        fun containesRef(ref: String) = postRefs.contains(ref)
    }
}