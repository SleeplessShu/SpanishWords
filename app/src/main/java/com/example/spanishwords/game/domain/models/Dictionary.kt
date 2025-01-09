package com.example.spanishwords.game.domain.models

import com.example.spanishwords.game.presentation.models.Language

data class Dictionary(
    val id:Int,
    val level: LanguageLevel,
    val category: WordCategory,
    val languageOrigin: Language,
    val languageTranslate: Language
)
