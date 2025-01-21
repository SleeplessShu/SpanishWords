package com.example.spanishwords.game.domain.repositories

import com.example.spanishwords.game.data.WordEntity
import com.example.spanishwords.game.domain.models.LanguageLevel
import com.example.spanishwords.game.domain.models.WordCategory
import com.example.spanishwords.game.presentation.models.DifficultLevel
import com.example.spanishwords.game.presentation.models.Language

interface DatabaseRepository {
    suspend fun getWordsPack(
        language1: Language,
        language2: Language,
        level: LanguageLevel,
        difficultLevel: Int,
        category: WordCategory
    ): List<WordEntity>

    suspend fun updateUsedWordsStatistic(wordEntity: WordEntity)
}
