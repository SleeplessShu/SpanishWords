package com.example.spanishwords.game.domain.api

import com.example.spanishwords.game.data.WordEntity
import com.example.spanishwords.game.domain.models.LanguageLevel
import com.example.spanishwords.game.domain.models.WordCategory
import com.example.spanishwords.game.presentation.models.Language
import com.example.spanishwords.game.presentation.models.Word

interface GameInteractor {

    suspend fun getWordPairs(
        language1: Language,
        language2: Language,
        level: LanguageLevel,
        difficultLevel: Int,
        category: WordCategory
    ): List<Pair<Word, Word>>

    fun shufflePairs(input: List<Pair<Word, Word>>): List<Pair<Word, Word>>

    fun toWordPair(
        wordEntity: WordEntity,
        original: Language,
        translate: Language
    ): Pair<Word, Word>

    fun getWordForLanguage(entity: WordEntity, lang: Language): Word
}