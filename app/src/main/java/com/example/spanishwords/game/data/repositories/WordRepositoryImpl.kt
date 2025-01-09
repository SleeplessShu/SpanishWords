package com.example.spanishwords.game.data.repositories

import com.example.spanishwords.game.data.WordEntity
import com.example.spanishwords.game.data.database.WordDao

class WordRepositoryImpl(private val wordDao: WordDao) {

    suspend fun getWordsByCategory(level: String, category: String): List<WordEntity> {
        return wordDao.getWordsByCategory(level, category)
    }

    suspend fun getRandomWords(level: String): List<WordEntity> {
        return wordDao.getRandomWords(level)
    }

    suspend fun updateWord(wordEntity: WordEntity) {
        wordDao.updateWord(wordEntity)
    }
}