package hu.droth.kristof.mindlearning.view.mainScreen.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import hu.droth.kristof.mindlearning.R
import hu.droth.kristof.mindlearning.model.BlurType
import hu.droth.kristof.mindlearning.model.IntelligenceType
import hu.droth.kristof.mindlearning.model.WordTheme
import hu.droth.kristof.mindlearning.util.getIconDrawable
import hu.droth.kristof.mindlearning.util.getStringResource

class IntelligenceTypeGVAdapter(
    private val context: Context,
    private val navController: NavController,
    private val wordTheme: WordTheme,
    private val enum: List<IntelligenceType>,
    private val recommendedLearningIntelligenceType: IntelligenceType
) :
    BaseAdapter() {

    private val enumNames = enum.map {
        context.getString(it.getStringResource())
    }
    private val enumIcons = enum.map {
        ContextCompat.getDrawable(context, it.getIconDrawable())
    }
    private val enumActions = enum.map {
        when (it) {
            IntelligenceType.VERBAL -> R.id.action_intelligenceTypeChooserFragment_to_gameVerbalFragment
            IntelligenceType.LOGICAL -> R.id.action_intelligenceTypeChooserFragment_to_gameLogicalFragment
            IntelligenceType.VISUAL -> R.id.action_intelligenceTypeChooserFragment_to_gameVisualFragment
            IntelligenceType.MUSICAL -> R.id.action_intelligenceTypeChooserFragment_to_gameMusicalFragment
            IntelligenceType.VISUAL_HARD -> R.id.action_intelligenceTypeChooserFragment_to_gameVisualFragment
            IntelligenceType.TEST -> R.id.action_intelligenceTypeChooserFragment_to_gameTestFragment
            IntelligenceType.RECOMMENDED_LEARNING -> getRecommendedAction()
        }
    }

    private fun getRecommendedAction(): Int {
        return when (recommendedLearningIntelligenceType) {
            IntelligenceType.VERBAL -> R.id.action_intelligenceTypeChooserFragment_to_gameVerbalFragment
            IntelligenceType.LOGICAL -> R.id.action_intelligenceTypeChooserFragment_to_gameLogicalFragment
            IntelligenceType.VISUAL -> R.id.action_intelligenceTypeChooserFragment_to_gameVisualFragment
            IntelligenceType.MUSICAL -> R.id.action_intelligenceTypeChooserFragment_to_gameMusicalFragment
            IntelligenceType.VISUAL_HARD -> R.id.action_intelligenceTypeChooserFragment_to_gameVisualFragment
            else -> throw IllegalStateException("Recommended intelligence type is illegal")
        }
    }

    private val inflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int = enumNames.size

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val viewHolder: IntelligenceTypeViewHolder?
        var newConvertView: View? = null

        if (convertView == null) {
            viewHolder = IntelligenceTypeViewHolder()
            newConvertView = inflater.inflate(R.layout.card_intelligence_type_grid, null)
            viewHolder.image =
                (newConvertView as ConstraintLayout).findViewById(R.id.ivCardIntelligenceIcon)
            viewHolder.title =
                newConvertView.findViewById(R.id.tvCardIntelligenceTypeName)
            newConvertView.setTag(viewHolder)
            newConvertView.setOnClickListener {
                setOnClickListeners(position, wordTheme)
            }
        } else {
            viewHolder = convertView.tag as IntelligenceTypeViewHolder
        }
        viewHolder.title.text = enumNames[position]
        viewHolder.image.setImageDrawable(enumIcons[position])
        return newConvertView ?: convertView!!

    }

    private fun setOnClickListeners(position: Int, wordTheme: WordTheme) {
        val action = enumActions[position]
        val bundle = when (enum[position]) {
            IntelligenceType.VISUAL -> {
                bundleOf(
                    "intelligenceType" to enum[position],
                    "wordTheme" to wordTheme,
                    "blurType" to BlurType.NONE
                )
            }
            IntelligenceType.VISUAL_HARD -> {
                bundleOf(
                    "intelligenceType" to enum[position],
                    "wordTheme" to wordTheme,
                    "blurType" to BlurType.BLUR
                )
            }
            IntelligenceType.RECOMMENDED_LEARNING -> {
                if (recommendedLearningIntelligenceType == IntelligenceType.VISUAL) {
                    bundleOf(
                        "intelligenceType" to enum[position],
                        "wordTheme" to wordTheme,
                        "blurType" to BlurType.NONE
                    )
                } else if (recommendedLearningIntelligenceType == IntelligenceType.VISUAL_HARD) {
                    bundleOf(
                        "intelligenceType" to enum[position],
                        "wordTheme" to wordTheme,
                        "blurType" to BlurType.BLUR
                    )
                } else {
                    bundleOf(
                        "wordTheme" to wordTheme,
                        "intelligenceType" to enum[position]
                    )
                }
            }
            else -> {
                bundleOf(
                    "wordTheme" to wordTheme,
                    "intelligenceType" to enum[position]
                )
            }
        }
        Log.d("GVAdapter", "Navigate to $wordTheme")
        navController.navigate(action, bundle)
    }


    private class IntelligenceTypeViewHolder {
        lateinit var image: ImageView
        lateinit var title: TextView
    }
}