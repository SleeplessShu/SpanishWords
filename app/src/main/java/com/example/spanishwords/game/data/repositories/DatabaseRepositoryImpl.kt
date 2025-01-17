package com.example.spanishwords.game.data.repositories

import com.example.spanishwords.game.data.WordEntity
import com.example.spanishwords.game.data.database.WordDao
import com.example.spanishwords.game.domain.models.LanguageLevel
import com.example.spanishwords.game.domain.models.WordCategory
import com.example.spanishwords.game.domain.repositories.DatabaseRepository
import com.example.spanishwords.game.presentation.models.DifficultLevel
import com.example.spanishwords.game.presentation.models.Language

class DatabaseRepositoryImpl(private val wordDao: WordDao) : DatabaseRepository {

    override suspend fun getWordsPack(
        language1: Language,
        language2: Language,
        level: LanguageLevel,
        difficultLevel: DifficultLevel,
        category: WordCategory
    ): List<WordEntity> {
        var dataBaseResponse = emptyList<WordEntity>()
        if (category == WordCategory.RANDOM) { val wordsNeeded = getDifficultyGrade(difficultLevel)
           dataBaseResponse = getRandom(wordsNeeded)
        } else {
            dataBaseResponse = wordDao.getWordsByCategory(category)
            dataBaseResponse = adaptForConditions(dataBaseResponse, difficultLevel)
        }
        return dataBaseResponse
    }

    suspend fun getRandom(wordsNeeded: Int): List<WordEntity> {
        val additionalWords = wordDao.getRandom(wordsNeeded)
        return additionalWords
    }

    override suspend fun updateUsedWordsStatistic(wordEntity: WordEntity) {
        wordDao.updateWord(wordEntity)
    }


    private suspend fun adaptForConditions(
        dataBaseResponse: List<WordEntity>,
        difficultLevel: DifficultLevel
    ): List<WordEntity> {
        val wordCount = getDifficultyGrade(difficultLevel)
        // Перемешиваем элементы
        val shuffledList = dataBaseResponse.shuffled()

        return if (shuffledList.size >= wordCount) {
            // Если хватает слов, обрезаем до нужного количества
            shuffledList.take(wordCount)
        } else {
            // Если слов меньше, запрашиваем недостающие
            val missingCount = wordCount - shuffledList.size
            val additionalWords = getRandom(missingCount)

            // Объединяем текущие слова и недостающие
            (shuffledList + additionalWords).take(wordCount)
        }
    }

    private fun getDifficultyGrade(difficultLevel: DifficultLevel): Int {
        return when (difficultLevel) {
            DifficultLevel.EASY -> 12
            DifficultLevel.MEDIUM -> 18
            DifficultLevel.HARD -> 24
            DifficultLevel.EXPERT -> 48
            DifficultLevel.SURVIVAL -> 48
        }
    }

}