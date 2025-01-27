package com.example.spanishwords.game.domain.api

interface ScoreInteractor {
    fun updateTodaysResult(matchResult: Int)
    fun getTodaysResult(): Int
    fun getAllDaysResults(): Map<String, Int>
}