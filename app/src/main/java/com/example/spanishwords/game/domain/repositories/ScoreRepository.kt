package com.example.spanishwords.game.domain.repositories

interface ScoreRepository {
    fun updateTodaysResult(matchResult: Int)
    fun getTodaysResult(): Int
    fun getAllDaysResults(): Map<String, Int>
}