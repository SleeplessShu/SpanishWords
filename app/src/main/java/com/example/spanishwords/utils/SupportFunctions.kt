package com.example.spanishwords.utils


import com.example.spanishwords.game.presentation.models.DifficultLevel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.SortedMap

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
    fun sortMapByDateDescending(inputMap: Map<String, Int>): SortedMap<String, Int> {
//        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        return inputMap
            // egusev yyyy-MM-dd format should be sortable as a string without formatting
            //  to LocalDate
            //  (1234-01-23 < 2001-01-10 < 2001-01-11 < 2020-10-31 < ... etc)
            //  So it seems that it can be replace to just `toSortedMap`
//            .mapKeys { entry -> LocalDate.parse(entry.key, dateFormatter) } // Преобразуем ключи в LocalDate
            .toSortedMap(compareByDescending { it }) // Сортируем по убыванию дат
//            .mapKeys { entry -> entry.key.format(dateFormatter) } // Преобразуем обратно ключи в строковый формат

            //  By the way, should this even be a map? It's not usually expected that a Map is sorted
            //  maybe can explicitly return SortedMap if you really need a map, or just use a List
            //  or a SortedSet (not sure, maybe map is needed in the business logic)
    }

    // egusev can just expand enum to have an Int field I think?
     fun getGameDifficult(difficultLevel: DifficultLevel): Int {
        return when (difficultLevel) {
            DifficultLevel.EASY -> 18
            DifficultLevel.MEDIUM -> 24
            DifficultLevel.HARD -> 48
            DifficultLevel.EXPERT -> 96
            DifficultLevel.SURVIVAL -> 192
        }
    }

    // egusev same as getGameDifficult(y)
     fun getLivesCount(difficultLevel: DifficultLevel): Int {
        return when (difficultLevel) {
            DifficultLevel.EASY -> 3
            DifficultLevel.MEDIUM -> 3
            DifficultLevel.HARD -> 2
            DifficultLevel.EXPERT -> 1
            DifficultLevel.SURVIVAL -> 1
        }
    }
}