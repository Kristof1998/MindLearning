package hu.droth.kristof.mindlearning.view.game.learning.viewModels

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.annotation.IdRes
import androidx.core.os.bundleOf
import androidx.lifecycle.*
import hu.droth.kristof.mindlearning.model.IntelligenceType
import hu.droth.kristof.mindlearning.model.LanguageType
import hu.droth.kristof.mindlearning.model.WordTheme
import hu.droth.kristof.mindlearning.model.entity.Fill
import hu.droth.kristof.mindlearning.model.entity.KnownWord
import hu.droth.kristof.mindlearning.model.entity.Meaning
import hu.droth.kristof.mindlearning.model.entity.Session
import hu.droth.kristof.mindlearning.model.helperClasses.WordWithMeanings
import hu.droth.kristof.mindlearning.repository.FillRepository
import hu.droth.kristof.mindlearning.repository.KnownWordRepository
import hu.droth.kristof.mindlearning.repository.SessionRepository
import hu.droth.kristof.mindlearning.repository.WordRepository
import hu.droth.kristof.mindlearning.util.SharedPreferencesUtil
import hu.droth.kristof.mindlearning.util.getFlagDrawable
import hu.droth.kristof.mindlearning.view.game.learning.GameBaseLearningViewModelInterface
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

abstract class GameBaseLearningViewModel(
    @IdRes private val checkerPageId: Int,
    val savedStateHandle: SavedStateHandle,
    private val knownWordRepository: KnownWordRepository,
    protected val wordRepository: WordRepository,
    private val sessionRepository: SessionRepository,
    private val fillRepository: FillRepository,
    private val sharedPreferencesUtil: SharedPreferencesUtil
) : ViewModel(), GameBaseLearningViewModelInterface {
    private val TAG = "GAME_BASE_LEARNING_VIEW_MODEL"

    private val flagList: List<Pair<LanguageType, Int>> = initFlagList()
    private val playerId: Long = initPLayerId()
    private val wordTheme: WordTheme = getWordThemeFromArgs()
    private val currentLearningLanguage: LanguageType = getCurrentLearningLanguage()
    private val currentDefaultLanguage: LanguageType = getCurrentDefaultLanguage()
    private lateinit var currentSession: Session
    private lateinit var wordByThemeAndType: List<WordWithMeanings>
    protected val learningWords: MutableList<WordWithMeanings> = mutableListOf()
    private lateinit var knownWords: MutableList<KnownWord>
    private val intelligenceType = initIntelligenceType()

    private fun initIntelligenceType(): IntelligenceType {
        return savedStateHandle.get<IntelligenceType>("intelligenceType")
            ?: throw Resources.NotFoundException("Intelligence type arg not found")
    }

    private var currentWordIndex = 0


    private val _ownWordText: MutableLiveData<String> = MutableLiveData("")
    private val _learningWordText: MutableLiveData<String> = MutableLiveData("")
    private val _wordNumberText: MutableLiveData<String> = MutableLiveData("")
    private val _ownWordFlagDrawable: MutableLiveData<Int?> = MutableLiveData<Int?>(null)
    private val _learningWordFlagDrawable: MutableLiveData<Int?> = MutableLiveData<Int?>(null)
    private val _shouldShowEmptyScreen: MutableLiveData<Boolean?> = MutableLiveData(null)
    private val _previousButtonEnable: MutableLiveData<Boolean> = MutableLiveData(false)
    private val _nextButtonText: MutableLiveData<String> = MutableLiveData("Next")
    private val _currentWordData: MutableLiveData<Meaning> = MutableLiveData()
    private val _shouldGoCheckerPage: MutableLiveData<Pair<Int, Bundle>> = MutableLiveData()
    private val _currentWordWithMeaning: MutableLiveData<WordWithMeanings?> = MutableLiveData(null)

    val ownWordText: LiveData<String>
        get() = _ownWordText
    val learningWordText: LiveData<String>
        get() = _learningWordText
    val ownWordFlagDrawable: LiveData<Int?>
        get() = _ownWordFlagDrawable
    val learningWordFlagDrawable: LiveData<Int?>
        get() = _learningWordFlagDrawable
    val wordNumberText: LiveData<String>
        get() = _wordNumberText
    val shouldShowEmptyScreen: LiveData<Boolean?>
        get() = _shouldShowEmptyScreen
    val previousButtonEnable: LiveData<Boolean>
        get() = _previousButtonEnable
    val nextButtonText: LiveData<String>
        get() = _nextButtonText
    val shouldGoCheckerPage: LiveData<Pair<Int, Bundle>>
        get() = _shouldGoCheckerPage
    val currentWordData: LiveData<Meaning>
        get() = _currentWordData
    val currentWordWithMeanings: LiveData<WordWithMeanings?>
        get() = _currentWordWithMeaning


    init {
        viewModelScope.launch {
            setCurrentSession()
            initWords()
            initCustomData()
            updateUI()
        }
    }


    private suspend fun initWords() {
        learningWords.clear()
        val currentSessionId = currentSession.sessionId
        val filledItems = fillRepository.getFilledDataByCurrentSessionIdAndThemeAndPlayerId(
            currentSessionId,
            wordTheme,
            playerId
        )
        knownWords = knownWordRepository.getAllKnownWordsByPlayerId(playerId).toMutableList()
        wordByThemeAndType = wordRepository.getWordWithMeaningsByThemeSuspended(wordTheme)
        learningWords.addAll(
            removeKnownWordsAndFilledItemsFromAllWordList(
                wordByThemeAndType,
                knownWords,
                filledItems
            )
        )

    }

    private fun removeKnownWordsAndFilledItemsFromAllWordList(
        wordByThemeAndType: List<WordWithMeanings>,
        knownWords: MutableList<KnownWord>,
        filledItems: List<Fill>
    ): List<WordWithMeanings> {
        val knownWordIdList = knownWords.map { it.wordId }
        val filledItemIdList = filledItems.filter { it.correct }.map { it.wordId }
        return wordByThemeAndType.filter {
            it.wordData.wordId !in knownWordIdList
        }.filter { it.wordData.wordId !in filledItemIdList }

    }

    private suspend fun setCurrentSession() {
        val session = sessionRepository.getCurrentSessionByThemeAndIntelligenceAndPlayerId(
            wordTheme,
            intelligenceType,
            playerId
        )
        val sessionInProgress = session?.inProgress ?: false
        val currentSessionId = session?.session ?: 0
        currentSession = if (sessionInProgress) {
            session!!
        } else {
            createNewSession(currentSessionId)
        }
    }

    private suspend fun createNewSession(lastSession: Int): Session {
        val newSession = Session(
            intelligenceType = intelligenceType,
            languageType = currentLearningLanguage,
            wordTheme = wordTheme,
            lastWordId = null,
            session = lastSession + 1,
            progress = 0,
            inProgress = true,
            lastModified = Date(System.currentTimeMillis()),
            playerId = playerId
        )
        val newSessionInsertResult = sessionRepository.insert(newSession)
        Log.d(TAG, "New session inserted with following id:$newSessionInsertResult")
        newSession.sessionId = newSessionInsertResult
        return newSession
    }

    private fun getCurrentDefaultLanguage(): LanguageType {
        return sharedPreferencesUtil.getCurrentDefaultLanguageType()
    }

    private fun getCurrentLearningLanguage(): LanguageType {
        return sharedPreferencesUtil.getCurrentLearningLanguageType()
    }

    private fun getWordThemeFromArgs(): WordTheme {
        return savedStateHandle.get<WordTheme>("wordTheme")
            ?: throw NullPointerException("WordTheme arguments not found")

    }

    private fun initPLayerId(): Long {
        return sharedPreferencesUtil.getCurrentPlayerId()
    }

    private fun initFlagList(): List<Pair<LanguageType, Int>> {
        return enumValues<LanguageType>().map { Pair(it, it.getFlagDrawable()) }
    }

    fun resetLecture() {
        viewModelScope.launch {
            deleteLectureDataFromDatabase()
            knownWords.clear()
            learningWords.clear()
            learningWords.addAll(wordByThemeAndType)
            updateUI()
        }
    }

    private suspend fun deleteLectureDataFromDatabase() {
        knownWords.forEach {
            knownWordRepository.deleteKnownWordByWordIdAndPlayerId(it.knownWordId, playerId)
        }
        currentSession.apply {
            lastModified = Date(System.currentTimeMillis())
            inProgress = false
        }
        val newSession = createNewSession(currentSession.session)
        currentSession = newSession
    }

    private fun setMutableData() {
        if (learningWords.isNotEmpty() && currentWordIndex >= 0 && currentWordIndex < learningWords.size) {
            setMutableLiveData()
        }
    }

    open fun setMutableLiveData() {
        _ownWordText.postValue(learningWords[currentWordIndex].meanings.filter { it.languageType == currentDefaultLanguage }[0].writing)
        _learningWordText.postValue(learningWords[currentWordIndex].meanings.filter { it.languageType == currentLearningLanguage }[0].writing)
        _ownWordFlagDrawable.postValue(flagList.filter { it.first == currentDefaultLanguage }[0].second)
        _learningWordFlagDrawable.postValue(flagList.filter { it.first == currentLearningLanguage }[0].second)
        _wordNumberText.postValue("${currentWordIndex + 1}/${learningWords.size}")
        _currentWordData.postValue(learningWords[currentWordIndex].meanings.filter { it.languageType == currentLearningLanguage }[0])
        _currentWordWithMeaning.postValue(learningWords[currentWordIndex])
    }

    private fun updateUI() {
        setShouldShowEmpty()
        setPreviousButtonClickable()
        setNextButtonText()
        setMutableData()
        navigateToNextPage()
    }

    private fun navigateToNextPage() {

        if (learningWords.isNotEmpty() && currentWordIndex == learningWords.size) {
            val learningWordId = ArrayList<Long>(learningWords.map { it.wordData.wordId })
            val bundle = bundleOf(
                "flagList" to flagList,
                "currentDefaultLanguage" to currentDefaultLanguage,
                "currentLearningLanguage" to currentLearningLanguage,
                "playerId" to playerId,
                "currentSessionId" to currentSession.sessionId,
                "learningWordsId" to learningWordId,
                "wordByThemeAndTypeSize" to wordByThemeAndType.size,
                "intelligenceType" to intelligenceType
            )
            _shouldGoCheckerPage.value = Pair(checkerPageId, bundle)
        }
    }

    fun getNextWord() {
        currentWordIndex++
        updateUI()
    }

    fun getPreviousWord() {
        currentWordIndex--
        updateUI()
    }

    private fun setShouldShowEmpty() {
        _shouldShowEmptyScreen.postValue(learningWords.isEmpty())
    }

    private fun setPreviousButtonClickable() {
        _previousButtonEnable.postValue(currentWordIndex != 0)
    }

    private fun setNextButtonText() {
        val buttonText = if (currentWordIndex == learningWords.size - 1) {
            "Check"
        } else {
            "Next"
        }
        _nextButtonText.postValue(buttonText)
    }

    override suspend fun initCustomData() {}
}

