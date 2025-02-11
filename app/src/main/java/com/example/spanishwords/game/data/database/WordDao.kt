package com.example.spanishwords.game.data.database


import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import com.example.spanishwords.game.data.WordEntity
import com.example.spanishwords.game.domain.models.LanguageLevel
import com.example.spanishwords.game.domain.models.WordCategory
import com.example.spanishwords.game.presentation.models.DifficultLevel
import com.example.spanishwords.game.presentation.models.Language

// egusev Doesn't seem that suspend is necessary, maybe I'm lacking understanding
//  since you're not calling any suspend functions inside your suspend functions anywhere except
//  `viewModelScope.launch`, which already is async, so I think removing suspend
//  achieves the same result?
//
//  UPD: I've checked and I'm wrong :D, don't listen to what I said above
//  btw, recommend this video highly: https://www.youtube.com/watch?v=rB5Q3y73FTo
//  and this article is good for android specific:
//  https://developer.android.com/kotlin/coroutines/coroutines-best-practices
//   And the whole chapter: https://developer.android.com/kotlin/coroutines is good too
@Dao
interface WordDao {

    // egusev why hardcoded limit 36? better to make a param
    // also, why COLLATE NOCASE? Better to make sure what's in the db is in the correct format
    @Query("SELECT * FROM A1 WHERE category = :category COLLATE NOCASE LIMIT 36")
    suspend fun getWordsByCategory(category: WordCategory): List<WordEntity>

    @Query("SELECT * FROM A1 ORDER BY RANDOM() LIMIT :wordsNeeded")
    suspend fun getRandom(wordsNeeded: Int): List<WordEntity>




    @Update
    suspend fun updateWord(wordEntity: WordEntity)

}