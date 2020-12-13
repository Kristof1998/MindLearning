package hu.droth.kristof.mindlearning.view.game.learning.viewModels

import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.droth.kristof.mindlearning.model.LanguageType
import hu.droth.kristof.mindlearning.model.WordTheme
import hu.droth.kristof.mindlearning.model.entity.KnownWord
import hu.droth.kristof.mindlearning.model.helperClasses.WordWithMeanings
import hu.droth.kristof.mindlearning.repository.KnownWordRepository
import hu.droth.kristof.mindlearning.repository.WordRepository
import hu.droth.kristof.mindlearning.util.SharedPreferencesUtil
import hu.droth.kristof.mindlearning.util.getFlagDrawable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class GameTestViewModel @ViewModelInject constructor(
    private val wordRepository: WordRepository,
    private val sharedPreferencesUtil: SharedPreferencesUtil,
    private val knownWordRepository: KnownWordRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val allWordList = mutableListOf<WordWithMeanings>()
    private val wordTheme: WordTheme = getWordTheme()

    private val currentLearningLanguage = getLearningLanguage()

    private var currentWordIndex: Int = 0
    private val playerId = getPlayerId()
    private val flagList: MutableList<Pair<LanguageType, Int>> = mutableListOf()

    val flagDrawableId: MutableLiveData<Int?> = MutableLiveData(null)
    val learningWordText: MutableLiveData<String> = MutableLiveData("")
    val inProgressText: MutableLiveData<String> = MutableLiveData("")
    val shouldNavigateMainScreen: MutableLiveData<Boolean> = MutableLiveData(false)
    val shouldShowEmpty: MutableLiveData<Boolean> = MutableLiveData(false)

    init {
        viewModelScope.launch(Dispatchers.Main) {
            initFlagList()
            initWordList(wordTheme)
            deleteKnownWordListFromDatabase()
            setMutableData()
        }
    }


    private suspend fun deleteKnownWordListFromDatabase() {
        allWordList.forEach {
            knownWordRepository.deleteKnownWordByWordIdAndPlayerId(it.wordData.wordId, playerId)
        }

    }

    fun btnIKnowButtonClicked() {
        val newKnownWord = KnownWord(
            wordId = allWordList[currentWordIndex].wordData.wordId,
            playerId = playerId
        )
        insertKnownWordInDatabase(newKnownWord)
        setNextWord()
    }

    fun btnLearnItButtonClicked() {
        setNextWord()
    }

    private fun getWordTheme(): WordTheme {
        return savedStateHandle.get<WordTheme>("wordTheme")
            ?: throw NullPointerException("WordTheme arguments not found")
    }

    private fun initFlagList() {
        enumValues<LanguageType>().forEach {
            flagList.add(Pair(it, it.getFlagDrawable()))
        }
    }

    private fun getPlayerId(): Long = sharedPreferencesUtil.getCurrentPlayerId()

    private fun getLearningLanguage(): LanguageType =
        sharedPreferencesUtil.getCurrentLearningLanguageType()


    private suspend fun initWordList(wordTheme: WordTheme) {
        allWordList.clear()
        val data = wordRepository.getWordWithMeaningsByThemeSuspended(wordTheme)
        allWordList.addAll(data)
    }

    private fun insertKnownWordInDatabase(knownWord: KnownWord) {
        viewModelScope.launch(Dispatchers.Main) {
            val insertId = knownWordRepository.insert(knownWord)
            Log.d("GAME_TEST_VIEW_MODEL", "KnownWord inserted with followingID:$insertId")
        }
    }

    private fun setNextWord() {
        currentWordIndex++
        if (currentWordIndex < allWordList.size) {
            setMutableData()
        } else {
            shouldNavigateMainScreen.postValue(true)
        }
    }

    private fun setMutableData() {
        if (allWordList.isEmpty()) {
            shouldShowEmpty.postValue(true)
        } else {
            learningWordText.postValue(allWordList[currentWordIndex].meanings.filter { it.languageType == currentLearningLanguage }[0].writing)
            flagDrawableId.postValue(flagList.filter { it.first == currentLearningLanguage }[0].second)
            inProgressText.postValue("${currentWordIndex + 1}/${allWordList.size}")
        }
    }
}
