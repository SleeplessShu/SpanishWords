package com.example.spanishwords.game.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dictionaryA1_v01")
data class WordEntity(
    @PrimaryKey val id: Int,
    val level: String,
    val category: String,
    val correct: Int,
    val mistake: Int,
    val date: String,
    val english: String,
    val spanish: String,
    val russian: String,
    val french: String,
    val german: String
)