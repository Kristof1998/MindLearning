@file:Suppress("unused")

package hu.droth.kristof.mindlearning.view.mainScreen.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import hu.droth.kristof.mindlearning.R
import hu.droth.kristof.mindlearning.view.mainScreen.adapters.CoursesRVAdapter
import kotlinx.android.synthetic.main.course_fragment.*

class CourseFragment : Fragment() {

    private lateinit var recycleViewAdapter: CoursesRVAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.course_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRecycleView()
    }

    override fun onStart() {
        super.onStart()
        val navBar =
            activity?.findViewById<BottomNavigationView>(R.id.mainFragmentBottomNavigationBar)
        navBar?.visibility = View.VISIBLE
    }

    private fun setRecycleView() {
        recycleViewAdapter = CoursesRVAdapter(this.requireActivity())
        courseFragmentRecycleView.layoutManager = LinearLayoutManager(context)
        courseFragmentRecycleView.adapter = recycleViewAdapter
    }

}