package com.example.spanishwords.game.presentation

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spanishwords.game.data.WordEntity
import com.example.spanishwords.game.domain.api.DatabaseInteractor
import com.example.spanishwords.game.domain.models.LanguageLevel
import com.example.spanishwords.game.domain.models.WordCategory
import com.example.spanishwords.game.presentation.models.DifficultLevel
import com.example.spanishwords.game.presentation.models.GameState
import com.example.spanishwords.game.presentation.models.Language
import com.example.spanishwords.game.presentation.models.MatchState
import com.example.spanishwords.game.presentation.models.Word
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class GameViewModel(private val repository: DatabaseInteractor) : ViewModel() {
    /*
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

     */
    private val languages = Language.entries.toTypedArray()
    private val levels = LanguageLevel.entries.toTypedArray()
    private val categories = WordCategory.entries.toTypedArray()
    private val difficult = DifficultLevel.entries.toTypedArray()


    private val _selectedLanguage1 = MutableLiveData(Language.RUSSIAN)
    val selectedLanguage1: LiveData<Language> get() = _selectedLanguage1

    private val _selectedLanguage2 = MutableLiveData(Language.SPANISH)
    val selectedLanguage2: LiveData<Language> get() = _selectedLanguage2

    private val _selectedLanguageLevel = MutableLiveData(LanguageLevel.A1)
    val selectedLanguageLevel: LiveData<LanguageLevel> get() = _selectedLanguageLevel

    private val _selectedCategory = MutableLiveData(WordCategory.RANDOM)
    val selectedCategory: LiveData<WordCategory> get() = _selectedCategory

    private val _selectedDifficult = MutableLiveData(DifficultLevel.MEDIUM)
    val selectedDifficult: LiveData<DifficultLevel> get() = _selectedDifficult

    private val _wordsPairs = MutableLiveData<List<Pair<Word, Word>>>()
    val wordsPairs: LiveData<List<Pair<Word, Word>>> get() = _wordsPairs

    private val _correctPairs = MutableLiveData<List<Pair<Word, Word>>>()
    val correctPairs: LiveData<List<Pair<Word, Word>>> get() = _correctPairs

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
    //MATCH SETTINGS

    // Переключение языка для первой группы
    fun switchLanguage1(isNext: Boolean) {
        val currentIndex = languages.indexOf(_selectedLanguage1.value)
        val nextIndex =
            if (isNext) (currentIndex + 1) % languages.size else (currentIndex - 1 + languages.size) % languages.size

        // Проверка на совпадение со второй группой
        if (languages[nextIndex] == _selectedLanguage2.value) {
            val adjustedIndex = (nextIndex + 1) % languages.size
            _selectedLanguage1.value = languages[adjustedIndex]
        } else {
            _selectedLanguage1.value = languages[nextIndex]
        }
    }

    // Переключение языка для второй группы
    fun switchLanguage2(isNext: Boolean) {
        val currentIndex = languages.indexOf(_selectedLanguage2.value)
        val nextIndex =
            if (isNext) (currentIndex + 1) % languages.size else (currentIndex - 1 + languages.size) % languages.size

        // Проверка на совпадение с первой группой
        if (languages[nextIndex] == _selectedLanguage1.value) {
            val adjustedIndex = (nextIndex + 1) % languages.size
            _selectedLanguage2.value = languages[adjustedIndex]
        } else {
            _selectedLanguage2.value = languages[nextIndex]
        }
    }

    //Переключение уровня языка
    fun switchWordsLevel(isNext: Boolean) {
        val currentIndex = levels.indexOf(_selectedLanguageLevel.value)
        val nextIndex =
            if (isNext) (currentIndex + 1) % levels.size else (currentIndex - +levels.size) % levels.size
        _selectedLanguageLevel.value = levels[nextIndex]
    }

    //Переключение сложности
    fun switchDifficultLevel(isNext: Boolean) {
        val currentIndex = difficult.indexOf(_selectedDifficult.value)
        val nextIndex =
            if (isNext) (currentIndex + 1) % difficult.size else (currentIndex - +difficult.size) % difficult.size
        _selectedDifficult.value = difficult[nextIndex]
    }

    //Переключение категории слов
    fun switchWordsCathegory(isNext: Boolean) {
        val currentIndex = categories.indexOf(_selectedCategory.value)
        val nextIndex =
            if (isNext) (currentIndex + 1) % categories.size else (currentIndex - +categories.size) % categories.size
        _selectedCategory.value = categories[nextIndex]
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
        if (selectedList[0].language == clickedWord.language) {
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
        return (first.id == second.id)/*
        return (_correctPairs.value?.any {
            (it.first == first && it.second == second) || (it.first == second && it.second == first)
        } == true)

         */
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
        loadWordsFromDatabase()
        onLoading()
        handler.postDelayed({
            _gameState.value = _gameState.value?.copy(state = GameState.GAME)
        }, DELAY_LOADING)

    }

    fun onGameEnd() {
        onLoading()
        handler.postDelayed({
            _gameState.value = _gameState.value?.copy(
                state = GameState.END_OF_GAME, lives = livesCount
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

    private fun loadWordsFromDatabase() {
        viewModelScope.launch {
            val wordsList = repository.getWordsPack(
                _selectedLanguage1.value!!,
                _selectedLanguage2.value!!,
                _selectedLanguageLevel.value!!,
                _selectedDifficult.value!!,
                _selectedCategory.value!!
            )
            val correctPairsList = wordsList.map { wordEntity ->
                toWordPair(
                    wordEntity, _selectedLanguage1.value!!, _selectedLanguage2.value!!
                )
            }
            Log.d("DEBUG", "loadWordsFromDatabase: ${correctPairsList}")
            _wordsPairs.value = shufflePairs(correctPairsList)
            _correctPairs.value = correctPairsList
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


    private companion object {
        const val DELAY_BUTTON_REACTION: Long = 500
        const val DELAY_LOADING: Long = 2000
        const val MISTAKE_PRICE: Int = 600
        const val CORRECT_ANSWER_PRICE: Int = 200

    }
}
