package hu.droth.kristof.mindlearning.view

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.crashlytics.FirebaseCrashlytics

abstract class BaseFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.setDefaultUncaughtExceptionHandler { _: Thread, throwable: Throwable ->
            FirebaseCrashlytics.getInstance().recordException(throwable)
            Toast.makeText(requireContext(), throwable.message ?: "Unknown error, try to restrart app", Toast.LENGTH_LONG).show()
        }
    }

//    private lateinit var defaultExceptionHandler: Thread.UncaughtExceptionHandler
//    val customExceptionHandler: Thread.UncaughtExceptionHandler = Thread.UncaughtExceptionHandler { thread, throwable ->
//        defaultExceptionHandler.uncaughtException(thread, throwable)
//    }

}
