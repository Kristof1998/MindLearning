package hu.droth.kristof.mindlearning.view.authentication.fragments

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.droth.kristof.mindlearning.model.Gender
import hu.droth.kristof.mindlearning.model.entity.Player
import hu.droth.kristof.mindlearning.repository.PlayerRepository
import hu.droth.kristof.mindlearning.util.SharedPreferencesUtil
import kotlinx.coroutines.launch


class AddPersonalDataViewModel @ViewModelInject constructor(
    private val playerRepository: PlayerRepository,
    private val sharedPreferencesUtil: SharedPreferencesUtil
) : ViewModel() {
    private val TAG = "ADD_PERSONAL_DATA_VIEW_MODEL"

    var textViewNameEmpty: MutableLiveData<Boolean> = MutableLiveData(false)
    var currentPLayerName: MutableLiveData<String> = MutableLiveData("")
    var isGenderFemale: MutableLiveData<Boolean> = MutableLiveData(false)
    var shouldProgressbarVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    var shouldShowSnackbar = MutableLiveData<Boolean?>()
    var shouldHideKeyboard = MutableLiveData<Boolean?>()
    val successFullCreatePlayer = MutableLiveData<Boolean?>()

    private var playerExist: Boolean = false
    private var playerName: String = ""
    private var playerGender: Gender = Gender.MALE

    fun onCreateButtonClick() {
        shouldHideKeyboard.postValue(true)
        viewModelScope.launch {
            setPlayerProperties()
            if (!checkIsEmpty(playerName)) {
                startProgressBar()
                playerExist = playerRepository.isExist(playerName)
                if (!playerExist) {
                    Log.d(TAG, "Creating player...")
                    createPlayer(playerName, playerGender)
                } else {
                    Log.d(TAG, "Player exist in the database")
                    shouldShowSnackbar.postValue(true)
                }
                stopProgressbar()
            }
        }

    }

    private fun setPlayerProperties() {
        playerName = currentPLayerName.value?.trim()
            ?: throw NullPointerException("LiveData value(currentPlayerName) is null!")
        playerGender = isGenderFemale.value?.let {
            if (it) {
                Gender.FEMALE
            } else {
                Gender.MALE
            }
        } ?: throw  NullPointerException("LiveData value(currentPlayerName) is null!")
    }

    private fun createPlayer(playerName: String, gender: Gender) {
        viewModelScope.launch {
            val newPlayer = Player(name = playerName, gender = gender)
            val newPlayerId = playerRepository.insert(newPlayer)
            Log.d(TAG, "Player created with id: $newPlayerId")
            saveCurrentPlayerId(newPlayerId)
            successFullCreatePlayer()
        }
    }

    private fun saveCurrentPlayerId(playerId: Long) {
        sharedPreferencesUtil.setCurrentPlayerId(playerId)
    }

    private fun successFullCreatePlayer() {
        successFullCreatePlayer.postValue(true)
    }

    private fun stopProgressbar() {
        shouldProgressbarVisible.postValue(false)
    }

    private fun startProgressBar() {
        shouldProgressbarVisible.postValue(true)

    }

    private fun checkIsEmpty(playerName: String): Boolean {
        return if (playerName.isEmpty() or playerName.isBlank()) {
            textViewNameEmpty.postValue(true)
            true
        } else {
            textViewNameEmpty.postValue(false)
            false
        }
    }

    fun replaceUser() {
        createPlayer(playerName, playerGender)
    }
}


