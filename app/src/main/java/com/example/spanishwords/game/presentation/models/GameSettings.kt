package com.example.spanishwords.game.presentation.models

import com.example.spanishwords.game.domain.models.LanguageLevel
import com.example.spanishwords.game.domain.models.WordCategory

data class GameSettings(
    val language1: Language = Language.RUSSIAN,
    val language2: Language = Language.SPANISH,
    val level: LanguageLevel = LanguageLevel.A1,
    val category: WordCategory = WordCategory.RANDOM,
    val difficult: DifficultLevel = DifficultLevel.MEDIUM
)

