package com.example.spanishwords.game.data.database

import androidx.room.Query
import androidx.room.Update
import com.example.spanishwords.game.data.WordEntity

interface WordDao {

    @Query("SELECT * FROM dictionaryA1_v01 WHERE level = :level AND category = :category LIMIT 36")
    suspend fun getWordsByCategory(level: String, category: String): List<WordEntity>

    @Query("SELECT * FROM dictionaryA1_v01 WHERE level = :level ORDER BY RANDOM() LIMIT 36")
    suspend fun getRandomWords(level: String): List<WordEntity>

    @Update
    suspend fun updateWord(word: WordEntity)
}