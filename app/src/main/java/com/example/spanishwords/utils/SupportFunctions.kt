package com.example.spanishwords.utils

class SupportFunctions {
    fun <T> switchItem(currentItem: T?, items: Array<T>, isNext: Boolean): T {
        val currentIndex = items.indexOf(currentItem).takeIf { it != -1 } ?: 0
        return if (isNext) {
            items[(currentIndex + 1) % items.size]
        } else {
            items[(currentIndex - 1 + items.size) % items.size]
        }
    }
}