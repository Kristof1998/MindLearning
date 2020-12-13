package hu.droth.kristof.mindlearning.util

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.github.mikephil.charting.utils.ColorTemplate

fun closeKeyboard(activity: Activity) {
    val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    val view = activity.currentFocus ?: View(activity)
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

val CHART_COLOR = arrayListOf(
    ColorTemplate.rgb("#89bf77"),
    ColorTemplate.rgb("#3d9a70"),
    ColorTemplate.rgb("#fbb862"),
    ColorTemplate.rgb("#d43d51"),
    ColorTemplate.rgb("#afd17c"),
    ColorTemplate.rgb("#00876c"),
    ColorTemplate.rgb("#ee7d4f"),
    ColorTemplate.rgb("#e35e4e"),
    ColorTemplate.rgb("#f59b56"),
    ColorTemplate.rgb("#fdd576"),
    ColorTemplate.rgb("#fff18f"),
    ColorTemplate.rgb("#64ad73"),
    ColorTemplate.rgb("#d6e184"),
)