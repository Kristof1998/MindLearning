package hu.droth.kristof.mindlearning.view.game.learning.viewModels

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import hu.droth.kristof.mindlearning.R
import hu.droth.kristof.mindlearning.repository.FillRepository
import hu.droth.kristof.mindlearning.repository.KnownWordRepository
import hu.droth.kristof.mindlearning.repository.SessionRepository
import hu.droth.kristof.mindlearning.repository.WordRepository
import hu.droth.kristof.mindlearning.util.SharedPreferencesUtil

class GameLogicalViewModel
@ViewModelInject constructor(
    @Assisted savedStateHandle: SavedStateHandle,
    knownWordRepository: KnownWordRepository,
    wordRepository: WordRepository,
    sessionRepository: SessionRepository,
    fillRepository: FillRepository,
    sharedPreferencesUtil: SharedPreferencesUtil
) : GameBaseLearningViewModel(
    checkerPageId = R.id.action_gameLogicalFragment_to_gameCheckingLogical,
    savedStateHandle = savedStateHandle,
    knownWordRepository = knownWordRepository,
    wordRepository = wordRepository,
    sessionRepository = sessionRepository,
    fillRepository = fillRepository,
    sharedPreferencesUtil = sharedPreferencesUtil
)