package com.example.spanishwords.game.domain.interactors

import androidx.lifecycle.LiveData
import com.example.spanishwords.game.domain.api.ScoreInteractor
import com.example.spanishwords.game.domain.repositories.ScoreRepository

class ScoreInteractorImpl(
    private val scoreRepository: ScoreRepository
): ScoreInteractor {
    override fun updateTodaysResult(matchResult: Int) {
        scoreRepository.updateTodaysResult(matchResult)
    }

    override fun getTodaysResult(): Int {
        return scoreRepository.getTodaysResult()
    }

    override fun getAllDaysResults(): LiveData<Map<String, Int>> {
        return scoreRepository.getAllDaysResults()
    }
}