package hu.droth.kristof.mindlearning.view.splashScreen

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.droth.kristof.mindlearning.R
import hu.droth.kristof.mindlearning.model.entity.Language
import hu.droth.kristof.mindlearning.model.helperClasses.ReaderWordData
import hu.droth.kristof.mindlearning.repository.LanguageRepository
import hu.droth.kristof.mindlearning.repository.MeaningRepository
import hu.droth.kristof.mindlearning.repository.WordRepository
import hu.droth.kristof.mindlearning.util.FileReaderUtil
import hu.droth.kristof.mindlearning.util.SharedPreferencesUtil
import hu.droth.kristof.mindlearning.util.WordDataUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SplashScreenViewModel @ViewModelInject constructor(
    private val wordRepository: WordRepository,
    private val meaningRepository: MeaningRepository,
    private val languageRepository: LanguageRepository,
    private val fileReaderUtil: FileReaderUtil,
    private val sharedPreferencesUtil: SharedPreferencesUtil
) : ViewModel() {

    private val TAG = "SPLASH_SCREEN_VIEW_MODEL"

    val shouldShowProgressBarWithText = MutableLiveData(false)
    val progressBarText = MutableLiveData("")
    val shouldShowTapToContinue = MutableLiveData(false)
    val isFirstRun = getFirstRun()
    val hasCurrentPlayer = hasCurrentPlayer()
    private val databaseInitialized = isDatabaseInitialized()


    fun initializeData() {
        if (databaseInitialized) {//Break code if database has been initialized
            showTapToContinue()
            return
        }
        viewModelScope.launch(Dispatchers.Main) {
            showProgressBar("Read data from file...")
            val wordList = readWordData()
            val languageList = readLanguageData()
            showProgressBar("Initialize database...")
            insertLanguageInDatabase(languageList)
            insertWordsInDatabase(wordList)
            setDatabaseInitialized()
            hideProgressBar()
            showTapToContinue()
        }
    }

    private suspend fun insertWordsInDatabase(wordList: List<ReaderWordData>) {
        wordList.forEach { readerWordData ->
            Log.d(TAG, "Readed word data:${readerWordData.meanings}")
            val insertableWord = WordDataUtil.setWordData(readerWordData.wordTheme)
            val wordId = wordRepository.insert(insertableWord)
            Log.d(TAG, "Word inserted with following ID:$wordId")
            readerWordData.meanings.forEach { meaning ->
                val insertableMeaning = WordDataUtil.setMeaningData(
                    wordId = wordId,
                    writing = meaning.value,
                    languageType = meaning.key
                )
                val meaningId = meaningRepository.insert(insertableMeaning)
                Log.d(TAG, "Meaning (wordID=$wordId) inserted with following ID:$meaningId")
            }
        }
    }

    private suspend fun insertLanguageInDatabase(languageList: List<Language>) {
        val idList = languageRepository.insert(languageList)
        Log.d(TAG, "Language inserted with following ID: $idList")
    }

    private suspend fun readWordData(): List<ReaderWordData> {
        val data = fileReaderUtil.readInitializeDataFromFile(R.raw.words)
        return fileReaderUtil.parseDataToReaderWordDataList(data)
    }

    private suspend fun readLanguageData(): List<Language> {
        val data = fileReaderUtil.readInitializeDataFromFile(R.raw.languages)
        return fileReaderUtil.parseDataToLanguageList(data)
    }

    private fun isDatabaseInitialized(): Boolean = sharedPreferencesUtil.isDatabaseInitialized()

    private fun getFirstRun(): Boolean = sharedPreferencesUtil.isFirstRun()

    private fun setDatabaseInitialized() {
        sharedPreferencesUtil.setDatabaseInitialized(true)
    }

    private fun hasCurrentPlayer(): Boolean {
        val currentPlayerID = sharedPreferencesUtil.getCurrentPlayerId()
        return currentPlayerID != -1L
    }

    private fun showProgressBar(text: String) {
        progressBarText.postValue(text)
        shouldShowProgressBarWithText.postValue(true)
    }

    private fun hideProgressBar() {
        shouldShowProgressBarWithText.postValue(false)
    }

    private fun showTapToContinue() {
        shouldShowTapToContinue.value = true
    }

    fun setFirstRunToFalse() {
        sharedPreferencesUtil.setFirstRun(false)
    }

}