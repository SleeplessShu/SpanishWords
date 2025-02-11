package com.example.spanishwords.game.data.repositories

import com.example.spanishwords.game.data.WordEntity
import com.example.spanishwords.game.data.database.WordDao
import com.example.spanishwords.game.domain.models.LanguageLevel
import com.example.spanishwords.game.domain.models.WordCategory
import com.example.spanishwords.game.domain.repositories.DatabaseRepository
import com.example.spanishwords.game.presentation.models.Language

class DatabaseRepositoryImpl(private val wordDao: WordDao) : DatabaseRepository {

    override suspend fun getWordsPack(
        // egusev 3 params below are unused
        language1: Language,
        language2: Language,
        level: LanguageLevel,

        difficultLevel: Int,
        category: WordCategory
    ): List<WordEntity> {
        var dataBaseResponse = emptyList<WordEntity>()
        if (category == WordCategory.RANDOM) {
            // egusev difficultLevel is limit of words? unclear naming
            // also, personally would prefer if repository didn't have logic under if, better to
            //  make 2 functions IMO and let the service (e.g. db interactor) decide
            //  which one to call -- as a bonus, would not have to use var

            // even here can just do "return getRandom(..
            //  and return adaptForConditions(.. and have no vars
           dataBaseResponse = getRandom(difficultLevel)
        } else {
            dataBaseResponse = wordDao.getWordsByCategory(category)
            dataBaseResponse = adaptForConditions(dataBaseResponse, difficultLevel)
        }
        return dataBaseResponse
    }

    suspend fun getRandom(wordsNeeded: Int): List<WordEntity> {
        // egusev what are additionalWords in this context?
        // context should be encapsulated inside a function, this one just gets random words,
        //  doesn't know what "additional" or not is
        // btw, this is public, but not from interface -- is that expected?
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