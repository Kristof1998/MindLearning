package hu.droth.kristof.mindlearning.view.mainScreen.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import hu.droth.kristof.mindlearning.R
import hu.droth.kristof.mindlearning.model.WordTheme
import hu.droth.kristof.mindlearning.util.getStringResource
import hu.droth.kristof.mindlearning.view.mainScreen.fragments.CourseFragmentDirections

class CoursesRVAdapter(
    private val context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val coursesTextList: List<String> = getCoursesName()
    private val courseType: List<WordTheme> = getCourseType()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val rootView =
            LayoutInflater.from(context).inflate(R.layout.card_course_fragment, parent, false)
        return CourseRVViewHolder(rootView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val tempCourseName = coursesTextList[position]
        val viewHolder = holder as CourseRVViewHolder
        holder.itemView.setOnClickListener {
            onItemClick(it, position)
        }
        viewHolder.courseText.text = tempCourseName
    }

    private fun onItemClick(view: View, position: Int) {
        val value = courseType[position]
        val action =
            CourseFragmentDirections.actionCourseFragmentToIntelligenceTypeChooserFragment(value)
        view.findNavController().navigate(action)

    }

    override fun getItemCount(): Int = coursesTextList.size

    private fun getCourseType(): List<WordTheme> {
        val courseTypeList = mutableListOf<WordTheme>()
        enumValues<WordTheme>().forEach {
            courseTypeList.add(it)
        }
        return courseTypeList
    }

    private fun getCoursesName(): List<String> {
        val courseNameList = mutableListOf<String>()
        enumValues<WordTheme>().forEach {
            courseNameList.add(context.getString(it.getStringResource()))
        }
        return courseNameList
    }

    class CourseRVViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val courseText: TextView = itemView.findViewById(R.id.courseFragmentRVText)
    }

}
