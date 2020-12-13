package hu.droth.kristof.mindlearning.view.game.checker.fragments

import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import dagger.hilt.android.AndroidEntryPoint
import hu.droth.kristof.mindlearning.R
import hu.droth.kristof.mindlearning.model.BlurType
import hu.droth.kristof.mindlearning.model.entity.Word
import hu.droth.kristof.mindlearning.view.game.checker.viewModels.GameCheckingVisualViewModel
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.game_checking_visual_fragment.*
import kotlinx.android.synthetic.main.game_visual_fragment.ivImageVisualFragment

@AndroidEntryPoint
class GameCheckingVisual :
    GameBaseCheckingFragment<GameCheckingVisualViewModel>(R.layout.game_checking_visual_fragment) {

    private var imageVisible = false

    override fun setObservables() {
        super.setObservables()
        checkingViewModel.currentWordWithMeanings.observe(viewLifecycleOwner, {
            setImageVisibilityToFalse()
            it?.let {
                setHelperImages(it.wordData)
            }
        })
    }

    private fun setImageVisibilityToFalse() {
        if (imageVisible) {
            ivImageVisualFragment.visibility = View.GONE
            imageVisible = false
        }
    }

    override fun setButtonClickListener(view: View) {
        super.setButtonClickListener(view)
        btnHelp.setOnClickListener {
            changeImageVisibility()
        }
    }

    private fun changeImageVisibility() {
        if (!imageVisible) {
            ivImageVisualFragment.visibility = View.VISIBLE
            imageVisible = true
        }
    }

    private fun setHelperImages(word: Word) {
        val imageUrl = when (checkingViewModel.blurType) {
            BlurType.NONE -> word.imageUrl
            BlurType.BLUR -> word.blurImageUrl
        }
        if (imageUrl != null) {
            when (checkingViewModel.blurType) {
                BlurType.NONE -> {
                    Glide.with(requireContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_placeholder)
                        .into(ivImageVisualFragment)
                }
                BlurType.BLUR -> {
                    Glide.with(requireContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_placeholder)
                        .apply(RequestOptions.bitmapTransform(BlurTransformation(10, 3)))
                        .into(ivImageVisualFragment)
                }
            }
        } else {
            Glide.with(requireContext())
                .load(R.drawable.ic_placeholder)
                .into(ivImageVisualFragment)
        }
    }
}

