package com.example.spanishwords.game.presentation

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spanishwords.game.domain.api.GameInteractor
import com.example.spanishwords.game.domain.api.ScoreInteractor
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

// egusev seems too big and difficult, but I guess that's normal for a ViewModel?
// Maybe you know of a way to split the logic between different ViewModels / any other way to
//  simplify
class GameViewModel(
    private val gameInteractor: GameInteractor,
    private val supportFunctions: SupportFunctions,
    private val scoreInteractor: ScoreInteractor,
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
        // egusev doing copy every time seems wrong, probably can do something like
        // _gameSettings.value.language1 = newLanguage
        // _gameSettings.doSomethingToNotifyAboutUpdate()
        // but not literally. Should be a way to not recreate _gameSettings every time, but idk
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

                } else if (isSameLanguage(clickedWord)) {

                    replaceInSelectedList(clickedWord)

                } else {

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
        // egusev !! is a sign that something is probably not right
        //  also, can save the result of _ingameWordsState.value!! and reuse, that seems cleaner,
        //  and also avoids a race condition if the _ingameWordsState has been changed between
        //  the 2 checks -- not sure about this whole thing, but probably you want to do checks for
        //  the same state.
        val state = _ingameWordsState.value
        if (state == null) {
            // egusev handle properly, this is just an example, I don't know how to log in android
            Log.e("error", "no state, achtung!!!")
            return false
        }
        // and can also probably wrap this whole thing as a
        //  `private fun MutableLiveData<IngameWordsState>.getState(): IngameWordsState {`
        //  below, and do `val state = _ingameWordsState.getState()`
        return (state.selectedWords[0].language == clickedWord.language
                && state.selectedWords[0].id != clickedWord.id)
    }

    // egusev just an example, remove from here
    private fun MutableLiveData<IngameWordsState>.getState(): IngameWordsState {
        val state = this.value
        if (state == null) {
            // egusev handle properly, this is just an example
            Log.e("error", "no state, achtung!!!")
            throw Exception()
        }
        return state
    }

    private fun isSameWord(clickedWord: Word): Boolean {
        return _ingameWordsState.value!!.selectedWords.isNotEmpty() &&
                clickedWord.id == _ingameWordsState.value!!.selectedWords[0].id &&
                clickedWord.language == _ingameWordsState.value!!.selectedWords[0].language
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

        // egusev you take a list, but always expect it to be 2 words?
        //  why accept a list then? what if someone calls this function and doesn't pass 2 elements?
        // Also, you use Pair<Word, Word> a lot it seems like, can make it your own class, like
        //  data class WordPair(val first: Word, val second: Word)
        //  and add a `fun isMathcingPair(): Boolean` fun to it instead, also re-use it in all
        //  repositories -- will probably come in handy to have it as a class in the future
        if (isMatchingPair(pair[0], pair[1])) {
            reactOnCorrect()
            correctGuessesCounter++
            updateCorrectWordsList(pair[0], pair[1])

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
                lives = lives, score = supportFunctions.getScoreAsString(score)
            )
        )
    }

    private fun reactOnCorrect() {
        addScoreAndLive()
        _gameState.postValue(
            _gameState.value?.copy(
                lives = lives, score = supportFunctions.getScoreAsString(score)
            )
        )
    }


    private fun addScoreAndLive() {
        score += CORRECT_ANSWER_PRICE
        if (lives < 3 && _gameSettings.value?.difficult != DifficultLevel.SURVIVAL) {
            lives++
        }
    }



    private fun clearSelectedList() {
        _ingameWordsState.value = _ingameWordsState.value?.copy(
            selectedWords = emptyList()
        )
    }


    private fun updateCorrectWordsList(first: Word, second: Word) {

        // egusev this:
        //  val newState1 = _ingameWordsState.value?.correctWords.orEmpty() + listOf(first, second)
        //  creates less collections, probably can do something smarter even, reusing the collection
        //  `correctWords` if it's MutableList, and reassigning it - same suggestion as line 111
        val newState = _ingameWordsState.value?.correctWords.orEmpty().toMutableList().apply {
            add(first)
            add(second)
        }
        clearSelectedList()
        _ingameWordsState.value = _ingameWordsState.value?.copy(correctWords = newState)
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
        val todaysScore = scoreInteractor.getTodaysResult()
        handler.postDelayed({
            _gameState.value = _gameState.value?.copy(
                state = GameState.END_OF_GAME, lives = lives, todaysScore = supportFunctions.getScoreAsString(todaysScore)
            )
            scoreInteractor.updateTodaysResult(score)
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
        difficultLevel = supportFunctions.getGameDifficult(_gameSettings.value?.difficult ?: DifficultLevel.MEDIUM)
        lives = supportFunctions.getLivesCount(_gameSettings.value?.difficult ?: DifficultLevel.MEDIUM)
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

    // egusev better not to leave comments of dead code
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




    private companion object {
        const val DELAY_BUTTON_REACTION: Long = 500
        const val DELAY_LOADING: Long = 2000
        const val MISTAKE_PRICE: Int = 600
        const val CORRECT_ANSWER_PRICE: Int = 200

    }
}
