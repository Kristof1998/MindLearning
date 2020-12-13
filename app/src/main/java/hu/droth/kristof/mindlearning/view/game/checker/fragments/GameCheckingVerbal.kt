package hu.droth.kristof.mindlearning.view.game.checker.fragments

import android.widget.TextView
import dagger.hilt.android.AndroidEntryPoint
import hu.droth.kristof.mindlearning.R
import hu.droth.kristof.mindlearning.view.game.checker.viewModels.GameCheckingVerbalViewModel
import kotlinx.android.synthetic.main.game_checking_verbal_fragment.*

@AndroidEntryPoint
class GameCheckingVerbal :
    GameBaseCheckingFragment<GameCheckingVerbalViewModel>(R.layout.game_checking_verbal_fragment) {

    private lateinit var missingTvData: TextView

    override fun setViewId() {
        super.setViewId()
        missingTvData = missingLetterData.findViewById(R.id.tvWordCardWithFlag)
    }

    override fun setObservables() {
        super.setObservables()
        checkingViewModel.missingWordData.observe(viewLifecycleOwner, {
            missingTvData.text = it
        })
    }
}