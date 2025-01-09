package com.example.spanishwords.game.domain.repositories

import com.example.spanishwords.game.domain.models.Dictionary

interface dictionaryRepository {
    fun getDictionary(level: String, category: String): Dictionary
    fun updateLastSeenDate(date: String)
    fun updateErrorsCounter(id: Int)
    fun updateCorrectCounter(id: Int)
}