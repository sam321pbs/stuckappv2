package com.sammengistu.stuckapp.utils

class StringUtils {
    companion object {
        fun capitilizeFirstLetter(word: String) : String {
            var updatedWord = word
            if (updatedWord.isNotBlank()) {
                val firstLetter = updatedWord[0].toString().toUpperCase()
                updatedWord = firstLetter + updatedWord.substring(1, updatedWord.length)
                return updatedWord
            }
            return updatedWord
        }
    }
}