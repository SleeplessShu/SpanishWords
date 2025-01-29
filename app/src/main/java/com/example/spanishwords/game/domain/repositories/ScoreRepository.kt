package com.example.spanishwords.game.domain.repositories

import androidx.lifecycle.LiveData

interface ScoreRepository {
    fun updateTodaysResult(matchResult: Int)
    fun getTodaysResult(): Int
    fun getAllDaysResults(): LiveData<Map<String, Int>>
}