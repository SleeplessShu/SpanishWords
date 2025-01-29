package com.example.spanishwords.game.domain.api

import androidx.lifecycle.LiveData

interface ScoreInteractor {
    fun updateTodaysResult(matchResult: Int)
    fun getTodaysResult(): Int
    fun getAllDaysResults(): LiveData<Map<String, Int>>
}