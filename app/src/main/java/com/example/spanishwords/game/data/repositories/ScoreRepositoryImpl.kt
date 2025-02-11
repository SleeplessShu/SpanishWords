package com.example.spanishwords.game.data.repositories

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.spanishwords.game.domain.repositories.ScoreRepository
import com.example.spanishwords.utils.SupportFunctions

class ScoreRepositoryImpl(
    // egusev from the description doesn't seem that this is the right class to use,
    //  probably should store this in db locally or on the server? I don't know what's right
    private var sharedPreferences: SharedPreferences,
    private val supportFunctions: SupportFunctions,
) : ScoreRepository {

    private val _allDaysResults = MutableLiveData<Map<String, Int>>()

    init {
        // egusev can't this take long to load? especially on init every time
        _allDaysResults.value = supportFunctions.sortMapByDateDescending(sharedPreferences.all
            .filterValues { it is Int }
            .mapValues { it.value as Int }
        )

    }

    override fun updateTodaysResult(matchResult: Int) {
        val currentDate = supportFunctions.getCurrentDate()
        val currentResult = getTodaysResult()
        val newResult = currentResult + matchResult

        sharedPreferences.edit().putInt(currentDate, newResult).apply()

        // egusev doesn't _allDaysResults already have the todaysResult?
        //  probably can make _allDaysResult a MutableMap
        //  and just do something like _allDaysResults.value[currentDate] = currentResult
        //  or _allDaysResults.postValue(_allDaysResults.value.apply { it[currentDate] = currentResult })
        //  if this is needed for LiveData
        //   or do any other optimization
        //   instead of filtering+maping+sorting etc

        // Обновляем LiveData
        val updatedMap = sharedPreferences.all
            .filterValues { it is Int }
            .mapValues { it.value as Int }
        _allDaysResults.postValue(supportFunctions.sortMapByDateDescending(updatedMap))
    }

    override fun getTodaysResult(): Int {
        val currentDate = supportFunctions.getCurrentDate()
        return sharedPreferences.getInt(currentDate, 0)
    }

    override fun getAllDaysResults(): LiveData<Map<String, Int>> = _allDaysResults

    // egusev unused function
    fun updateResult(date: String, score: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt(date, score).apply()

        // Обновляем LiveData
        val updatedMap = sharedPreferences.all
            .filterValues { it is Int }
            .mapValues { it.value as Int }
        _allDaysResults.postValue(updatedMap)
    }



    // egusev unused function, extra tab, magic constants
        private fun uploadTestData() {
            val testGameResults = mapOf(
                "2024-01-01" to 100,
                "2024-01-02" to 250,
                "2024-01-03" to 175,
                "2024-01-04" to 300,
                "2024-01-05" to 450,
                "2024-01-06" to 50,
                "2024-01-07" to 200,
                "2024-01-08" to 350,
                "2024-01-09" to 400,
                "2024-01-10" to 125,
                "2024-01-11" to 275,
                "2024-01-12" to 325,
                "2024-01-13" to 100,
                "2024-01-14" to 150,
                "2024-01-15" to 475,
                "2024-01-16" to 50,
                "2024-01-17" to 300,
                "2024-01-18" to 250,
                "2024-01-19" to 600,
                "2024-01-20" to 225,
                "2024-01-21" to 125,
                "2024-01-22" to 500,
                "2024-01-23" to 450,
                "2024-01-24" to 150,
                "2024-01-25" to 275,
                "2024-01-26" to 175,
                "2024-01-27" to 200,
                "2024-01-28" to 300,
                "2024-01-29" to 50,
                "2024-01-30" to 400,
                "2024-01-31" to 350,
                "2024-02-01" to 425,
                "2024-02-02" to 125,
                "2024-02-03" to 250,
                "2024-02-04" to 325,
                "2024-02-05" to 100,
                "2024-02-06" to 275,
                "2024-02-07" to 500,
                "2024-02-08" to 150,
                "2024-02-09" to 50,
                "2024-02-10" to 600,
                "2024-02-11" to 175,
                "2024-02-12" to 200,
                "2024-02-13" to 225,
                "2024-02-14" to 300,
                "2024-02-15" to 400,
                "2024-02-16" to 450,
                "2024-02-17" to 125,
                "2024-02-18" to 350
            )
            val editor = sharedPreferences.edit()
            testGameResults.forEach { (date, score) ->
                editor.putInt(date, score) // Сохраняем каждую пару (дата, очки)
            }
            editor.apply() // Применяем изменения
        }


}