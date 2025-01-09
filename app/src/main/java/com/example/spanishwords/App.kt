package com.example.spanishwords

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.room.Room
import com.example.spanishwords.di.dataModule
import com.example.spanishwords.di.domainModule
import com.example.spanishwords.di.presentationModule
import com.example.spanishwords.game.data.database.AppDatabase
import com.example.spanishwords.settings.domain.api.SettingsInteractor
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import org.koin.java.KoinJavaComponent.getKoin
import java.io.FileOutputStream
import java.io.InputStream


class App : Application() {
    override fun onCreate() {
        super.onCreate()

        appContext = applicationContext

        startKoin {
            androidContext(this@App)
            modules(dataModule, domainModule, presentationModule)
        }
        copyDatabaseFromAssets(this, "dictionary.dp")

        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "dictionary.dp"  // Название локальной базы данных
        ).build()

        val settingsInteractor: SettingsInteractor = getKoin().get()
        val isNightModeOn = settingsInteractor.getThemeSettings()
        AppCompatDelegate.setDefaultNightMode(
            if (isNightModeOn) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )

    }
    fun copyDatabaseFromAssets(context: Context, dbName: String) {
        val dbPath = context.getDatabasePath(dbName)

        // Проверяем, существует ли уже база данных
        if (dbPath.exists()) {
            return // База данных уже скопирована
        }

        // Создаем папку для базы данных, если её нет
        dbPath.parentFile?.mkdirs()

        // Копирование файла
        val inputStream: InputStream = context.assets.open("databases/$dbName")
        val outputStream = FileOutputStream(dbPath)

        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
    }

    companion object {
        lateinit var database: AppDatabase
        lateinit var appContext: Context
            private set
    }
}