package com.example.spanishwords.game.data.repositories

import com.example.spanishwords.game.data.WordEntity
import com.example.spanishwords.game.data.database.WordDao
import com.example.spanishwords.game.domain.models.LanguageLevel
import com.example.spanishwords.game.domain.models.WordCategory
import com.example.spanishwords.game.domain.repositories.DatabaseRepository
import com.example.spanishwords.game.presentation.models.Language

class DatabaseRepositoryImpl(private val wordDao: WordDao) : DatabaseRepository {

    override suspend fun getWordsPack(
        language1: Language,
        language2: Language,
        level: LanguageLevel,
        difficultLevel: Int,
        category: WordCategory
    ): List<WordEntity> {
        var dataBaseResponse = emptyList<WordEntity>()
        if (category == WordCategory.RANDOM) {
           dataBaseResponse = getRandom(difficultLevel)
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
        difficultLevel: Int
    ): List<WordEntity> {

        // Перемешиваем элементы
        val shuffledList = dataBaseResponse.shuffled()

        return if (shuffledList.size >= difficultLevel) {
            // Если хватает слов, обрезаем до нужного количества
            shuffledList.take(difficultLevel)
        } else {
            // Если слов меньше, запрашиваем недостающие
            val missingCount = difficultLevel - shuffledList.size
            val additionalWords = getRandom(missingCount)

            // Объединяем текущие слова и недостающие
            (shuffledList + additionalWords).take(difficultLevel)
        }
    }



}