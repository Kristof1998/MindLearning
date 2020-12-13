package hu.droth.kristof.mindlearning.view.game.learning.fragments

import android.content.res.Resources.NotFoundException
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import hu.droth.kristof.mindlearning.R
import hu.droth.kristof.mindlearning.view.game.learning.viewModels.GameTestViewModel
import kotlinx.android.synthetic.main.game_test_fragment.*
import java.util.*

@AndroidEntryPoint
class GameTestFragment : Fragment() {

    private val viewModel: GameTestViewModel by viewModels()
    private lateinit var learningWordFlagImageView: ImageView
    private lateinit var learningWordTextView: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.game_test_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewId()
        setOnClickListeners()
        setLiveDataObservables()
    }

    private fun setOnClickListeners() {
        llTestSuccess.setOnClickListener { btnIKnowClicked() }
        llTestFailure.setOnClickListener { btnLearnItClicked() }
        imageView.setOnClickListener { findNavController().popBackStack() }
    }

    private fun setViewId() {
        learningWordFlagImageView = learningWordData.findViewById(R.id.ivFlagCardWithFlag)
        learningWordTextView = learningWordData.findViewById(R.id.tvWordCardWithFlag)
    }

    private fun setLiveDataObservables() {
        viewModel.learningWordText.observe(viewLifecycleOwner, {
            learningWordTextView.text = it.capitalize(Locale.ROOT)
        })
        viewModel.flagDrawableId.observe(viewLifecycleOwner, {
            setFlagDrawable(it)
        })
        viewModel.inProgressText.observe(viewLifecycleOwner, {
            tvWordNumber.text = it
        })
        viewModel.shouldNavigateMainScreen.observe(viewLifecycleOwner, {
            if (it) {
                navigateBack()
            }
        })
        viewModel.shouldShowEmpty.observe(viewLifecycleOwner, {
            if (it) {
                showEmpty()
            }
        })
    }

    private fun showEmpty() {
        llTestSuccess.visibility = View.GONE
        llTestFailure.visibility = View.GONE
        learningWordData.visibility = View.GONE
        view5.visibility = View.GONE
        tvWordNumber.visibility = View.GONE
        ivEmpty.visibility = View.VISIBLE
        tvEmpty.visibility = View.VISIBLE
    }

    private fun navigateBack() {
        findNavController().popBackStack()
    }

    private fun setFlagDrawable(flagDrawableId: Int?) {
        flagDrawableId?.let {
            val drawable = ContextCompat.getDrawable(requireContext(), it)
                ?: throw NotFoundException("Flag drawable not found: $it")
            learningWordFlagImageView.setImageDrawable(drawable)
        }
    }

    private fun btnIKnowClicked() {
        viewModel.btnIKnowButtonClicked()
    }

    private fun btnLearnItClicked() {
        viewModel.btnLearnItButtonClicked()
    }

}
