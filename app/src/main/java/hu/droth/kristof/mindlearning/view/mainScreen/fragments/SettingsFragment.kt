package hu.droth.kristof.mindlearning.view.mainScreen.fragments

import android.os.Bundle
import android.view.View
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import hu.droth.kristof.mindlearning.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onStart() {
        super.onStart()
        val navBar =
            activity?.findViewById<BottomNavigationView>(R.id.mainFragmentBottomNavigationBar)
        navBar?.visibility = View.VISIBLE
    }
}