package hu.droth.kristof.mindlearning.view.game.checker.fragments

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.addListener
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import hu.droth.kristof.mindlearning.R
import hu.droth.kristof.mindlearning.model.LanguageType
import hu.droth.kristof.mindlearning.util.EventObserver
import hu.droth.kristof.mindlearning.util.closeKeyboard
import hu.droth.kristof.mindlearning.util.getFlagDrawable
import hu.droth.kristof.mindlearning.view.game.checker.viewModels.GameBaseCheckingViewModel
import kotlinx.android.synthetic.main.game_checking_verbal_fragment.*
import java.lang.reflect.ParameterizedType
import java.util.*

abstract class GameBaseCheckingFragment<CWM : GameBaseCheckingViewModel>(
    @LayoutRes private val fragmentLayout: Int
) : Fragment() {


    private lateinit var tvOwnWord: TextView
    private lateinit var tvLearningWord: TextView
    private lateinit var ownWordFlag: ImageView
    private lateinit var ownWordBackground: ConstraintLayout
    private lateinit var solutionWord: TextView
    private lateinit var solutionFlag: ImageView
    private lateinit var solutionBackground: ConstraintLayout
    private lateinit var btnBackArrow: ImageView
    private lateinit var tvWordNumber: TextView
    private lateinit var rootView: ConstraintLayout



    private lateinit var checkerAnimation: AnimatorSet

    private var isAnimationInProgress = false

    val checkingViewModel: CWM by lazy {
        ViewModelProvider(this).get(getVMClass())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(fragmentLayout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewId()
        setButtonClickListener(view)
        setObservables()
    }
    override fun onStart() {
        super.onStart()
        val navBar = activity?.findViewById<BottomNavigationView>(R.id.mainFragmentBottomNavigationBar)
        navBar?.visibility = View.GONE
    }

    open fun setButtonClickListener(view: View) {
        btnBackArrow.setOnClickListener {
            closeKeyboard(requireActivity())
            findNavController().popBackStack()
        }
        ivCheckAnswer.setOnClickListener { checkAnswer() }
        etAnswerGame.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                checkAnswer()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false

        }
    }

    open fun setObservables() {
        checkingViewModel.ownWordText.observe(viewLifecycleOwner, {
            tvOwnWord.text = it.capitalize(Locale.ROOT)
        })
        checkingViewModel.solutionWordText.observe(viewLifecycleOwner, {
            solutionWord.text = it.capitalize(Locale.ROOT)
        })
        checkingViewModel.ownWordFlagDrawable.observe(viewLifecycleOwner, { nullableFlag ->
            nullableFlag?.let {
                ownWordFlag.setImageDrawable(getFlagDrawable(it))
            }
        })
        checkingViewModel.solutionWordFlagDrawable.observe(viewLifecycleOwner, { nullableFlag ->
            nullableFlag?.let {
                solutionFlag.setImageDrawable(getFlagDrawable(it))
            }
        })
        checkingViewModel.shouldShowAnswer.observe(viewLifecycleOwner, {
            if (it != null) {
                showAnswer(it)
                checkingViewModel.shouldShowAnswer.value = null
            }
        })
        checkingViewModel.wordNumberText.observe(viewLifecycleOwner, {
            tvWordNumber.text = it
        })
        checkingViewModel.shouldGameEnd.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                shouldGameEnd()
            }
        })
    }

    private fun shouldGameEnd() {
        closeKeyboard(requireActivity())
        findNavController().popBackStack()
    }

    open fun setViewId() {
        tvOwnWord = ownWordAndSolutionData.findViewById(R.id.tvWordCardWithFlagOwnWord)
        ownWordFlag = ownWordAndSolutionData.findViewById(R.id.ivFlagCardWithFlagOwnWord)
        ownWordBackground = ownWordAndSolutionData.findViewById(R.id.clOwnWordLayout)
        solutionWord = ownWordAndSolutionData.findViewById(R.id.tvWordCardWithFlagSolution)
        solutionFlag = ownWordAndSolutionData.findViewById(R.id.ivFlagCardWithFlagSolution)
        solutionBackground = ownWordAndSolutionData.findViewById(R.id.clSolutionLayout)
        btnBackArrow = gameTopBar.findViewById(R.id.btnBack)
        tvWordNumber = gameTopBar.findViewById(R.id.tvWordNumber)
    }

    private fun checkAnswer() {
        if (!isAnimationInProgress) {
            checkingViewModel.checkAnswer(etAnswerGame.text.toString().trim())
        }
    }

    private fun showAnswer(solutionData: Triple<String, LanguageType, Boolean>) {
        isAnimationInProgress = true
        if (!::checkerAnimation.isInitialized) {
            initializeCheckerAnimation()
        }
        solutionWord.text = solutionData.first.capitalize(Locale.ROOT)
        solutionFlag.setImageDrawable(ContextCompat.getDrawable(requireContext(), solutionData.second.getFlagDrawable()))
        val color = if (solutionData.third) {
            R.color.successColor
        } else {
            R.color.errorColor
        }
        solutionBackground.backgroundTintList = requireContext().getColorStateList(color)
        val scale = requireContext().resources.displayMetrics.density
        solutionBackground.cameraDistance = 8000 * scale
        ownWordBackground.cameraDistance = 8000 * scale
        //startAnim
        checkerAnimation.start()

    }

    private fun initializeCheckerAnimation() {
        val frontAnim = AnimatorInflater.loadAnimator(requireContext(), R.animator.front_animator) as AnimatorSet
        val frontAnimReverse = AnimatorInflater.loadAnimator(requireContext(), R.animator.front_animator) as AnimatorSet
        val backAnim = AnimatorInflater.loadAnimator(requireContext(), R.animator.back_animator) as AnimatorSet
        val backAnimReverse = AnimatorInflater.loadAnimator(requireContext(), R.animator.back_animator) as AnimatorSet
        frontAnim.setTarget(ownWordBackground)
        backAnim.setTarget(solutionBackground)
        frontAnimReverse.setTarget(solutionBackground)
        backAnimReverse.setTarget(ownWordBackground)
        val resultReverseAnim = AnimatorSet()
        resultReverseAnim.play(backAnimReverse).with(frontAnimReverse).after(1000)
        val resultAnim = AnimatorSet()
        resultAnim.play(frontAnim).with(backAnim)
        resultAnim.addListener(onEnd = {
            resultReverseAnim.start()
        })
        resultReverseAnim.addListener(onEnd = {
            //reset editText field
            etAnswerGame.setText("")
            isAnimationInProgress = false
            checkingViewModel.getNextCheckableWord()
        })
        checkerAnimation = resultAnim
    }

    private fun getFlagDrawable(@DrawableRes flagId: Int): Drawable {
        return ContextCompat.getDrawable(requireContext(), flagId) ?: throw Resources.NotFoundException("Flag drawable not found with id:$flagId")
    }


    @Suppress("UNCHECKED_CAST")
    private fun getVMClass(): Class<CWM> {
        return (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<CWM>
    }

}