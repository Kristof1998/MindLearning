package hu.droth.kristof.mindlearning.view.authentication.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import hu.droth.kristof.mindlearning.R
import hu.droth.kristof.mindlearning.databinding.FragmentAddPersonalDataBinding
import hu.droth.kristof.mindlearning.util.closeKeyboard
import hu.droth.kristof.mindlearning.view.mainScreen.MainActivity
import kotlinx.android.synthetic.main.dialoge_create_account.*


@AndroidEntryPoint
class AddPersonalDataFragment : Fragment() {

    private lateinit var binding: FragmentAddPersonalDataBinding
    private val viewModel: AddPersonalDataViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_add_personal_data,
            container,
            false
        )
        //Connect binding with viewModel
        binding.addPersonalDataViewModel = viewModel
        //Setting liveData observables
        setLiveDataObservables()

        return binding.root
    }

    private fun setLiveDataObservables() {
        viewModel.textViewNameEmpty.observe(viewLifecycleOwner, {
            setNameError(it)
        })
        viewModel.shouldShowSnackbar.observe(viewLifecycleOwner, {
            it?.let {
                setPlayerExistSnackbar()
                viewModel.shouldShowSnackbar.value = null
            }
        })
        viewModel.shouldHideKeyboard.observe(viewLifecycleOwner, {
            it?.let {
                closeKeyboard(requireActivity())
                viewModel.shouldHideKeyboard.value = null
            }
        })
        viewModel.successFullCreatePlayer.observe(viewLifecycleOwner, {
            setPlayerCreatedSnackbar()
        })
    }

    private fun startMainActivity() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TASK)
        activity?.finish()
        startActivity(intent)
    }

    private fun setPlayerExistSnackbar() {
        val snackbar = Snackbar.make(requireView(), "Player with this name exist.", 5000)
            .setAction("REPLACE") { createAreYouSureDialog() }
        snackbar.show()
    }

    private fun setPlayerCreatedSnackbar() {
        Snackbar.make(requireView(), "Player successfully created", Snackbar.LENGTH_LONG)
            .addCallback(object :
                Snackbar.Callback() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    super.onDismissed(transientBottomBar, event)
                    startMainActivity()
                }
            }).show()

    }

    private fun createAreYouSureDialog() {
        val dialogClickListener =
            DialogInterface.OnClickListener { dialog, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        viewModel.replaceUser()
                    }
                    DialogInterface.BUTTON_NEGATIVE -> {
                        dialog.dismiss()
                    }
                }
            }


        val builder = AlertDialog.Builder(context)
        val dialog =
            builder.setMessage(getString(R.string.are_you_sure_replacing_player_this_is_irreversible_operation))
                .setPositiveButton(
                    getString(R.string.replace), dialogClickListener
                ).setNegativeButton(getString(R.string.cancel), dialogClickListener)
                .show()
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.darkTextColor))
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.darkTextColor))
    }

    private fun setNameError(isNameEmpty: Boolean) {
        if (isNameEmpty) {
            etName.error = getString(R.string.fill_this_line)
        }
    }


}