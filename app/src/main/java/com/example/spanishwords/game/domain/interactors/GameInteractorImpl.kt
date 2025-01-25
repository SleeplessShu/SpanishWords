package com.example.spanishwords.game.domain.interactors

import android.util.Log
import com.example.spanishwords.game.data.WordEntity
import com.example.spanishwords.game.domain.api.DatabaseInteractor
import com.example.spanishwords.game.domain.api.GameInteractor
import com.example.spanishwords.game.domain.models.LanguageLevel
import com.example.spanishwords.game.domain.models.WordCategory
import com.example.spanishwords.game.presentation.models.Language
import com.example.spanishwords.game.presentation.models.Word
import kotlin.random.Random

class GameInteractorImpl(private val repository: DatabaseInteractor) : GameInteractor {

    override suspend fun getWordPairs(
        language1: Language,
        language2: Language,
        level: LanguageLevel,
        difficultLevel: Int,
        category: WordCategory
    ): List<Pair<Word, Word>> {
        val wordsList = repository.getWordsPack(language1, language2, level, difficultLevel, category)
        Log.d("GameInteractorTesting", "getWordPairs: ${wordsList}")
        return wordsList.map { wordEntity ->
            toWordPair(wordEntity, language1, language2)
        }
    }

    override fun shufflePairs(input: List<Pair<Word, Word>>): List<Pair<Word, Word>> {
        if (input.size <= 1) return input
        val secondWords = input.map { it.second }.shuffled(Random(System.currentTimeMillis()))
        return input.mapIndexed { index, pair ->
            pair.first to secondWords[index]
        }
    }

    override fun toWordPair(wordEntity: WordEntity, original: Language, translate: Language): Pair<Word, Word> {
        val word1 = getWordForLanguage(wordEntity, original)
        val word2 = getWordForLanguage(wordEntity, translate)
        return Pair(word1, word2)
    }

    override fun getWordForLanguage(entity: WordEntity, lang: Language): Word {
        return when (lang) {
            Language.ENGLISH -> Word(entity.id, entity.english, Language.ENGLISH)
            Language.SPANISH -> Word(entity.id, entity.spanish, Language.SPANISH)
            Language.RUSSIAN -> Word(entity.id, entity.russian, Language.RUSSIAN)
            Language.FRENCH -> Word(entity.id, entity.french, Language.FRENCH)
            Language.GERMAN -> Word(entity.id, entity.german, Language.GERMAN)
        }
    }
}
