package com.example.spanishwords.game.data.repositories

import android.util.Log
import androidx.compose.ui.text.toLowerCase
import com.example.spanishwords.game.data.WordEntity
import com.example.spanishwords.game.data.database.WordDao
import com.example.spanishwords.game.domain.models.LanguageLevel
import com.example.spanishwords.game.domain.models.WordCategory
import com.example.spanishwords.game.domain.repositories.DatabaseRepository
import com.example.spanishwords.game.presentation.models.DifficultLevel
import com.example.spanishwords.game.presentation.models.Language

class DatabaseRepositoryImpl(private val wordDao: WordDao) : DatabaseRepository{

    override suspend fun getWordsPack(language1: Language,
                                      language2: Language,
                                      level: LanguageLevel,
                                      difficultLevel: DifficultLevel,
                                      category: WordCategory
    ): List<WordEntity> {

        val dataBaseResponse = wordDao.getWordsByCategory(category)

        return dataBaseResponse
    }

    override suspend fun updateUsedWordsStatistic(wordEntity: WordEntity) {
        wordDao.updateWord(wordEntity)
    }
}