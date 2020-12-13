package hu.droth.kristof.mindlearning.view.game.checker.viewModels

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import hu.droth.kristof.mindlearning.repository.FillRepository
import hu.droth.kristof.mindlearning.repository.SessionRepository
import hu.droth.kristof.mindlearning.repository.WordRepository

class GameCheckingMusicalViewModel @ViewModelInject constructor(
    @Assisted savedStateHandle: SavedStateHandle,
    fillRepository: FillRepository,
    sessionRepository: SessionRepository,
    wordRepository: WordRepository
) : GameBaseCheckingViewModel(
    savedStateHandle = savedStateHandle,
    fillRepository = fillRepository,
    sessionRepository = sessionRepository,
    wordRepository = wordRepository
)