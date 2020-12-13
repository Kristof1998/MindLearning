package hu.droth.kristof.mindlearning.view.game.learning.fragments

import dagger.hilt.android.AndroidEntryPoint
import hu.droth.kristof.mindlearning.R
import hu.droth.kristof.mindlearning.model.entity.Meaning
import hu.droth.kristof.mindlearning.view.game.learning.viewModels.GameLogicalViewModel
import kotlinx.android.synthetic.main.game_logical_fragment.*

@AndroidEntryPoint
class GameLogicalFragment :
    GameBaseLearningFragment<GameLogicalViewModel>(R.layout.game_logical_fragment) {
    override fun setObservables() {
        super.setObservables()
        viewModel.currentWordData.observe(viewLifecycleOwner, {
            it?.let { meaning: Meaning ->
                tvLetterNumber.text = meaning.writing.length.toString()
                tvConsonantNumber.text = meaning.consonantSize.toString()
                tvSyllableNumber.text = meaning.syllable.toString()
                tvVowelNumber.text = meaning.vowelSize.toString()
            }
        })
    }
}