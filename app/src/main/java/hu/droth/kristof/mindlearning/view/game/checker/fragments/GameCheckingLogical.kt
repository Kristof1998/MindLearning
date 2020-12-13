package hu.droth.kristof.mindlearning.view.game.checker.fragments

import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import hu.droth.kristof.mindlearning.R
import hu.droth.kristof.mindlearning.view.game.checker.viewModels.GameCheckingLogicalViewModel
import kotlinx.android.synthetic.main.game_checking_logical_fragment.*

@AndroidEntryPoint
class GameCheckingLogical : GameBaseCheckingFragment<GameCheckingLogicalViewModel>(R.layout.game_checking_logical_fragment) {

    private var helpVisible = false

    override fun setObservables() {
        super.setObservables()
        checkingViewModel.currentMeaningData.observe(viewLifecycleOwner, {
            setHelpVisibility(false)
            tvLetterNumber.text = it.writing.length.toString()
            tvConsonantNumber.text = it.consonantSize.toString()
            tvSyllableNumber.text = it.syllable.toString()
            tvVowelNumber.text = it.vowelSize.toString()
        })
    }

    override fun setButtonClickListener(view: View) {
        super.setButtonClickListener(view)
        btnHelp.setOnClickListener { setHelpVisibility(true) }
    }

    private fun setHelpVisibility(visible: Boolean) {
        if (visible && !helpVisible) {
            tvLetterNumber.visibility = View.VISIBLE
            tvConsonantNumber.visibility = View.VISIBLE
            tvSyllableNumber.visibility = View.VISIBLE
            tvVowelNumber.visibility = View.VISIBLE
            helpVisible = true
        } else if (!visible && helpVisible) {
            tvLetterNumber.visibility = View.INVISIBLE
            tvConsonantNumber.visibility = View.INVISIBLE
            tvSyllableNumber.visibility = View.INVISIBLE
            tvVowelNumber.visibility = View.INVISIBLE
            helpVisible = false
        }
    }

}