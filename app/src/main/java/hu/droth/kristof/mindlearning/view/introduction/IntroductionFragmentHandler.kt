package hu.droth.kristof.mindlearning.view.introduction

import android.view.View

interface IntroductionFragmentHandler : View.OnClickListener {
    fun setViews()
    fun setProgressbar()
}
