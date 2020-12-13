package hu.droth.kristof.mindlearning.view.splashScreen

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import dagger.hilt.android.AndroidEntryPoint
import hu.droth.kristof.mindlearning.R
import hu.droth.kristof.mindlearning.databinding.ActivitySplashScreenBinding
import hu.droth.kristof.mindlearning.view.authentication.AuthenticationActivity
import hu.droth.kristof.mindlearning.view.introduction.IntroductionActivity
import hu.droth.kristof.mindlearning.view.mainScreen.MainActivity
import kotlinx.android.synthetic.main.activity_splash_screen.*


@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {

    private val viewModel: SplashScreenViewModel by viewModels()
    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var topAnimation: Animation
    private lateinit var bottomAnimation: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        setDataBinding()
        loadAnimations()

    }

    private fun setDataBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash_screen)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

    }

    override fun onStart() {
        super.onStart()
        startAnimations()
        viewModel.initializeData()

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (viewModel.shouldShowTapToContinue.value == true) {
                    startNextActivity()
                }
                true
            }
            else -> super.onTouchEvent(event)
        }
    }

    private fun loadAnimations() {
        topAnimation = AnimationUtils.loadAnimation(applicationContext, R.anim.splash_top_animation)
        bottomAnimation =
            AnimationUtils.loadAnimation(applicationContext, R.anim.splash_bottom_animation)
    }

    private fun startAnimations() {
        ivApplicationIcon.animation = topAnimation
        tvApplicationName.animation = bottomAnimation
    }

    private fun startNextActivity() {
        val nextActivityIntent: Intent =
            if (viewModel.isFirstRun) {
                viewModel.setFirstRunToFalse()
                Intent(applicationContext, IntroductionActivity::class.java)
            } else if (!viewModel.hasCurrentPlayer) {
                Intent(applicationContext, AuthenticationActivity::class.java)
            } else {
                Intent(applicationContext, MainActivity::class.java)
            }
        nextActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(nextActivityIntent)
        finish()
    }


}