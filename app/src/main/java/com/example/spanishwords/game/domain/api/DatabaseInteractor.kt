package com.example.spanishwords.game.domain.api

import com.example.spanishwords.game.data.WordEntity
import com.example.spanishwords.game.domain.models.LanguageLevel
import com.example.spanishwords.game.domain.models.WordCategory
import com.example.spanishwords.game.presentation.models.DifficultLevel
import com.example.spanishwords.game.presentation.models.Language

interface DatabaseInteractor {
    suspend fun getWordsPack(
        language1: Language,
        language2: Language,
        level: LanguageLevel,
        difficultLevel: DifficultLevel,
        category: WordCategory
    ): List<WordEntity>

    suspend fun updateWord(wordEntity: WordEntity)
}