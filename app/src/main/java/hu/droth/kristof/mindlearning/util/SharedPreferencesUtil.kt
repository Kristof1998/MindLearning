package hu.droth.kristof.mindlearning.util

import android.content.Context
import android.content.Context.MODE_PRIVATE
import hu.droth.kristof.mindlearning.model.LanguageType

class SharedPreferencesUtil(applicationContext: Context) {
    private val applicationData = "APPLICATION_DATA"
    private val firstRun = "FIRST_RUN"
    private val currentPlayer = "CURRENT_PLAYER"
    private val databaseInitialized = "DATABASE_INITIALIZED"
    private val currentLearningLanguage = "CURRENT_LEARNING_LANGUAGE"
    private val currentDefaultLanguage = "CURRENT_DEFAULT_LANGUAGE"
    private val sharedPreferences =
        applicationContext.getSharedPreferences(applicationData, MODE_PRIVATE)


    fun isFirstRun(): Boolean = sharedPreferences.getBoolean(firstRun, true)
    fun setFirstRun(first: Boolean) {
        sharedPreferences.edit().putBoolean(firstRun, first).apply()
    }

    fun getCurrentPlayerId(): Long = sharedPreferences.getLong(currentPlayer, -1)


    fun setCurrentPlayerId(playerId: Long) {
        sharedPreferences.edit().putLong(currentPlayer, playerId).apply()
    }

    fun isDatabaseInitialized(): Boolean = sharedPreferences.getBoolean(databaseInitialized, false)

    fun setDatabaseInitialized(isInitialized: Boolean) {
        sharedPreferences.edit().putBoolean(databaseInitialized, isInitialized).apply()
    }

    fun getCurrentLearningLanguageType(): LanguageType = LanguageType.valueOf(sharedPreferences.getString(currentLearningLanguage, LanguageType.ENGLISH.name)!!)

    fun getCurrentDefaultLanguageType(): LanguageType = LanguageType.valueOf(sharedPreferences.getString(currentDefaultLanguage, LanguageType.HUNGARIAN.name)!!)
}

