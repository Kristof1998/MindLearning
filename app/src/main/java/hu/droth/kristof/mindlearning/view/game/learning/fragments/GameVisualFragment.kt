package hu.droth.kristof.mindlearning.view.game.learning.fragments

import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import hu.droth.kristof.mindlearning.R
import hu.droth.kristof.mindlearning.model.BlurType
import hu.droth.kristof.mindlearning.model.entity.Word
import hu.droth.kristof.mindlearning.view.game.learning.viewModels.GameVisualViewModel
import kotlinx.android.synthetic.main.game_visual_fragment.*

@AndroidEntryPoint
class GameVisualFragment :
    GameBaseLearningFragment<GameVisualViewModel>(R.layout.game_visual_fragment) {

    private val TAG = "GAME_VISUAL_FRAGMENT_TAG"
    private lateinit var progressBar: ProgressBar
    private lateinit var progressBarText: TextView

    override fun setObservables() {
        super.setObservables()

        viewModel.currentWordWithMeanings.observe(viewLifecycleOwner, { nullableWordWithMeanings ->
            nullableWordWithMeanings?.wordData?.let { setImage(it) }
        })
        viewModel.shouldShowProgressBar.observe(viewLifecycleOwner, {
            if (it) {
                progressBar.visibility = View.VISIBLE
                progressBarText.visibility = View.VISIBLE
                Log.d(TAG, "progressbar visible")
            } else {
                progressBar.visibility = View.INVISIBLE
                progressBarText.visibility = View.INVISIBLE
                Log.d(TAG, "progressbar invisible")
            }
        })
        viewModel.progressBarText.observe(viewLifecycleOwner, {
            progressBarText.text = it
        })
    }

    override fun setViewId(view: View) {
        super.setViewId(view)
        progressBar = view.findViewById(R.id.progressBarGameVisual)
        progressBarText = view.findViewById(R.id.progressBarTextGameVisual)
    }


    private fun setImage(word: Word) {
        val imageUrl = when (viewModel.blurType) {
            BlurType.NONE -> word.imageUrl
            BlurType.BLUR -> word.blurImageUrl
        }
        if (imageUrl != null) {
            Glide.with(requireContext())
                .load(imageUrl)
                .placeholder(R.drawable.ic_placeholder)
                .into(ivImageVisualFragment)
        } else {
            Glide.with(requireContext())
                .load(R.drawable.ic_placeholder)
                .into(ivImageVisualFragment)
        }
    }


}