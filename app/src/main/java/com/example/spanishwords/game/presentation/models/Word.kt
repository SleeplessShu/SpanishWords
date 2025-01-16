package com.example.spanishwords.game.presentation.models

data class Word(
    val id: Int,
    val text: String,
    var language: Language
)
