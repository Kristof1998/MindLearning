package hu.droth.kristof.mindlearning.view.mainScreen.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mindlearning.util.getStringResource
import com.google.android.material.bottomnavigation.BottomNavigationView
import hu.droth.kristof.mindlearning.R
import hu.droth.kristof.mindlearning.databinding.IntelligenceTypeChooserFragmentBinding
import hu.droth.kristof.mindlearning.model.IntelligenceType
import hu.droth.kristof.mindlearning.view.mainScreen.adapters.IntelligenceTypeGVAdapter
import hu.droth.kristof.mindlearning.view.mainScreen.viewModels.IntelligenceTypeChooserViewModel
import kotlinx.android.synthetic.main.intelligence_type_chooser_fragment.*

class IntelligenceTypeChooserFragment : Fragment() {


    private val viewModel: IntelligenceTypeChooserViewModel by viewModels()
    private val args: IntelligenceTypeChooserFragmentArgs by navArgs()
    private lateinit var binding: IntelligenceTypeChooserFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.intelligence_type_chooser_fragment,
            container,
            false
        )
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        return binding.root
    }


    private fun setWordTheme() {
        viewModel.wordThemeText.postValue(requireContext().getString(args.wordThemeType.getStringResource()))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setWordTheme()
        setGridView()
    }

    override fun onStart() {
        super.onStart()
        val navBar =
            activity?.findViewById<BottomNavigationView>(R.id.mainFragmentBottomNavigationBar)
        navBar?.visibility = View.VISIBLE
    }

    private fun setGridView() {
        val enum = enumValues<IntelligenceType>().asList()
        val gridViewAdapter = IntelligenceTypeGVAdapter(
            context = requireContext(),
            navController = findNavController(),
            wordTheme = args.wordThemeType,
            enum = enum
        )
        gvIntelligenceTypeSelector.adapter = gridViewAdapter

    }
}