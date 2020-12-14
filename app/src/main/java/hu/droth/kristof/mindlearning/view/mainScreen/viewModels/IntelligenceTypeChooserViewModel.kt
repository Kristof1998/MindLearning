package hu.droth.kristof.mindlearning.view.mainScreen.viewModels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.droth.kristof.mindlearning.model.IntelligenceType
import hu.droth.kristof.mindlearning.model.entity.Fill
import hu.droth.kristof.mindlearning.repository.FillRepository
import hu.droth.kristof.mindlearning.util.SharedPreferencesUtil
import kotlinx.coroutines.launch
import javax.inject.Inject

class IntelligenceTypeChooserViewModel @ViewModelInject constructor(
    private val fillRepository: FillRepository,
    private val sharedPreferencesUtil: SharedPreferencesUtil
) :
    ViewModel() {

    private var playerId: Long = 0
    private val _recommendedLearningType = MutableLiveData<IntelligenceType>()

    val recommendedLearningType: LiveData<IntelligenceType>
        get() = _recommendedLearningType


    val wordThemeText: MutableLiveData<String> = MutableLiveData("")

    init {
        viewModelScope.launch {
            initializePlayerId()
            val fillData = getFillsByPlayer()
            val fillsByIntelligenceType = getFillsByIntelligenceType(fillData)
            getBestFillPerformanceIntelligenceType(fillsByIntelligenceType)
        }

    }

    private fun initializePlayerId() {
        playerId = sharedPreferencesUtil.getCurrentPlayerId()
    }


    private fun getBestFillPerformanceIntelligenceType(data: List<Pair<IntelligenceType, List<Fill>>>) {
        val result = data.map { dataMap ->
            if (dataMap.second.isNotEmpty()) {
                val correct = dataMap.second.filter { it.correct }.size
                val performance = correct / dataMap.second.size.toDouble()
                Pair(dataMap.first, performance)
            } else {
                Pair(dataMap.first, 0.0)
            }
        }
        val sorted = result.sortedByDescending { it.second }
        val recommendedIntelligenceType = if(sorted.isEmpty()){
            IntelligenceType.VERBAL
        }else{
            sorted[0].first
        }
       _recommendedLearningType.postValue(recommendedIntelligenceType)
    }


    private suspend fun getFillsByPlayer(): List<Fill> {
        return fillRepository.getAllFillByPlayerId(playerId)
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

}