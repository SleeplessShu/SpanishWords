package com.example.spanishwords.game.domain.interactors

import com.example.spanishwords.game.data.WordEntity
import com.example.spanishwords.game.domain.api.DatabaseInteractor
import com.example.spanishwords.game.domain.models.LanguageLevel
import com.example.spanishwords.game.domain.models.WordCategory
import com.example.spanishwords.game.domain.repositories.DatabaseRepository
import com.example.spanishwords.game.presentation.models.DifficultLevel
import com.example.spanishwords.game.presentation.models.Language

class DatabaseInteractorImpl(private val database: DatabaseRepository): DatabaseInteractor {
    override suspend fun getWordsPack(language1: Language,
                                      language2: Language,
                                      level: LanguageLevel,
                                      difficultLevel: DifficultLevel,
                                      category: WordCategory
    ): List<WordEntity> {
        return database.getWordsPack(
            language1, language2, level, difficultLevel, category)
    }

    override suspend fun updateWord(wordEntity: WordEntity){
        database.updateUsedWordsStatistic(wordEntity)
    }
}