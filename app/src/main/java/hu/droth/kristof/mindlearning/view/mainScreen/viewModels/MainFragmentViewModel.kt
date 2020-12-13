package hu.droth.kristof.mindlearning.view.mainScreen.viewModels

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import hu.droth.kristof.mindlearning.model.entity.Player
import hu.droth.kristof.mindlearning.model.helperClasses.InProgressCourse
import hu.droth.kristof.mindlearning.repository.PlayerRepository
import hu.droth.kristof.mindlearning.repository.SessionRepository
import hu.droth.kristof.mindlearning.util.SharedPreferencesUtil

class MainFragmentViewModel @ViewModelInject constructor(
    private val playerRepository: PlayerRepository,
    private val sessionRepository: SessionRepository,
    private val sharedPreferencesUtil: SharedPreferencesUtil
) :
    ViewModel() {
    private val TAG = "MAIN_FRAGMENT_VIEW_MODEL"
    private var playerId = initPlayerId()

    val shouldShowProgressBarWithText = MutableLiveData(false)
    val progressBarText = MutableLiveData("")
    val currentPlayer: LiveData<Player> = setCurrentPlayer()
    val inProgressCourses: LiveData<List<InProgressCourse>> = setInProgressCourses()


    private fun initPlayerId(): Long {
        return sharedPreferencesUtil.getCurrentPlayerId()
    }

    private fun setCurrentPlayer(): LiveData<Player> {
        Log.d(TAG, "CurrentPlayerId: $playerId")
        return liveData {
            emit(playerRepository.getPlayerById(playerId))
        }
    }

    private fun setInProgressCourses(): LiveData<List<InProgressCourse>> {
        return liveData {
            val result = sessionRepository.getInProgressSessions(playerId)
            Log.d(TAG, "In progress sessions size: ${result.size}")
            emit(result)
        }
    }

}