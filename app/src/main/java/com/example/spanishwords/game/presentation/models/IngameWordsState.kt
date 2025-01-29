package com.example.spanishwords.game.presentation.models

data class IngameWordsState(
    val selectedWords: List<Word> = emptyList(),
    val correctWords: List<Word> = emptyList(),
    val errorWords: List<Word> = emptyList(),
    val usedWords: List<Word> = emptyList(),
)
