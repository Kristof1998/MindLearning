package hu.droth.kristof.mindlearning.view.game.checker.viewModels

import android.util.Log
import androidx.lifecycle.*
import hu.droth.kristof.mindlearning.model.LanguageType
import hu.droth.kristof.mindlearning.model.entity.Fill
import hu.droth.kristof.mindlearning.model.entity.Meaning
import hu.droth.kristof.mindlearning.model.entity.Session
import hu.droth.kristof.mindlearning.model.helperClasses.WordWithMeanings
import hu.droth.kristof.mindlearning.repository.FillRepository
import hu.droth.kristof.mindlearning.repository.SessionRepository
import hu.droth.kristof.mindlearning.repository.WordRepository
import hu.droth.kristof.mindlearning.util.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

abstract class GameBaseCheckingViewModel constructor(
    val savedStateHandle: SavedStateHandle,
    private val fillRepository: FillRepository,
    private val sessionRepository: SessionRepository,
    private val wordRepository: WordRepository
) : ViewModel() {


    private val TAG = "GAME_BASE_CHECKING_VIEW_MODEL"

    protected var learningWords: MutableList<WordWithMeanings> = mutableListOf()
    protected val currentLearningLanguage: LanguageType = getCurrentLearningLanguageType()
    private val currentDefaultLanguage: LanguageType = getCurrentDefaultLanguageType()
    private val flagList: MutableList<Pair<LanguageType, Int>> = getFlagList()
    private val wordByThemeAndTypeSize: Int = getWordByThemeAndTypeSize()
    protected var currentWordIndex = 0


    private lateinit var currentSession: Session
    private val _ownWordText: MutableLiveData<String> = MutableLiveData("")
    private val _solutionWordText: MutableLiveData<String> = MutableLiveData("")
    private val _ownWordFlagDrawable: MutableLiveData<Int?> = MutableLiveData<Int?>(null)
    private val _solutionFlagDrawable: MutableLiveData<Int?> = MutableLiveData<Int?>(null)
    private val _wordNumberText: MutableLiveData<String> = MutableLiveData("")
    private val _shouldGameEnd: MutableLiveData<Event<Boolean>> = MutableLiveData()
    private val _currentWordWithMeanings: MutableLiveData<WordWithMeanings?> = MutableLiveData(null)
    val shouldShowAnswer: MutableLiveData<Triple<String, LanguageType, Boolean>?> =
        MutableLiveData(null)
    private val _currentMeaningData: MutableLiveData<Meaning> = MutableLiveData()

    init {
        viewModelScope.launch {
            getCurrentSession()
            getWordData()
            updateUI()
        }
    }

    val ownWordText: LiveData<String>
        get() = _ownWordText
    val solutionWordText: LiveData<String>
        get() = _solutionWordText
    val ownWordFlagDrawable: LiveData<Int?>
        get() = _ownWordFlagDrawable
    val solutionWordFlagDrawable: LiveData<Int?>
        get() = _solutionFlagDrawable
    val wordNumberText: LiveData<String>
        get() = _wordNumberText
    val shouldGameEnd: LiveData<Event<Boolean>>
        get() = _shouldGameEnd
    val currentWordWithMeanings: LiveData<WordWithMeanings?>
        get() = _currentWordWithMeanings
    val currentMeaningData: LiveData<Meaning>
        get() = _currentMeaningData

    private var currentAnswer = ""

    private fun getWordByThemeAndTypeSize(): Int {
        return savedStateHandle.get<Int>("wordByThemeAndTypeSize")
            ?: throw  NullPointerException("WordByThemeAndTypeSize argument not found")
    }

    private val playerId: Long = getPlayerId()


    private suspend fun getWordData() {
        val lw = savedStateHandle.get<List<Long>>("learningWordsId") ?: throw  NullPointerException(
            "Learning words argument not found"
        )
        Log.d(TAG, "LearningWordIdList size:${lw.size}")
        lw.forEach {
            learningWords.add(wordRepository.geWordWithMeaningsByWordId(it))
        }
    }

    private suspend fun getCurrentSession() {
        val sessionId = savedStateHandle.get<Long>("currentSessionId")
            ?: throw  NullPointerException("Current session argument not found")
        val session = sessionRepository.getSessionById(sessionId)
        Log.d("GAME_BASE_CHECKING_VIEW_MODEL", "currentSession:$session")
        currentSession = session

    }

    private fun getPlayerId(): Long {
        return savedStateHandle.get<Long>("playerId")
            ?: throw  NullPointerException("Player id argument not found")
    }

    private fun getCurrentLearningLanguageType(): LanguageType {
        return savedStateHandle.get<LanguageType>("currentLearningLanguage")
            ?: throw  NullPointerException("Current learning language argument not found")
    }

    private fun getCurrentDefaultLanguageType(): LanguageType {
        return savedStateHandle.get<LanguageType>("currentDefaultLanguage")
            ?: throw  NullPointerException("Current default language argument not found")
    }

    private fun getFlagList(): MutableList<Pair<LanguageType, Int>> {
        return savedStateHandle.get<MutableList<Pair<LanguageType, Int>>>("flagList")
            ?: throw  NullPointerException("Learning words argument not found")
    }


    private fun setShouldShowAnswer(solution: String, isValid: Boolean) {
        shouldShowAnswer.postValue(Triple(solution, currentLearningLanguage, isValid))
    }

    fun getNextCheckableWord() {
        if (learningWords.isNotEmpty()) {
            val solution =
                learningWords[currentWordIndex].meanings.find { it.languageType == currentLearningLanguage }?.writing
                    ?: throw NullPointerException("Solution not found in")
            val wordData = learningWords[currentWordIndex]
            val isValid = currentAnswer.equals(solution, true)
            if (isValid) {
                learningWords.remove(wordData)
            }
            if (learningWords.isEmpty()) {
                setGameEnd()
            } else {
                randomSortWords()
                updateUI()
            }
        }
    }

    private fun updateUI() {
        if (learningWords.isNotEmpty()) {
            setMutableData()
        }
    }

    private fun setGameEnd() {
        viewModelScope.launch {
            updateCurrentSessionProgressInDataBase()
            _shouldGameEnd.value = Event(true)
        }
    }

    private suspend fun updateCurrentSessionProgressInDataBase() {
        currentSession.apply {
            this.progress = 100
            this.inProgress = false
            this.lastModified = Date(System.currentTimeMillis())
        }
        val sessionUpdateResult = sessionRepository.update(currentSession)
        Log.d(TAG, "$sessionUpdateResult row updated in sessions")
    }

    open fun setMutableData() {
        _ownWordText.postValue(learningWords[currentWordIndex].meanings.filter { it.languageType == currentDefaultLanguage }[0].writing)
        _solutionWordText.postValue(learningWords[currentWordIndex].meanings.filter { it.languageType == currentLearningLanguage }[0].writing)
        _ownWordFlagDrawable.postValue(flagList.filter { it.first == currentDefaultLanguage }[0].second)
        _solutionFlagDrawable.postValue(flagList.filter { it.first == currentLearningLanguage }[0].second)
        _wordNumberText.postValue("${currentWordIndex + 1}/${learningWords.size}")
        _currentWordWithMeanings.postValue(learningWords[currentWordIndex])
        _currentMeaningData.postValue(learningWords[currentWordIndex].meanings.filter { it.languageType == currentLearningLanguage }[0])
    }

    private fun randomSortWords() {
        if (learningWords.size > 1) {
            learningWords.shuffle()
        }
    }


    private fun updateDatabase(fillWord: WordWithMeanings, isValid: Boolean) {
        viewModelScope.launch(Dispatchers.Main) {
            val newFill = Fill(
                playerId = playerId,
                wordId = fillWord.wordData.wordId,
                fillTime = Date(System.currentTimeMillis()),
                correct = isValid,
                session = currentSession
            )
            val fillInsertResult = fillRepository.insert(newFill)
            Log.d(TAG, "Fill successfully inserted with ID:$fillInsertResult and body: $newFill")
            updateCurrentSessionInDataBase(fillWord, isValid)
        }
    }


    private suspend fun updateCurrentSessionInDataBase(
        fillWord: WordWithMeanings,
        isValid: Boolean
    ) {
        val newProgress = if (isValid) {
            ((wordByThemeAndTypeSize - learningWords.size.toDouble() + 1) / wordByThemeAndTypeSize.toDouble()) * 100
        } else {
            currentSession.progress.toDouble()
        }
        val inProgress = learningWords.size != 0
        currentSession.apply {
            this.progress = newProgress.toInt()
            this.inProgress = inProgress
            this.lastWordId = fillWord.wordData.wordId
            this.lastModified = Date(System.currentTimeMillis())
        }
        val sessionUpdateResult = sessionRepository.update(currentSession)
        Log.d(TAG, "$sessionUpdateResult row updated in sessions")
    }

    fun checkAnswer(answer: String) {
        currentAnswer = answer
        val solution =
            learningWords[currentWordIndex].meanings.find { it.languageType == currentLearningLanguage }?.writing
                ?: throw NullPointerException("Solution not found in")
        val wordData = learningWords[currentWordIndex]
        val isValid = answer.equals(solution, true)
        updateDatabase(wordData, isValid)
        setShouldShowAnswer(solution, isValid)
    }


}
