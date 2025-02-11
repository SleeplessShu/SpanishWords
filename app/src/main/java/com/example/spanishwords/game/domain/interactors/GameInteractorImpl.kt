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
        // egusev level is unused (because it's unused in repository too)
        level: LanguageLevel,
        difficultLevel: Int,
        category: WordCategory
    ): List<Pair<Word, Word>> {
        // egusev seems fishy that this funciton is suspend, but I don't know how to fix or if
        //  this needs fixing
        val wordsList = repository.getWordsPack(language1, language2, level, difficultLevel, category)
        Log.d("GameInteractorTesting", "getWordPairs: ${wordsList}")
        return wordsList.map { wordEntity ->
            toWordPair(wordEntity, language1, language2)
        }
    }

    override fun shufflePairs(input: List<Pair<Word, Word>>): List<Pair<Word, Word>> {
        if (input.size <= 1) return input
        // egusev can't you do return input.shuffled(...)? Gives the same result
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
        // egusev this seems a bit cleaner, can also move the when(lang) logic to extension fun
        //  of WordEntity, so you'd do something like
        //  return Word(entity.id, entity.getTranslation(lang), lang)
        //  and .getTranslation(lang) would do the when(lang) ... logic
        val mappedTranslation = when(lang) {
            Language.ENGLISH -> entity.english
            Language.SPANISH -> entity.spanish
            Language.RUSSIAN -> entity.russian
            Language.FRENCH -> entity.french
            Language.GERMAN -> entity.german
        }
        return Word(entity.id, mappedTranslation, lang)
    }
}
