package com.example.spanishwords.game.data.database


import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import com.example.spanishwords.game.data.WordEntity
import com.example.spanishwords.game.domain.models.LanguageLevel
import com.example.spanishwords.game.domain.models.WordCategory
import com.example.spanishwords.game.presentation.models.DifficultLevel
import com.example.spanishwords.game.presentation.models.Language

@Dao
interface WordDao {

    @Query("SELECT * FROM A1 WHERE category = :category COLLATE NOCASE LIMIT 36")
    suspend fun getWordsByCategory(category: WordCategory): List<WordEntity>

    @Query("SELECT * FROM A1")
    suspend fun testing(): List<WordEntity>

    @Query("SELECT COUNT(*) FROM A1")
    suspend fun countWords(): Int


    @Update
    suspend fun updateWord(wordEntity: WordEntity)

}