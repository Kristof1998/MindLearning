package hu.droth.kristof.mindlearning.view.game.learning.fragments

import android.content.res.Resources.NotFoundException
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import hu.droth.kristof.mindlearning.R
import hu.droth.kristof.mindlearning.view.game.learning.viewModels.GameBaseLearningViewModel
import kotlinx.android.synthetic.main.game_top_bar.*
import kotlinx.android.synthetic.main.game_verbal_fragment.*
import java.lang.reflect.ParameterizedType
import java.util.*

abstract class GameBaseLearningFragment<VM : GameBaseLearningViewModel>(
    @LayoutRes private val fragmentLayout: Int
) : Fragment() {

    val viewModel: VM by lazy {
        ViewModelProvider(this).get(getVMClass())
    }


    private val TAG = "GAME_BASE_FRAGMENT"

    private lateinit var btnNext: Button
    private lateinit var btnPrevious: Button
    private lateinit var tvOwnWord: TextView
    private lateinit var tvLearningWord: TextView
    private lateinit var ivOwnWordFlag: ImageView
    private lateinit var ownWordBackground: ConstraintLayout
    private lateinit var ivLearningWordFlag: ImageView
    private lateinit var btnBackArrow: ImageView
    private lateinit var tvWordNumber: TextView
    private lateinit var rootView: ConstraintLayout
    private lateinit var emptyScreenViewList: List<View>
    private lateinit var alwaysVisibleViewList: List<View>
    private lateinit var defaultViewVisibility: List<Pair<View, Int>>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(fragmentLayout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewId(view)
        saveDefaultViewVisibility()
        setAlwaysVisibleViewList()
        setAllViewVisibilityGone()
        setButtonClicks()
        setObservables()
    }

    override fun onStart() {
        super.onStart()
        val navBar =
            activity?.findViewById<BottomNavigationView>(R.id.mainFragmentBottomNavigationBar)
        navBar?.visibility = View.GONE
    }

    private fun saveDefaultViewVisibility() {
        val mutableResultList = mutableListOf<Pair<View, Int>>()
        for (i in 0 until rootView.childCount) {
            val view = rootView.getChildAt(i)
            mutableResultList.add(Pair(view, view.visibility))
        }
        defaultViewVisibility = mutableResultList
    }

    private fun setAlwaysVisibleViewList() {
        alwaysVisibleViewList = listOf<View>(gameTopBar, btnBackArrow, view5)
    }

    private fun setAllViewVisibilityGone() {
        for (i in 0 until rootView.childCount) {
            rootView.getChildAt(i).visibility = View.GONE
        }
    }

    open fun setButtonClicks() {
        btnNext.setOnClickListener { onNextButtonClicked() }
        btnPrevious.setOnClickListener { onPreviousButtonClicked() }
        btnBackArrow.setOnClickListener { findNavController().popBackStack() }
    }


    open fun setViewId(view: View) {
        tvOwnWord = ownWordAndSolutionData.findViewById(R.id.tvWordCardWithFlag)
        ivOwnWordFlag = ownWordAndSolutionData.findViewById(R.id.ivFlagCardWithFlag)
        tvLearningWord = learningWordData.findViewById(R.id.tvWordCardWithFlag)
        ivLearningWordFlag = learningWordData.findViewById(R.id.ivFlagCardWithFlag)
        btnBackArrow = gameTopBar.findViewById(R.id.btnBack)
        tvWordNumber = gameTopBar.findViewById(R.id.tvWordNumber)
        btnNext = view.findViewById(R.id.btnNextCardWord)
        btnPrevious = view.findViewById(R.id.btnPreviousCardWord)
        rootView = view.findViewById(R.id.clRootView)
    }


    open fun setObservables() {
        viewModel.ownWordText.observe(viewLifecycleOwner, {
            tvOwnWord.text = it?.capitalize(Locale.ROOT) ?: ""
        })
        viewModel.learningWordText.observe(viewLifecycleOwner, {
            tvLearningWord.text = it?.capitalize(Locale.ROOT) ?: ""
        })
        viewModel.ownWordFlagDrawable.observe(viewLifecycleOwner, { nullableFlag ->
            nullableFlag?.let {
                ivOwnWordFlag.setImageDrawable(getFlagDrawable(it))
            }
        })
        viewModel.learningWordFlagDrawable.observe(viewLifecycleOwner, { nullableFlag ->
            nullableFlag?.let {
                ivLearningWordFlag.setImageDrawable(getFlagDrawable(it))
            }
        })
        viewModel.shouldShowEmptyScreen.observe(viewLifecycleOwner, { shouldShowNullable ->
            shouldShowNullable?.let {
                if (it) {
                    showEmptyScreen()
                } else {
                    showNonEmptyScreen()
                }
            }
        })
        viewModel.previousButtonEnable.observe(viewLifecycleOwner, {
            btnPrevious.isEnabled = it
        })
        viewModel.nextButtonText.observe(viewLifecycleOwner, {
            btnNext.text = it
        })
        viewModel.wordNumberText.observe(viewLifecycleOwner, {
            tvWordNumber.text = it
        })
        viewModel.shouldGoCheckerPage.observe(viewLifecycleOwner, {
            findNavController().navigate(it.first, it.second)
        })

    }


    private fun showNonEmptyScreen() {
        //all element visibility to visible
        defaultViewVisibility.forEach {
            val view = it.first
            val visibility = it.second
            view.visibility = visibility
        }
        //if emptyScreen initialized, set the items visibility to gone
        if (::emptyScreenViewList.isInitialized) {
            emptyScreenViewList.forEach {
                it.visibility = View.GONE
            }
        }
    }

    private fun showEmptyScreen() {
        //all viewChild visibility gone
        for (i in 0 until rootView.childCount) {
            rootView.getChildAt(i).visibility = View.GONE
        }
        //add empty elements if not exist
        if (!::emptyScreenViewList.isInitialized) {
            addEmptyScreenElements()
        }
        emptyScreenViewList.forEach {
            it.visibility = View.VISIBLE
        }
        alwaysVisibleViewList.forEach {
            it.visibility = View.VISIBLE
        }
    }

    private fun addEmptyScreenElements() {
        //set empty image and button for view
        val emptyImage = ImageView(requireContext()).apply {
            val drawable =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_icon_empty_recycle_view)
                    ?: throw  NotFoundException("Empty drawable not found")
            setImageDrawable(drawable)
            id = View.generateViewId()
        }
        val emptyText = TextView(requireContext()).apply {
            text = context.getString(R.string.you_know_everything_from_this_lecture)
            maxLines = 2
            setTextColor(ContextCompat.getColor(requireContext(), R.color.whiteColor))
            textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 30F)
            id = View.generateViewId()
        }
        val emptyButton = Button(requireContext()).apply {
            text = context.getString(R.string.reset_lecture)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 24F)
            val backgroundDrawable =
                ContextCompat.getDrawable(requireContext(), R.drawable.rounded_button_next_selector)
                    ?: throw  NotFoundException("Background drawable not found")
            background = backgroundDrawable
            setTextColor(ContextCompat.getColor(requireContext(), R.color.darkTextColor))
            setOnClickListener { resetLecture() }

            transformationMethod = null
            id = View.generateViewId()
        }
        rootView.addView(
            emptyImage,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        rootView.addView(
            emptyText,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        rootView.addView(
            emptyButton,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        ConstraintSet().apply {
            clone(rootView)
            //set emptyImage constraints
            connect(emptyImage.id, ConstraintSet.TOP, gameTopBar.id, ConstraintSet.BOTTOM, 20)
            connect(emptyImage.id, ConstraintSet.LEFT, rootView.id, ConstraintSet.LEFT)
            connect(emptyImage.id, ConstraintSet.RIGHT, rootView.id, ConstraintSet.RIGHT)
            //set emptyText constraints
            connect(emptyText.id, ConstraintSet.TOP, emptyImage.id, ConstraintSet.BOTTOM, 20)
            connect(emptyText.id, ConstraintSet.LEFT, rootView.id, ConstraintSet.LEFT)
            connect(emptyText.id, ConstraintSet.RIGHT, rootView.id, ConstraintSet.RIGHT)
            //set emptyButton constraints
            connect(emptyButton.id, ConstraintSet.TOP, emptyText.id, ConstraintSet.BOTTOM, 40)
            connect(emptyButton.id, ConstraintSet.LEFT, rootView.id, ConstraintSet.LEFT)
            connect(emptyButton.id, ConstraintSet.RIGHT, rootView.id, ConstraintSet.RIGHT)
            applyTo(rootView)
        }
        emptyScreenViewList = listOf(emptyImage, emptyButton, emptyText)
    }

    private fun resetLecture() {
        viewModel.resetLecture()
    }

    private fun getFlagDrawable(@DrawableRes flagId: Int): Drawable {
        return ContextCompat.getDrawable(requireContext(), flagId)
            ?: throw NotFoundException("Flag drawable not found with id:$flagId")
    }

    private fun onNextButtonClicked() {
        viewModel.getNextWord()
    }

    private fun onPreviousButtonClicked() {
        viewModel.getPreviousWord()
    }

    @Suppress("UNCHECKED_CAST")
    private fun getVMClass(): Class<VM> {
        return (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<VM>
    }
}
