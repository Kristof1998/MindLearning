package hu.droth.kristof.mindlearning.view.mainScreen.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hu.droth.kristof.mindlearning.R
import hu.droth.kristof.mindlearning.model.helperClasses.InProgressCourse
import hu.droth.kristof.mindlearning.util.getStringResource

class InProgressCoursesRVAdapter(
    private val inProgressCourseList: List<InProgressCourse>,
    private val context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var itemClickListener: InProgressCourseClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val rootView = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_current_progress, parent, false)
        return InProgressCoursesRVViewHolder(rootView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val tempCourse = inProgressCourseList[position]
        val viewHolder = holder as InProgressCoursesRVViewHolder
        viewHolder.course = tempCourse
        viewHolder.theme.text = context.getString(tempCourse.wordTheme.getStringResource())
        viewHolder.type.text = context.getString(tempCourse.intelligenceType.getStringResource())
        viewHolder.progressBar.setProgress(tempCourse.progress, false)
    }

    override fun getItemCount(): Int = inProgressCourseList.size

    inner class InProgressCoursesRVViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressbar)
        val theme: TextView = itemView.findViewById(R.id.tvTheme)
        val type: TextView = itemView.findViewById(R.id.tvType)
        var course: InProgressCourse? = null

        init {
            itemView.setOnClickListener {
                course?.let { inProgressCourse -> itemClickListener?.onItemClick(inProgressCourse) }
            }
        }

    }

    interface InProgressCourseClickListener {
        fun onItemClick(course: InProgressCourse)
    }

}