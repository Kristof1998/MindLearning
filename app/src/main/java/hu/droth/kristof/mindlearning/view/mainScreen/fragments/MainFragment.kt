package hu.droth.kristof.mindlearning.view.mainScreen.fragments

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import hu.droth.kristof.mindlearning.R
import hu.droth.kristof.mindlearning.databinding.MainFragmentBinding
import hu.droth.kristof.mindlearning.model.BlurType
import hu.droth.kristof.mindlearning.model.IntelligenceType
import hu.droth.kristof.mindlearning.model.helperClasses.InProgressCourse
import hu.droth.kristof.mindlearning.view.mainScreen.adapters.InProgressCoursesRVAdapter
import hu.droth.kristof.mindlearning.view.mainScreen.viewModels.MainFragmentViewModel
import kotlinx.android.synthetic.main.main_fragment.*

@AndroidEntryPoint
class MainFragment : Fragment(), InProgressCoursesRVAdapter.InProgressCourseClickListener {

    private val viewModel: MainFragmentViewModel by viewModels()
    private lateinit var binding: MainFragmentBinding
    private lateinit var recyclerViewAdapter: InProgressCoursesRVAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.main_fragment,
            container,
            false
        )
        binding.lifecycleOwner = this
        //Connect binding with viewModel
        binding.mainViewModel = viewModel
        //Setting liveData observables
        setLiveDataObservables()


        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val navBar =
            activity?.findViewById<BottomNavigationView>(R.id.mainFragmentBottomNavigationBar)
        navBar?.visibility = View.VISIBLE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        btnEasterEgg.setOnClickListener { easterEggButtonClick() }
        btnStartCourse.setOnClickListener { startCourseButtonClick() }
    }

    private fun setLiveDataObservables() {
        //Set current progresses
        viewModel.inProgressCourses.observe(viewLifecycleOwner, {
            if (it.isEmpty()) {
                setEmptyRecycleView()
            } else {
                setNonEmptyRecycleView(it)
            }
        })

    }

    private fun setEmptyRecycleView() {
        inProgressRecycleView.visibility = View.GONE
        ivEmptyRecycleView.visibility = View.VISIBLE
        tvRecycleViewEmpty.visibility = View.VISIBLE
        btnStartCourse.visibility = View.VISIBLE
    }

    private fun setNonEmptyRecycleView(progressList: List<InProgressCourse>) {
        //set recycler view
        recyclerViewAdapter = InProgressCoursesRVAdapter(progressList, requireContext())
        recyclerViewAdapter.itemClickListener = this
        inProgressRecycleView.layoutManager = LinearLayoutManager(context)
        inProgressRecycleView.adapter = recyclerViewAdapter

        //set view visibility
        inProgressRecycleView.visibility = View.VISIBLE
        ivEmptyRecycleView.visibility = View.GONE
        tvRecycleViewEmpty.visibility = View.GONE
        btnStartCourse.visibility = View.GONE
    }

    private fun startCourseButtonClick() {
        findNavController().navigate(R.id.action_mainFragment_to_courseFragment)
    }

    private fun easterEggButtonClick() {
        btnStartCourse.visibility = View.INVISIBLE
        bgEasterEgg.visibility = View.VISIBLE
        tvEasterEgg.visibility = View.VISIBLE
        ivEasterEgg.visibility = View.VISIBLE
        Toast.makeText(context, "Patric will hide in 6 seconds", Toast.LENGTH_SHORT).show()

        object : CountDownTimer(6000, 1000) {
            override fun onTick(millisUntilFinish: Long) {}

            override fun onFinish() {
                bgEasterEgg.visibility = View.GONE
                tvEasterEgg.visibility = View.GONE
                ivEasterEgg.visibility = View.GONE
                btnStartCourse.visibility = View.VISIBLE
            }
        }.start()
    }

    override fun onItemClick(course: InProgressCourse) {
        val bundle = when (course.intelligenceType) {
            IntelligenceType.VISUAL -> {
                bundleOf(
                    "wordTheme" to course.wordTheme,
                    "intelligenceType" to course.intelligenceType,
                    "blurType" to BlurType.NONE
                )
            }
            IntelligenceType.VISUAL_HARD -> {
                bundleOf(
                    "intelligenceType" to course.intelligenceType,
                    "wordTheme" to course.wordTheme,
                    "blurType" to BlurType.BLUR
                )
            }
            else -> {
                bundleOf(
                    "wordTheme" to course.wordTheme,
                    "intelligenceType" to course.intelligenceType
                )
            }
        }
        val direction = when (course.intelligenceType) {
            IntelligenceType.VERBAL -> R.id.action_mainFragment_to_gameVerbalFragment
            IntelligenceType.LOGICAL -> R.id.action_mainFragment_to_gameLogicalFragment
            IntelligenceType.VISUAL -> R.id.action_mainFragment_to_gameVisualFragment
            IntelligenceType.MUSICAL -> R.id.action_mainFragment_to_gameMusicalFragment
            IntelligenceType.VISUAL_HARD -> R.id.action_mainFragment_to_gameVisualFragment
            else -> throw NotImplementedError("No intelligence type implemented :${course.intelligenceType}")
        }
        findNavController().navigate(direction, bundle)
    }

}