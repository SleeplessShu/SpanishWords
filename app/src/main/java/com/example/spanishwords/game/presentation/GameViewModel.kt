package com.example.spanishwords.game.presentation

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spanishwords.game.domain.api.GameInteractor
import com.example.spanishwords.game.domain.models.LanguageLevel
import com.example.spanishwords.game.domain.models.WordCategory
import com.example.spanishwords.game.presentation.models.DifficultLevel
import com.example.spanishwords.game.presentation.models.GameSettings
import com.example.spanishwords.game.presentation.models.GameState
import com.example.spanishwords.game.presentation.models.IngameWordsState
import com.example.spanishwords.game.presentation.models.Language
import com.example.spanishwords.game.presentation.models.MatchState
import com.example.spanishwords.game.presentation.models.Word
import com.example.spanishwords.utils.SupportFunctions
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GameViewModel(
    //private val repository: DatabaseInteractor,
    private val gameInteractor: GameInteractor,
    private val supportFunctions: SupportFunctions,
) : ViewModel() {

    private val languages = Language.entries.toTypedArray()
    private val levels = LanguageLevel.entries.toTypedArray()
    private val categories = WordCategory.entries.toTypedArray()
    private val difficult = DifficultLevel.entries.toTypedArray()

    private val _gameState = MutableLiveData<MatchState>(MatchState())
    val gameState: LiveData<MatchState> get() = _gameState

    private val _gameSettings = MutableLiveData(GameSettings())
    val gameSettings: LiveData<GameSettings> get() = _gameSettings

    private val _ingameWordsState = MutableLiveData(IngameWordsState())
    val ingameWordsState: LiveData<IngameWordsState> get() = _ingameWordsState

    private var score: Int = 0
    private var lives: Int = 3
    private var difficultLevel: Int = 18
    private val digitsInScore: Int = 10
    private var correctGuessesCounter: Int = 0
    private val pageSize = 6
    private var pairsFromDatabase: List<Pair<Word, Word>> = emptyList()
    private var currentPage = 0
    private val handler: Handler = Handler(Looper.getMainLooper())

    private val _wordsPairs = MutableLiveData<List<Pair<Word, Word>>>()
    val wordsPairs: LiveData<List<Pair<Word, Word>>> get() = _wordsPairs

    init {
        onMatchSettings()
    }
    //MATCH SETTINGS

    // Переключение языка для первой группы
    fun switchLanguage1(isNext: Boolean) {
        val nextLanguage =
            supportFunctions.switchItem(_gameSettings.value?.language1, languages, isNext)
        if (nextLanguage == _gameSettings.value?.language2) {
            val adjustedLanguage = supportFunctions.switchItem(nextLanguage, languages, isNext)
            updateLanguage1(adjustedLanguage)
        } else {
            updateLanguage1(nextLanguage)
        }
    }

    // Переключение языка для второй группы
    fun switchLanguage2(isNext: Boolean) {
        val nextLanguage =
            supportFunctions.switchItem(_gameSettings.value?.language2, languages, isNext)
        if (nextLanguage == _gameSettings.value?.language1) {
            val adjustedLanguage = supportFunctions.switchItem(nextLanguage, languages, isNext)
            updateLanguage2(adjustedLanguage)
        } else {
            updateLanguage2(nextLanguage)
        }
    }

    //Переключение уровня языка
    fun switchWordsLevel(isNext: Boolean) {
        val nextLevel = supportFunctions.switchItem(_gameSettings.value?.level, levels, isNext)
        updateLevel(nextLevel)
    }

    //Переключение сложности
    fun switchDifficultLevel(isNext: Boolean) {
        val nextDifficult = supportFunctions.switchItem(_gameSettings.value?.difficult, difficult, isNext)
        updateDifficult(nextDifficult)
    }

    //Переключение категории слов
    fun switchWordsCategory(isNext: Boolean) {
        val nextCategory = supportFunctions.switchItem(_gameSettings.value?.category, categories, isNext)
        updateCategory(nextCategory)
    }

    fun updateLanguage1(newLanguage: Language) {
        _gameSettings.value = _gameSettings.value?.copy(language1 = newLanguage)
    }

    fun updateLanguage2(newLanguage: Language) {
        _gameSettings.value = _gameSettings.value?.copy(language2 = newLanguage)
    }

    fun updateLevel(newLevel: LanguageLevel) {
        _gameSettings.value = _gameSettings.value?.copy(level = newLevel)
    }

    fun updateCategory(newCategory: WordCategory) {
        _gameSettings.value = _gameSettings.value?.copy(category = newCategory)
    }

    fun updateDifficult(newDifficult: DifficultLevel) {
        _gameSettings.value = _gameSettings.value?.copy(difficult = newDifficult)
    }

    //GAME
    fun onWordClick(clickedWord: Word) {
        val selectedWords = _ingameWordsState.value?.selectedWords.orEmpty()
        when (selectedWords.size) {
            0 -> addInSelectedList(clickedWord)
            1 -> {
                if (isSameWord(clickedWord)) {
                    clearSelectedList()
                    Log.d("missing_spot", "onWordClick: 1-if")
                } else if (isSameLanguage(clickedWord)) {
                    Log.d("missing_spot", "onWordClick: 1-elseif")
                    replaceInSelectedList(clickedWord)

                } else {
                    Log.d("missing_spot", "onWordClick: 1-else")
                    addInSelectedList(clickedWord)
                    checkPair(_ingameWordsState.value!!.selectedWords)
                }
            }

            else -> clearSelectedList()
        }
    }

    private fun addInSelectedList(clickedWord: Word) {
        val updatedSelectedWords = _ingameWordsState.value?.selectedWords.orEmpty() + clickedWord
        _ingameWordsState.value = _ingameWordsState.value?.copy(
            selectedWords = updatedSelectedWords
        )
    }

    private fun isSameLanguage(clickedWord: Word): Boolean {
        return (_ingameWordsState.value!!.selectedWords[0].language == clickedWord.language && _ingameWordsState.value!!.selectedWords[0].id != clickedWord.id)
    }

    private fun isSameWord(clickedWord: Word): Boolean {
        return _ingameWordsState.value!!.selectedWords.isNotEmpty() && clickedWord.id == _ingameWordsState.value!!.selectedWords[0].id && clickedWord.language == _ingameWordsState.value!!.selectedWords[0].language
    }

    private fun replaceInSelectedList(clickedWord: Word) {
        val updatedSelectedWords =
            _ingameWordsState.value?.selectedWords.orEmpty().toMutableList().apply {
                if (isNotEmpty()) {
                    this[0] = clickedWord
                }
            }
        _ingameWordsState.value = _ingameWordsState.value?.copy(
            selectedWords = updatedSelectedWords
        )
    }


    private fun checkPair(pair: List<Word>) {
        Log.d("DEBUG", "!!!checkPair: ${pair[0]}, ${pair[1]}")
        if (isMatchingPair(pair[0], pair[1])) {
            reactOnCorrect()
            correctGuessesCounter++
            updateCorrectWordsList(pair[0], pair[1])
            Log.d("DEBUG", "checkPair: ISCORRECT")
        } else {
            updateErrorList(pair[0], pair[1])
            reactOnError()
        }
        endGameCheck()
        clearSelectedList()
    }

    private fun endGameCheck() {
        val totalPairs = pairsFromDatabase.size
        val answeredPairs = currentPage * pageSize + correctGuessesCounter

        if (lives <= 0 || answeredPairs >= totalPairs) {
            onGameEnd()
        } else if (correctGuessesCounter == pageSize) {
            onPageCompleted()
        }
    }


    private fun reactOnError() {
        removeScoreAndLive()
        _gameState.postValue(
            _gameState.value?.copy(
                lives = lives, score = getScoreAsString(score)
            )
        )
    }

    private fun reactOnCorrect() {
        addScoreAndLive()
        _gameState.postValue(
            _gameState.value?.copy(
                lives = lives, score = getScoreAsString(score)
            )
        )
    }


    private fun addScoreAndLive() {
        score += CORRECT_ANSWER_PRICE
        if (lives < 3 && _gameSettings.value?.difficult != DifficultLevel.SURVIVAL) {
            lives++
        }
    }

    private fun getScoreAsString(score: Int): String {
        return String.format("%0${digitsInScore}d", score)
    }

    private fun clearSelectedList() {
        _ingameWordsState.value = _ingameWordsState.value?.copy(
            selectedWords = emptyList()
        )
    }


    private fun updateCorrectWordsList(first: Word, second: Word) {

        val newState = _ingameWordsState.value?.correctWords.orEmpty().toMutableList().apply {
            add(first)
            add(second)
        }
        clearSelectedList()
        _ingameWordsState.value = _ingameWordsState.value?.copy(correctWords = newState)
        Log.d("DEBUG", "updateCorrectWordsList: ${newState}")
        updateUsedWordsList(first, second)
    }


    private fun updateUsedWordsList(first: Word, second: Word) {
        val currentState = _ingameWordsState.value ?: return
        handler.postDelayed({
            val updatedUsedWordsList = currentState.usedWords.toMutableList().apply {
                add(first)
                add(second)
            }
            _ingameWordsState.value = currentState.copy(
                correctWords = emptyList(), usedWords = updatedUsedWordsList
            )
        }, DELAY_BUTTON_REACTION)
    }

    private fun isMatchingPair(first: Word, second: Word): Boolean {
        return (first.id == second.id)

    }

    private fun updateErrorList(first: Word, second: Word) {
        val newState = _ingameWordsState.value?.copy(
            selectedWords = emptyList(), errorWords = listOf(first, second)
        )
        _ingameWordsState.value = newState
        clearErrorList()
    }

    private fun clearErrorList() {
        handler.postDelayed({
            _ingameWordsState.value = _ingameWordsState.value?.copy(errorWords = emptyList())
        }, DELAY_BUTTON_REACTION)
    }

    private fun removeScoreAndLive() {
        lives--
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
        setupScoreLivesDifficult()
        loadWordsFromDatabase {
            loadNextPage()
            handler.postDelayed({
                _gameState.value = _gameState.value?.copy(state = GameState.GAME)
            }, DELAY_LOADING)
        }
    }


    fun onGameEnd() {
        onLoading()
        handler.postDelayed({
            _gameState.value = _gameState.value?.copy(
                state = GameState.END_OF_GAME, lives = lives
            )
        }, DELAY_LOADING)
    }

    fun restartGame() {
        resetMatchStats()
        onGame()
    }

    private fun resetMatchStats() {
        _ingameWordsState.value = _ingameWordsState.value?.copy(
            selectedWords = emptyList(), errorWords = emptyList(), usedWords = emptyList()
        )
    }

    private fun setupScoreLivesDifficult() {
        score = 0
        correctGuessesCounter = 0
        currentPage = 0
        difficultLevel = getGameDifficult(_gameSettings.value?.difficult ?: DifficultLevel.MEDIUM)
        lives = getLivesCount(_gameSettings.value?.difficult ?: DifficultLevel.MEDIUM)
    }


    private fun loadWordsFromDatabase(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val shuffledPairs = gameInteractor.getWordPairs(
                _gameSettings.value?.language1 ?: Language.ENGLISH,
                _gameSettings.value?.language2 ?: Language.SPANISH,
                _gameSettings.value?.level ?: LanguageLevel.A1,
                difficultLevel,
                _gameSettings.value?.category ?: WordCategory.RANDOM
            )
            if (shuffledPairs.isEmpty()) {
                onGameEnd()
                return@launch
            }

            pairsFromDatabase = shuffledPairs
            currentPage = 0
            onSuccess() // Переход к первой странице.
        }
    }


    fun onPageCompleted() {
        if (currentPage * pageSize < pairsFromDatabase.size) {
            loadNextPage()
        } else {
            onGameEnd()
        }
    }


    private fun shufflePairs(input: List<Pair<Word, Word>>): List<Pair<Word, Word>> {
        return gameInteractor.shufflePairs(input)
    }

    /*
        private fun updateWordStats(wordEntity: WordEntity, isCorrect: Boolean) {
            val updatedWord = wordEntity.copy(
                correct = if (isCorrect) wordEntity.correct + 1 else wordEntity.correct,
                mistake = if (!isCorrect) wordEntity.mistake + 1 else wordEntity.mistake,
                date = getCurrentDate()
            )
            viewModelScope.launch {
                repository.updateWord(updatedWord)
            }
        }

     */



    fun loadNextPage() {
        if (currentPage * pageSize >= pairsFromDatabase.size) {
            onGameEnd()
            return
        }

        val nextPagePairs = getCurrentPagePairs()
        if (nextPagePairs.isNotEmpty()) {
            _ingameWordsState.value = _ingameWordsState.value?.copy(
                selectedWords = emptyList(),
                errorWords = emptyList(),
                correctWords = emptyList(),
                usedWords = emptyList()
            )
            correctGuessesCounter = 0
            _wordsPairs.value = shufflePairs(nextPagePairs)
            currentPage++
        } else {
            onGameEnd()
        }
    }


    private fun getCurrentPagePairs(): List<Pair<Word, Word>> {
        val fromIndex = currentPage * pageSize
        val toIndex = (fromIndex + pageSize).coerceAtMost(pairsFromDatabase.size)
        return if (fromIndex < toIndex) {
            pairsFromDatabase.subList(fromIndex, toIndex)
        } else {
            emptyList()
        }
    }

    private fun getGameDifficult(difficultLevel: DifficultLevel): Int {
        return when (difficultLevel) {
            DifficultLevel.EASY -> 18
            DifficultLevel.MEDIUM -> 24
            DifficultLevel.HARD -> 48
            DifficultLevel.EXPERT -> 96
            DifficultLevel.SURVIVAL -> 192
        }
    }

    private fun getLivesCount(difficultLevel: DifficultLevel): Int {
        return when (difficultLevel) {
            DifficultLevel.EASY -> 3
            DifficultLevel.MEDIUM -> 3
            DifficultLevel.HARD -> 2
            DifficultLevel.EXPERT -> 1
            DifficultLevel.SURVIVAL -> 1
        }
    }


    private companion object {
        const val DELAY_BUTTON_REACTION: Long = 500
        const val DELAY_LOADING: Long = 2000
        const val MISTAKE_PRICE: Int = 600
        const val CORRECT_ANSWER_PRICE: Int = 200

    }
}
