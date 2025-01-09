package com.example.spanishwords.game.presentation.models

data class MatchState (
    var state: GameState = GameState.MATCH_SETTINGS,
    var lives: Int = 3,
    var score: String = "00000000",
    var answerPointsState: AnswerPointsState = AnswerPointsState.EMPTY
)