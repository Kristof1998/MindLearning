package hu.droth.kristof.mindlearning.view.game.learning.fragments

import dagger.hilt.android.AndroidEntryPoint
import hu.droth.kristof.mindlearning.R
import hu.droth.kristof.mindlearning.view.game.learning.viewModels.GameVerbalViewModel

@AndroidEntryPoint
class GameVerbalFragment :
    GameBaseLearningFragment<GameVerbalViewModel>(R.layout.game_verbal_fragment)