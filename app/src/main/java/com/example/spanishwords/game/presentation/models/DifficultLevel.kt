package com.example.spanishwords.game.presentation.models

// egusev instead of getGameDifficult and getLivesCount funs
enum class DifficultLevel(notSureWhatThisFieldIsSupposedToBePleaseRename: Int, lives: Int) {
    EASY(notSureWhatThisFieldIsSupposedToBePleaseRename = 18, lives = 3),
    MEDIUM(notSureWhatThisFieldIsSupposedToBePleaseRename = 24, lives = 3),
    HARD(notSureWhatThisFieldIsSupposedToBePleaseRename = 48, lives = 2),
    EXPERT(notSureWhatThisFieldIsSupposedToBePleaseRename = 96, lives = 1),
    SURVIVAL(notSureWhatThisFieldIsSupposedToBePleaseRename = 192, lives = 1),
}