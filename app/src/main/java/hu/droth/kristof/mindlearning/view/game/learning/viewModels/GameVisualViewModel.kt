package hu.droth.kristof.mindlearning.view.game.learning.viewModels


import android.content.res.Resources.NotFoundException
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import hu.droth.kristof.mindlearning.R
import hu.droth.kristof.mindlearning.model.BlurType
import hu.droth.kristof.mindlearning.model.entity.Word
import hu.droth.kristof.mindlearning.repository.*
import hu.droth.kristof.mindlearning.util.NetworkUtil
import hu.droth.kristof.mindlearning.util.SharedPreferencesUtil
import okio.IOException

class GameVisualViewModel @ViewModelInject constructor(
    private val imageSearchRepository: ImageSearchRepository,
    @Assisted savedStateHandle: SavedStateHandle,
    knownWordRepository: KnownWordRepository,
    wordRepository: WordRepository,
    sessionRepository: SessionRepository,
    fillRepository: FillRepository,
    sharedPreferencesUtil: SharedPreferencesUtil,
    private val networkUtil: NetworkUtil,
) : GameBaseLearningViewModel(
    checkerPageId = R.id.action_gameVisualFragment_to_gameCheckingVisual,
    savedStateHandle = savedStateHandle,
    knownWordRepository = knownWordRepository,
    wordRepository = wordRepository,
    sessionRepository = sessionRepository,
    fillRepository = fillRepository,
    sharedPreferencesUtil = sharedPreferencesUtil
) {
    private val TAG = "GAME_VISUAL_VIEW_MODEL"
    private val _shouldShowProgressBar: MutableLiveData<Boolean> = MutableLiveData(false)
    private val _progressBarText: MutableLiveData<String> = MutableLiveData("")

    val blurType = initBlurType()
    val shouldShowProgressBar: LiveData<Boolean>
        get() = _shouldShowProgressBar

    val progressBarText: LiveData<String>
        get() = _progressBarText

    private fun initBlurType(): BlurType {
        return savedStateHandle.get<BlurType>("blurType")
            ?: throw NotFoundException("Blur type not found in resources")
    }

    override suspend fun initCustomData() {
        setProgressBar("Loading images...")
        val networkConnected = networkUtil.isInternetConnected()
        if (!networkConnected) {
            throw IOException("Can not connected to internet")
        }
        for (i in 0 until learningWords.size) {
            setProgressBar("Loading images...\n$i/${learningWords.size}")
            val it = learningWords[i]
            val imageData: Pair<Long, String?>? =
                if (blurType == BlurType.NONE && it.wordData.imageUrl == null) {
                    imageSearchRepository.getImageUrlData(it, 0)
                } else if (blurType == BlurType.BLUR && it.wordData.blurImageUrl == null) {
                    imageSearchRepository.getImageUrlData(it, 1)
                } else {
                    null
                }
            if (imageData != null) {
                updateData(imageData)
            }
        }
        setProgressBar(null)
    }


    private suspend fun updateData(updateData: Pair<Long, String?>) {
        if (updateData.second != null) {
            val data = learningWords.find { it.wordData.wordId == updateData.first }
                ?: throw NotFoundException("Word not found in learningWord list")
            when (blurType) {
                BlurType.NONE -> data.wordData.imageUrl = updateData.second
                BlurType.BLUR -> data.wordData.blurImageUrl = updateData.second
            }
            updateDataInDataBase(data.wordData)
        }
    }

    private suspend fun updateDataInDataBase(wordData: Word) {
        Log.d(TAG, "Updatable word data: $wordData")
        val updateResult = wordRepository.update(wordData)
        Log.d(TAG, "Word updated in $updateResult row")
    }


    private fun setProgressBar(progressText: String?) {
        if (progressText == null) {
            _shouldShowProgressBar.postValue(false)
        } else {
            _shouldShowProgressBar.postValue(true)
            _progressBarText.postValue(progressText)
        }
    }
}