package hu.droth.kristof.mindlearning.view.game.checker.viewModels

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import hu.droth.kristof.mindlearning.repository.FillRepository
import hu.droth.kristof.mindlearning.repository.SessionRepository
import hu.droth.kristof.mindlearning.repository.WordRepository
import java.util.*
import kotlin.math.ceil

class GameCheckingVerbalViewModel @ViewModelInject constructor(
    @Assisted savedStateHandle: SavedStateHandle,
    fillRepository: FillRepository,
    sessionRepository: SessionRepository,
    wordRepository: WordRepository
) : GameBaseCheckingViewModel(
    savedStateHandle = savedStateHandle,
    fillRepository = fillRepository,
    sessionRepository = sessionRepository,
    wordRepository = wordRepository
) {
    private val _missingWordData: MutableLiveData<String> = MutableLiveData("")

    val missingWordData: LiveData<String>
        get() = _missingWordData

    override fun setMutableData() {
        super.setMutableData()
        _missingWordData.postValue(getMissingWordData())
    }

    private fun getMissingWordData(): String {
        val solution =
            learningWords[currentWordIndex].meanings.find { it.languageType == currentLearningLanguage }?.writing
                ?: throw NullPointerException("Solution not found in")
        val length = ceil(solution.length / 2.0).toInt()
        val rv = Array(solution.length) { it }.toMutableList()
        rv.shuffle()
        val randomValues = rv.subList(0, length)

        val sb = StringBuilder(solution.length)
        for (i in solution.indices) {
            val currentChar = if (i in randomValues) {
                '_'
            } else {
                solution[i]
            }
            sb.append(currentChar)
        }
        return sb.toString().capitalize(Locale.ROOT)
    }


}


