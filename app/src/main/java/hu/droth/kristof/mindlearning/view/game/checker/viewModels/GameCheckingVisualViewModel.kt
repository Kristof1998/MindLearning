package hu.droth.kristof.mindlearning.view.game.checker.viewModels

import android.content.res.Resources
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import hu.droth.kristof.mindlearning.model.BlurType
import hu.droth.kristof.mindlearning.model.IntelligenceType
import hu.droth.kristof.mindlearning.repository.FillRepository
import hu.droth.kristof.mindlearning.repository.SessionRepository
import hu.droth.kristof.mindlearning.repository.WordRepository

class GameCheckingVisualViewModel @ViewModelInject constructor(
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
    val blurType = initBlurType()

    private fun initBlurType(): BlurType {
        val intelligenceType = savedStateHandle.get<IntelligenceType>("intelligenceType")
            ?: throw Resources.NotFoundException("Intelligence type not found in args ")
        return when (intelligenceType) {
            IntelligenceType.VISUAL -> BlurType.NONE
            IntelligenceType.VISUAL_HARD -> BlurType.BLUR
            else -> throw IllegalStateException("Cannot create blur type to intelligence type")
        }
    }

}