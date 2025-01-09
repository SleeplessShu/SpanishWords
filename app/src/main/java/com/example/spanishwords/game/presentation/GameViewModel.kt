package com.example.spanishwords.game.presentation

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.spanishwords.game.presentation.models.GameState
import com.example.spanishwords.game.presentation.models.Language
import com.example.spanishwords.game.presentation.models.MatchState
import com.example.spanishwords.game.presentation.models.Word

class GameViewModel : ViewModel() {

    private val _wordsPairs = MutableLiveData(
        listOf(
            Word("Hola", Language.ENGLISH) to Word("Пока", Language.SPANISH),
            Word("Adiós", Language.ENGLISH) to Word("Привет", Language.SPANISH),
            Word("Gracias", Language.ENGLISH) to Word("Пожалуйста", Language.SPANISH),
            Word("Por favor", Language.ENGLISH) to Word("Спасибо", Language.SPANISH),
            Word("Buenos días", Language.ENGLISH) to Word("Доброй ночи", Language.SPANISH),
            Word("Buenas noches", Language.ENGLISH) to Word("Доброе утро", Language.SPANISH)
        )
    )

    val wordsPairs: LiveData<List<Pair<Word, Word>>> get() = _wordsPairs

    private val _correctPairs = MutableLiveData(
        listOf(
            Word("Hola", Language.ENGLISH) to Word("Привет", Language.SPANISH),
            Word("Adiós", Language.ENGLISH) to Word("Пока", Language.SPANISH),
            Word("Gracias", Language.ENGLISH) to Word("Спасибо", Language.SPANISH),
            Word("Por favor", Language.ENGLISH) to Word("Пожалуйста", Language.SPANISH),
            Word("Buenos días", Language.ENGLISH) to Word("Доброе утро", Language.SPANISH),
            Word("Buenas noches", Language.ENGLISH) to Word("Доброй ночи", Language.SPANISH)
        )
    )
    private var score: Int = 0
    private var livesCount: Int = 3
    private val digitsInScore: Int = 10

    private val handler: Handler = Handler(Looper.getMainLooper())

    private val selectedList = mutableListOf<Word>()
    private var correctPairList: MutableList<Pair<Word, Word>> = mutableListOf()

    private val _selectedWords = MutableLiveData<List<Word>>()
    val selectedWords: LiveData<List<Word>> get() = _selectedWords

    private val _errorWords = MutableLiveData<List<Word>>()
    val errorWords: LiveData<List<Word>> get() = _errorWords

    private val _correctWords = MutableLiveData<List<Word>>()
    val correctWords: LiveData<List<Word>> get() = _correctWords

    private val _usedWords = MutableLiveData<List<Word>>()
    val usedWords: LiveData<List<Word>> get() = _usedWords


    private val _gameState = MutableLiveData<MatchState>(MatchState())
    val gameState: LiveData<MatchState> get() = _gameState


    init {
        onMatchSettings()

    }


    fun onWordClick(clickedWord: Word) {
        when (selectedList.size) {
            0 -> addInSelectedList(clickedWord)
            1 -> isSameLanguage(clickedWord)
            2 -> checkPair(selectedList)
        }
    }

    private fun addInSelectedList(clickedWord: Word) {
        selectedList.add(clickedWord)
        _selectedWords.value = selectedList.toList()
    }

    private fun replaceInSelectedList(clickedWord: Word) {
        selectedList[0] = clickedWord
        _selectedWords.value = selectedList.toList()
    }

    private fun checkPair(pair: List<Word>) {
        val first = pair[0]
        val second = pair[1]
        if (isMatchingPair(first, second)) {
            reactOnCorrect()
            updateCorrectWordsList(first, second)
        } else {
            updateErrorList(first, second)
            reactOnError()
        }
        endGameCheck()
        clearSelectedList()
    }

    private fun endGameCheck() {
        if (livesCount <= 0) {
            handler.postDelayed(
                {
                    onGameEnd()
                }, 1000
            )
        } else if (_correctPairs.value?.size == correctPairList.size) {
            onGameEnd()
        }
    }

    private fun reactOnError() {
        removeScoreAndLive()
        _gameState.postValue(
            _gameState.value?.copy(
                lives = livesCount, score = getScoreAsString(score)
            )
        )
    }

    private fun reactOnCorrect() {
        addScoreAndLive()
        _gameState.postValue(
            _gameState.value?.copy(
                lives = livesCount, score = getScoreAsString(score)
            )
        )
    }

    private fun addScoreAndLive() {
        score += CORRECT_ANSWER_PRICE
        if (livesCount < 3) {
            livesCount++
        }
    }

    private fun getScoreAsString(score: Int): String {
        return String.format("%0${digitsInScore}d", score)
    }

    private fun clearSelectedList() {
        _selectedWords.value = emptyList()
        selectedList.clear()
    }

    private fun isSameLanguage(clickedWord: Word) {
        if (selectedList[0].lang == clickedWord.lang) {
            replaceInSelectedList(clickedWord)
        } else {
            addInSelectedList(clickedWord)
            checkPair(selectedList)
        }
    }

    private fun updateCorrectWordsList(first: Word, second: Word) {
        _selectedWords.value = emptyList()
        val updatedCorrectWordsList = _correctWords.value.orEmpty().toMutableList().apply {
            add(first)
            add(second)
        }
        correctPairList.apply {
            add(Pair(first, second))
        }
        _correctWords.value = updatedCorrectWordsList
        updateUsedWordsList(first, second)
    }

    private fun updateUsedWordsList(first: Word, second: Word) {
        handler.postDelayed({
            val updatedUsedWordsList = _usedWords.value.orEmpty().toMutableList().apply {
                add(first)
                add(second)
            }
            _correctWords.value = emptyList()
            _usedWords.value = updatedUsedWordsList
        }, DELAY_BUTTON_REACTION)
    }

    private fun isMatchingPair(first: Word, second: Word): Boolean {
        return (_correctPairs.value?.any {
            (it.first == first && it.second == second) || (it.first == second && it.second == first)
        } == true)
    }

    private fun updateErrorList(first: Word, second: Word) {
        _selectedWords.value = emptyList()
        _errorWords.value = listOf(first, second)
        clearErrorList()
    }

    private fun clearErrorList() {
        handler.postDelayed({
            _errorWords.value = emptyList()
        }, DELAY_BUTTON_REACTION)
    }

    private fun removeScoreAndLive() {
        livesCount--
        score -= MISTAKE_PRICE

    }


    fun onMatchSettings() {
        _gameState.value = _gameState.value?.copy(state = GameState.MATCH_SETTINGS)
    }

    fun onLoading() {
        _gameState.value = _gameState.value?.copy(state = GameState.LOADING)

    }

    fun onGame() {
        onLoading()
        handler.postDelayed({
            _gameState.value = _gameState.value?.copy(state = GameState.GAME)
        }, DELAY_LOADING)

    }

    fun onGameEnd() {
        onLoading()
        handler.postDelayed({
            _gameState.value = _gameState.value?.copy(
                state = GameState.END_OF_GAME,
                lives = livesCount
            )

        }, DELAY_LOADING)
    }

    fun restartGame() {
        resetMatchStats()
        onGame()
    }

    private fun resetMatchStats() {
        _selectedWords.value = emptyList()
        _errorWords.value = emptyList()
        _usedWords.value = emptyList()
        correctPairList = mutableListOf()
        livesCount = 3
        score = 0
    }

    private companion object {
        const val DELAY_BUTTON_REACTION: Long = 500
        const val DELAY_LOADING: Long = 2000
        const val MISTAKE_PRICE: Int = 600
        const val CORRECT_ANSWER_PRICE: Int = 200

    }
}
