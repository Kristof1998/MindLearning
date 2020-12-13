package hu.droth.kristof.mindlearning.view.mainScreen.viewModels

import android.content.res.Resources
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import hu.droth.kristof.mindlearning.model.Gender
import hu.droth.kristof.mindlearning.model.IntelligenceType
import hu.droth.kristof.mindlearning.model.WordTheme
import hu.droth.kristof.mindlearning.model.entity.Fill
import hu.droth.kristof.mindlearning.model.entity.Player
import hu.droth.kristof.mindlearning.model.entity.Word
import hu.droth.kristof.mindlearning.model.helperClasses.InProgressCourse
import hu.droth.kristof.mindlearning.repository.FillRepository
import hu.droth.kristof.mindlearning.repository.PlayerRepository
import hu.droth.kristof.mindlearning.repository.SessionRepository
import hu.droth.kristof.mindlearning.repository.WordRepository
import hu.droth.kristof.mindlearning.util.SharedPreferencesUtil
import kotlinx.coroutines.launch

class ProfileFragmentViewModel @ViewModelInject constructor(
    private val playerRepository: PlayerRepository,
    private val sessionRepository: SessionRepository,
    private val fillRepository: FillRepository,
    private val wordRepository: WordRepository,
    private val sharedPreferencesUtil: SharedPreferencesUtil
) : ViewModel() {

    private val TAG = "PROFILE_FRAGMENT_VIEW_MODEL"
    private var playerId: Long = 0
    val shouldShowProgressBar = MutableLiveData(false)
    private var _inProgressCourses = MutableLiveData<List<InProgressCourse>>()
    private var _accounts = MutableLiveData<List<Player>>()

    private var _currentPlayer = MutableLiveData<Player>()
    private val _fillsByIntelligenceType =
        MutableLiveData<List<Pair<IntelligenceType, List<Fill>>>>()
    private val _fillsByWordTheme = MutableLiveData<List<Pair<WordTheme, List<Fill>>>>()
    private val _shouldCreateExportFile = MutableLiveData<String>()

    val accounts: LiveData<List<Player>>
        get() = _accounts
    val inProgressCourses: LiveData<List<InProgressCourse>>
        get() = _inProgressCourses
    val fillsByIntelligenceType: LiveData<List<Pair<IntelligenceType, List<Fill>>>>
        get() = _fillsByIntelligenceType
    val fillsByWordTheme: LiveData<List<Pair<WordTheme, List<Fill>>>>
        get() = _fillsByWordTheme
    val currentPlayer: LiveData<Player>
        get() = _currentPlayer
    val shouldCreateExportFile: LiveData<String>
        get() = _shouldCreateExportFile

    init {
        viewModelScope.launch {
            playerId = initPlayerId()
            refreshData()
        }
    }

    private fun initPlayerId(): Long {
        return sharedPreferencesUtil.getCurrentPlayerId()
    }

    private suspend fun setAccounts() {
        _accounts.postValue(playerRepository.getAllPlayer())
    }

    private suspend fun setCurrentPlayer() {
        Log.d(TAG, "CurrentPlayerId: $playerId")
        _currentPlayer.postValue(playerRepository.getPlayerById(playerId))

    }

    private suspend fun setInProgressCourses() {
        _inProgressCourses.postValue(sessionRepository.getInProgressSessions(playerId))
    }

    private suspend fun setPieChartData() {
        val allFillData = fillRepository.getAllFillByPlayerId(playerId)
        val wordList: MutableList<Word> = mutableListOf()
        allFillData.forEach { fill ->
            val word = wordRepository.getWordById(fill.wordId) ?: throw Resources.NotFoundException(
                "Word not found in wordRepository"
            )
            wordList.add(word)
        }
        _fillsByWordTheme.postValue(getFillsByTheme(allFillData, wordList))
        _fillsByIntelligenceType.postValue(getFillsByIntelligenceType(allFillData))
    }

    private fun getFillsByIntelligenceType(fillData: List<Fill>): List<Pair<IntelligenceType, List<Fill>>> {
        val resultList: MutableList<Pair<IntelligenceType, List<Fill>>> = mutableListOf()
        resultList.add(
            Pair(
                IntelligenceType.VERBAL,
                fillData.filter { it.session.intelligenceType == IntelligenceType.VERBAL })
        )
        resultList.add(
            Pair(
                IntelligenceType.LOGICAL,
                fillData.filter { it.session.intelligenceType == IntelligenceType.LOGICAL })
        )
        resultList.add(
            Pair(
                IntelligenceType.MUSICAL,
                fillData.filter { it.session.intelligenceType == IntelligenceType.MUSICAL })
        )
        resultList.add(
            Pair(
                IntelligenceType.VISUAL,
                fillData.filter { it.session.intelligenceType == IntelligenceType.VISUAL })
        )
        resultList.add(
            Pair(
                IntelligenceType.VISUAL_HARD,
                fillData.filter { it.session.intelligenceType == IntelligenceType.VISUAL_HARD })
        )
        resultList.removeIf { it.second.isEmpty() }
        return resultList
    }

    private fun getFillsByTheme(
        fillData: List<Fill>,
        wordList: List<Word>
    ): List<Pair<WordTheme, List<Fill>>> {
        val resultList: MutableList<Pair<WordTheme, List<Fill>>> = mutableListOf()
        val wordThemeWithFill = fillData.map { fill ->
            val wordTheme = wordList.find { it.wordId == fill.wordId }!!.theme
            Pair(wordTheme, fill)
        }
        resultList.add(
            Pair(
                WordTheme.ANIMAL,
                wordThemeWithFill.filter { it.first == WordTheme.ANIMAL }.map { it.second })
        )
        resultList.add(
            Pair(
                WordTheme.ART,
                wordThemeWithFill.filter { it.first == WordTheme.ART }.map { it.second })
        )
        resultList.add(
            Pair(
                WordTheme.COLOR,
                wordThemeWithFill.filter { it.first == WordTheme.COLOR }.map { it.second })
        )
        resultList.add(
            Pair(
                WordTheme.CLOTHING,
                wordThemeWithFill.filter { it.first == WordTheme.CLOTHING }.map { it.second })
        )
        resultList.add(
            Pair(
                WordTheme.FAMILY,
                wordThemeWithFill.filter { it.first == WordTheme.FAMILY }.map { it.second })
        )
        resultList.add(
            Pair(
                WordTheme.HUMAN_BODY,
                wordThemeWithFill.filter { it.first == WordTheme.HUMAN_BODY }.map { it.second })
        )
        resultList.add(
            Pair(
                WordTheme.JOB,
                wordThemeWithFill.filter { it.first == WordTheme.JOB }.map { it.second })
        )
        resultList.add(
            Pair(
                WordTheme.MUSIC,
                wordThemeWithFill.filter { it.first == WordTheme.MUSIC }.map { it.second })
        )
        resultList.add(
            Pair(
                WordTheme.SCHOOL,
                wordThemeWithFill.filter { it.first == WordTheme.SCHOOL }.map { it.second })
        )
        resultList.add(
            Pair(
                WordTheme.WEATHER,
                wordThemeWithFill.filter { it.first == WordTheme.WEATHER }.map { it.second })
        )
        resultList.removeIf { it.second.isEmpty() }
        return resultList
    }


    fun updateCurrentPlayer(selectedAccountName: String) {
        viewModelScope.launch {
            val player = playerRepository.getPlayerByName(selectedAccountName)!!
            playerId = player.playerId
            refreshData()
        }
    }

    private suspend fun refreshData() {
        sharedPreferencesUtil.setCurrentPlayerId(playerId)
        setCurrentPlayer()
        setInProgressCourses()
        setAccounts()
        setPieChartData()
    }

    fun createNewUser(name: String, checked: Boolean) {
        viewModelScope.launch {
            val gender = if (checked) {
                Gender.FEMALE
            } else {
                Gender.MALE
            }
            val newUser = Player(name, gender)
            //set new player values
            playerId = playerRepository.insert(newUser)
            refreshData()
        }
    }

    fun exportStatistic(
        allPlayer: Boolean,
        sessionData: Boolean,
        playerData: Boolean,
        wordData: Boolean,
        fillData: Boolean
    ) {
        viewModelScope.launch {
            val session = if (sessionData) {
                if (allPlayer) {
                    sessionRepository.getAllSession()
                } else {
                    sessionRepository.getSessionByPlayerId(playerId)
                }
            } else {
                emptyList()
            }
            val player = if (playerData) {
                if (allPlayer) {
                    playerRepository.getAllPlayer()
                } else {
                    listOf(playerRepository.getPlayerById(playerId))
                }
            } else {
                emptyList()
            }
            val word = if (wordData) {
                wordRepository.getAllWords()
            } else {
                emptyList()
            }
            val fill = if (fillData) {
                if (allPlayer) {
                    fillRepository.getAllFill()
                } else {
                    fillRepository.getAllFillByPlayerId(playerId)
                }
            } else {
                emptyList()
            }
            val gson = GsonBuilder().setPrettyPrinting().create()
            val resultText = StringBuilder("")
            resultText.append(gson.toJson(session))
            resultText.append(gson.toJson(player))
            resultText.append(gson.toJson(word))
            resultText.append(gson.toJson(fill))
            _shouldCreateExportFile.postValue(resultText.toString())
        }
    }


}