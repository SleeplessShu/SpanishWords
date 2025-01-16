package com.example.spanishwords.game.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.spanishwords.game.data.WordEntity

@Database(entities = [WordEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
}