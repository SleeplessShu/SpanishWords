package com.example.spanishwords.game.presentation

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spanishwords.game.data.WordEntity
import com.example.spanishwords.game.domain.api.DatabaseInteractor
import com.example.spanishwords.game.domain.models.LanguageLevel
import com.example.spanishwords.game.domain.models.WordCategory
import com.example.spanishwords.game.presentation.models.DifficultLevel
import com.example.spanishwords.game.presentation.models.GameSettings
import com.example.spanishwords.game.presentation.models.GameState
import com.example.spanishwords.game.presentation.models.IngameWordsState
import com.example.spanishwords.game.presentation.models.Language
import com.example.spanishwords.game.presentation.models.MatchState
import com.example.spanishwords.game.presentation.models.Word
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class GameViewModel(private val repository: DatabaseInteractor) : ViewModel() {

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

    //private val _correctPairs = MutableLiveData<List<Pair<Word, Word>>>()
    //val correctPairs: LiveData<List<Pair<Word, Word>>> get() = _correctPairs

    private var score: Int = 0
    private var lives: Int = 3
    private var difficultLevel: Int = 18
    private val digitsInScore: Int = 10
    private var correctGuessesCounter: Int = 0

    private val handler: Handler = Handler(Looper.getMainLooper())
    private var selectedList: List<Word> = emptyList()


    //private var correctPairList: MutableList<Pair<Word, Word>> = mutableListOf()
    private val _wordsPairs = MutableLiveData<List<Pair<Word, Word>>>()
    val wordsPairs: LiveData<List<Pair<Word, Word>>> get() = _wordsPairs

    //private val _selectedWords = MutableLiveData<List<Word>>()
    //val selectedWords: LiveData<List<Word>> get() = _selectedWords

    //private val _errorWords = MutableLiveData<List<Word>>()
    //val errorWords: LiveData<List<Word>> get() = _errorWords

    //private val _correctWords = MutableLiveData<List<Word>>()
    //val correctWords: LiveData<List<Word>> get() = _correctWords

    //private val _usedWords = MutableLiveData<List<Word>>()
    //val usedWords: LiveData<List<Word>> get() = _usedWords


    init {
        onMatchSettings()
    }
    //MATCH SETTINGS

    // Переключение языка для первой группы
    fun switchLanguage1(isNext: Boolean) {
        val currentIndex = languages.indexOf(_gameSettings.value?.language1)
        val nextIndex =
            if (isNext) (currentIndex + 1) % languages.size else (currentIndex - 1 + languages.size) % languages.size

        if (languages[nextIndex] == _gameSettings.value?.language2) {
            val adjustedIndex = (nextIndex + 1) % languages.size
            updateLanguage1(languages[adjustedIndex])
        } else {
            updateLanguage1(languages[nextIndex])
        }
    }

    // Переключение языка для второй группы
    fun switchLanguage2(isNext: Boolean) {
        val currentIndex = languages.indexOf(_gameSettings.value?.language2)
        val nextIndex =
            if (isNext) (currentIndex + 1) % languages.size else (currentIndex - 1 + languages.size) % languages.size

        if (languages[nextIndex] == _gameSettings.value?.language1) {
            val adjustedIndex = (nextIndex + 1) % languages.size
            updateLanguage2(languages[adjustedIndex])
        } else {
            updateLanguage2(languages[nextIndex])
        }
    }

    //Переключение уровня языка
    fun switchWordsLevel(isNext: Boolean) {
        val currentIndex = levels.indexOf(_gameSettings.value?.level)
        val nextIndex =
            if (isNext) (currentIndex + 1) % levels.size else (currentIndex - +levels.size) % levels.size
        updateLevel(levels[nextIndex])
    }

    //Переключение сложности
    fun switchDifficultLevel(isNext: Boolean) {
        val currentIndex = difficult.indexOf(_gameSettings.value?.difficult)
        val nextIndex =
            if (isNext) (currentIndex + 1) % difficult.size else (currentIndex - +difficult.size) % difficult.size
        updateDifficult(difficult[nextIndex])
    }

    //Переключение категории слов
    fun switchWordsCathegory(isNext: Boolean) {
        val currentIndex = categories.indexOf(_gameSettings.value?.category)
        val nextIndex =
            if (isNext) (currentIndex + 1) % categories.size else (currentIndex - +categories.size) % categories.size
        updateCategory(categories[nextIndex])
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
        when (selectedList.size) {
            0 -> addInSelectedList(clickedWord)
            1 -> isSameLanguage(clickedWord)
            2 -> checkPair(selectedList)
        }
    }


    private fun addInSelectedList(clickedWord: Word) {
        _ingameWordsState.value = _ingameWordsState.value?.copy(
            selectedWords = _ingameWordsState.value?.selectedWords.orEmpty() + clickedWord
        )
    }

    private fun replaceInSelectedList(clickedWord: Word) {
        val updatedList = _ingameWordsState.value?.selectedWords.orEmpty().toMutableList().apply {
            if (isNotEmpty()) {
                this[0] = clickedWord
            }
        }
        _ingameWordsState.value = _ingameWordsState.value?.copy(
            selectedWords = updatedList
        )
    }


    private fun checkPair(pair: List<Word>) {
        val first = pair[0]
        val second = pair[1]
        if (isMatchingPair(first, second)) {
            reactOnCorrect()
            correctGuessesCounter++
            updateCorrectWordsList(first,second)
        } else {
            updateErrorList(first, second)
            reactOnError()
        }
        endGameCheck()
        clearSelectedList()
    }

    private fun endGameCheck() {
        if (lives <= 0) {
            handler.postDelayed(
                {
                    onGameEnd()
                }, 1000
            )
        } else if (correctGuessesCounter == difficultLevel) {
            onGameEnd()
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
        if (lives < 3) {
            lives++
        }
    }

    private fun getScoreAsString(score: Int): String {
        return String.format("%0${digitsInScore}d", score)
    }

    private fun clearSelectedList() {
        _ingameWordsState.value?.copy(selectedWords = emptyList())
    }

    private fun isSameLanguage(clickedWord: Word) {
        if (selectedList[0].language == clickedWord.language){
            if (isSameWord(clickedWord)) {
            replaceInSelectedList(clickedWord)
            } else {
                selectedList = emptyList<Word>()
            }
        } else {
            addInSelectedList(clickedWord)
            checkPair(selectedList)
        }
    }
    private fun isSameWord(clickedWord: Word): Boolean {
        return selectedList.isNotEmpty() && clickedWord.id == selectedList[0].id
    }

        private fun updateCorrectWordsList(first: Word, second: Word) {

            val updatedCorrectWordsList = _ingameWordsState.value?.correctWords.orEmpty().toMutableList().apply {
                add(first)
                add(second)
            }
            /*
            correctPairList.apply {
                add(Pair(first, second))
            }

             */
            updateUsedWordsList(first, second)
            _ingameWordsState.value = _ingameWordsState.value?.copy(
                selectedWords = emptyList(),
                correctWords = updatedCorrectWordsList
                )
        }


    private fun updateUsedWordsList(first: Word, second: Word) {
        handler.postDelayed({
            val updatedUsedWordsList =
                _ingameWordsState.value?.usedWords.orEmpty().toMutableList().apply {
                    add(first)
                    add(second)
                }
            _ingameWordsState.value = _ingameWordsState.value?.copy(
                correctWords = emptyList(), usedWords = updatedUsedWordsList
            )

        }, DELAY_BUTTON_REACTION)
    }

    private fun isMatchingPair(first: Word, second: Word): Boolean {
        return (first.id == second.id)

    }

    private fun updateErrorList(first: Word, second: Word) {
        _ingameWordsState.value?.copy(
            selectedWords = emptyList(), errorWords = listOf(first, second)
        )
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
        handler.postDelayed({
            _gameState.value = _gameState.value?.copy(state = GameState.GAME)
        }, DELAY_LOADING)
        setupScoreLivesDifficult()
        loadWordsFromDatabase()

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
        difficultLevel = getGameDifficult(_gameSettings.value?.difficult ?: DifficultLevel.MEDIUM)
        lives = getLivesCount(_gameSettings.value?.difficult ?: DifficultLevel.MEDIUM)
    }

    private fun loadWordsFromDatabase() {
        viewModelScope.launch {
            val wordsList = repository.getWordsPack(
                _gameSettings.value?.language1 ?: Language.ENGLISH,
                _gameSettings.value?.language2 ?: Language.SPANISH,
                _gameSettings.value?.level ?: LanguageLevel.A1,
                difficultLevel,
                _gameSettings.value?.category ?: WordCategory.RANDOM
            )
            val pairsList = wordsList.map { wordEntity ->
                toWordPair(
                    wordEntity,
                    _gameSettings.value?.language1 ?: Language.ENGLISH,
                    _gameSettings.value?.language2 ?: Language.SPANISH
                )
            }


            _wordsPairs.value = shufflePairs(pairsList)


        }
    }

    private fun toWordPair(
        wordEntity: WordEntity, original: Language, translate: Language
    ): Pair<Word, Word> {
        val word1 = getWordForLanguage(wordEntity, original)
        val word2 = getWordForLanguage(wordEntity, translate)
        return Pair(word1, word2)
    }

    private fun getWordForLanguage(entity: WordEntity, lang: Language): Word {
        return when (lang) {
            Language.ENGLISH -> Word(entity.id, entity.english, Language.ENGLISH)
            Language.SPANISH -> Word(entity.id, entity.spanish, Language.SPANISH)
            Language.RUSSIAN -> Word(entity.id, entity.russian, Language.RUSSIAN)
            Language.FRENCH -> Word(entity.id, entity.french, Language.FRENCH)
            Language.GERMAN -> Word(entity.id, entity.german, Language.GERMAN)
        }
    }

    private fun shufflePairs(input: List<Pair<Word, Word>>): List<Pair<Word, Word>> {
        if (input.size <= 1) return input
        val secondWords = input.map { it.second }.shuffled(Random(System.currentTimeMillis()))
        var shuffledPairs: List<Pair<Word, Word>>
        do {
            val shuffledSecondWords = secondWords.shuffled(Random(System.currentTimeMillis()))
            shuffledPairs = input.mapIndexed { index, pair ->
                pair.first to shuffledSecondWords[index]
            }
        } while (shuffledPairs.any { it.first.text == it.second.text })

        return shuffledPairs
    }

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

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    private fun getGameDifficult(difficultLevel: DifficultLevel): Int {
        return when (difficultLevel) {
            DifficultLevel.EASY -> 12
            DifficultLevel.MEDIUM -> 18
            DifficultLevel.HARD -> 24
            DifficultLevel.EXPERT -> 48
            DifficultLevel.SURVIVAL -> 48
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
