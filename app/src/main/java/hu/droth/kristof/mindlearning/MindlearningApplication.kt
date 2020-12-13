package hu.droth.kristof.mindlearning

import android.app.Application
import android.widget.Toast
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class MindlearningApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Thread.setDefaultUncaughtExceptionHandler { _: Thread, throwable: Throwable ->
            FirebaseCrashlytics.getInstance().recordException(throwable)
            Toast.makeText(
                applicationContext,
                throwable.message ?: "Unknown error, try to restrart app",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}