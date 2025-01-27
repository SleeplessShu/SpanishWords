package com.example.spanishwords.utils


import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class SupportFunctions {
    fun <T> switchItem(currentItem: T?, items: Array<T>, isNext: Boolean): T {
        val currentIndex = items.indexOf(currentItem).takeIf { it != -1 } ?: 0
        return if (isNext) {
            items[(currentIndex + 1) % items.size]
        } else {
            items[(currentIndex - 1 + items.size) % items.size]
        }
    }

    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }
     fun getScoreAsString(score: Int): String {
        return score.toString().padStart(9, '0')
    }
    fun sortMapByDateDescending(inputMap: Map<String, Int>): Map<String, Int> {
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        return inputMap
            .mapKeys { entry -> LocalDate.parse(entry.key, dateFormatter) } // Преобразуем ключи в LocalDate
            .toSortedMap(compareByDescending { it }) // Сортируем по убыванию дат
            .mapKeys { entry -> entry.key.format(dateFormatter) } // Преобразуем обратно ключи в строковый формат
    }

}