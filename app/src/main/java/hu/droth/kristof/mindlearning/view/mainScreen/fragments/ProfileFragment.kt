package hu.droth.kristof.mindlearning.view.mainScreen.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import hu.droth.kristof.mindlearning.R
import hu.droth.kristof.mindlearning.databinding.ProfileFragmentBinding
import hu.droth.kristof.mindlearning.model.BlurType
import hu.droth.kristof.mindlearning.model.IntelligenceType
import hu.droth.kristof.mindlearning.model.WordTheme
import hu.droth.kristof.mindlearning.model.entity.Fill
import hu.droth.kristof.mindlearning.model.entity.Player
import hu.droth.kristof.mindlearning.model.helperClasses.InProgressCourse
import hu.droth.kristof.mindlearning.util.CHART_COLOR
import hu.droth.kristof.mindlearning.util.getStringResource
import hu.droth.kristof.mindlearning.view.mainScreen.adapters.InProgressCoursesRVAdapter
import hu.droth.kristof.mindlearning.view.mainScreen.viewModels.ProfileFragmentViewModel
import kotlinx.android.synthetic.main.profile_fragment.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

const val WRITE_EXTERNAL_STORAGE_PERMISSION = 100

@AndroidEntryPoint
class ProfileFragment : Fragment(), InProgressCoursesRVAdapter.InProgressCourseClickListener,
    EasyPermissions.PermissionCallbacks {

    private val viewModel: ProfileFragmentViewModel by viewModels()


    private lateinit var binding: ProfileFragmentBinding
    private lateinit var recycleViewAdapter: InProgressCoursesRVAdapter
    private lateinit var accountArrayAdapter: ArrayAdapter<String>
    private lateinit var btnCreateAccount: Button
    private lateinit var toggleBtnGender: ToggleButton
    private lateinit var etNewUserName: EditText
    private lateinit var createPlayerAlertDialog: AlertDialog
    private lateinit var switchPlayerAlertDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.profile_fragment,
            container,
            false
        )
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        setLiveDataObservables()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setButtonClicks()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(requireActivity()).build().show()
        }
    }

    override fun onStart() {
        super.onStart()
        val navBar =
            activity?.findViewById<BottomNavigationView>(R.id.mainFragmentBottomNavigationBar)
        navBar?.visibility = View.VISIBLE
    }

    private fun setButtonClicks() {
        btnSelectAccount.setOnClickListener { openAccountDialog() }
        btnExportStatistic.setOnClickListener { exportStatistic() }
    }

    private fun setLiveDataObservables() {
        //Set current progresses
        viewModel.inProgressCourses.observe(viewLifecycleOwner, {
            setInProgressRecycleView(it)
        })
        //Set accounts
        viewModel.accounts.observe(viewLifecycleOwner, { playerList ->
            selectAccountArrayAdapter(playerList)
        })
        viewModel.fillsByIntelligenceType.observe(viewLifecycleOwner, {
            setIntelligenceTypePieChart(it)
        })
        viewModel.fillsByWordTheme.observe(viewLifecycleOwner, {
            setWordDataPieChart(it)
        })
        viewModel.shouldCreateExportFile.observe(viewLifecycleOwner, {
            createExportFile(it)
        })


    }

    private fun createExportFile(data: String) {
        val storageState = android.os.Environment.getExternalStorageState()
        if (storageState != android.os.Environment.MEDIA_MOUNTED) {
            Toast.makeText(
                requireContext(),
                getString(R.string.can_not_access_external_storage),
                Toast.LENGTH_LONG
            ).show()
            return
        }
        val rootDirectory =
            requireContext().getExternalFilesDir("/")?.parentFile?.parentFile?.parentFile?.parentFile!!
        val exportDirectory = File(rootDirectory, "Mindlearning exports")
        if (!exportDirectory.exists()) {
            exportDirectory.mkdir()
        }
        val currentDate =
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS"))
        val fileName = "Mindlearning export $currentDate.txt"
        val newFile = File(exportDirectory, fileName)
        try {
            if (!newFile.exists()) {
                newFile.createNewFile()
            }
            newFile.writeText(data)
            Toast.makeText(
                requireContext(),
                getString(R.string.file_created_in_directory) + exportDirectory.absolutePath,
                Toast.LENGTH_LONG
            ).show()
            Log.d("PROFILE_FRAGMENT", newFile.absolutePath.toString())
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                e.message ?: getString(R.string.unknown_error),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    @AfterPermissionGranted(WRITE_EXTERNAL_STORAGE_PERMISSION)
    private fun exportStatistic() {
        val permission = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (EasyPermissions.hasPermissions(requireContext(), *permission)) {
            openExportStatisticDialog()
        } else {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.need_to_access_to_external_storage_to_export_the_statistic_file),
                WRITE_EXTERNAL_STORAGE_PERMISSION,
                *permission
            )
        }

    }

    private fun openExportStatisticDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val viewGroup = view?.parent as ViewGroup
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialoge_export_settings, viewGroup, false)
        val selectedPlayer = dialogView.findViewById<ToggleButton>(R.id.btnSelectedPlayer)
        val sessionData = dialogView.findViewById<CheckBox>(R.id.cbSessionData)
        val playerData = dialogView.findViewById<CheckBox>(R.id.cbPlayerData)
        val wordData = dialogView.findViewById<CheckBox>(R.id.cbWordData)
        val fillData = dialogView.findViewById<CheckBox>(R.id.cbFillData)
        val btnExport = dialogView.findViewById<Button>(R.id.btnExport)
        val errorText = dialogView.findViewById<TextView>(R.id.tvError)
        val exportAlertDialog = builder.setView(dialogView).create()
        btnExport.setOnClickListener {
            if (!sessionData.isChecked && !playerData.isChecked && !wordData.isChecked && !fillData.isChecked) {
                errorText.visibility = View.VISIBLE
            } else {
                errorText.visibility = View.INVISIBLE
                viewModel.exportStatistic(
                    selectedPlayer.isChecked,
                    sessionData.isChecked,
                    playerData.isChecked,
                    wordData.isChecked,
                    fillData.isChecked
                )
                exportAlertDialog.hide()
            }
        }
        exportAlertDialog.show()

    }

    private fun setWordDataPieChart(data: List<Pair<WordTheme, List<Fill>>>) {
        val nonEmptyDataLists = data.filter { it.second.isNotEmpty() }
        if (nonEmptyDataLists.isEmpty()) {//no available data
            pieChartWordTheme.setNoDataText(getString(R.string.there_is_no_data_for_chart))
            pieChartWordTheme.data = null
            pieChartWordTheme.invalidate()
            return
        }
        val pieEntry = data.map {
            val value = it.second.size.toFloat()
            val label = requireContext().getString(it.first.getStringResource())
            PieEntry(value, label)
        }
        val pieDataSet = PieDataSet(pieEntry, "Fills by word theme").apply {
            colors = CHART_COLOR
        }
        val pieData = PieData(pieDataSet)
        pieChartWordTheme.legend.isEnabled = false
        pieChartWordTheme.description.isEnabled = true
        pieChartWordTheme.description = Description().apply {
            text = getString(R.string.fills_by_word_theme)
            textColor = ContextCompat.getColor(requireContext(), R.color.whiteColor)
            textSize =
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 6F, resources.displayMetrics)
        }
        pieChartWordTheme.transparentCircleRadius = 0F
        pieChartWordTheme.setHoleColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.transparentColor
            )
        )
        pieChartWordTheme.data = pieData
        pieChartWordTheme.holeRadius =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5F, resources.displayMetrics)
        pieChartWordTheme.setDrawRoundedSlices(false)
        pieChartWordTheme.animate()
        pieChartWordTheme.invalidate()
    }

    private fun setIntelligenceTypePieChart(data: List<Pair<IntelligenceType, List<Fill>>>) {
        val nonEmptyDataLists = data.filter { it.second.isNotEmpty() }
        if (nonEmptyDataLists.isEmpty()) {//no available data
            pieChartIntelligenceType.setNoDataText(getString(R.string.there_is_no_data_for_chart))
            pieChartIntelligenceType.data = null
            pieChartIntelligenceType.invalidate()
            return
        }
        val pieEntry = data.map {
            val value = it.second.size.toFloat()
            val label = requireContext().getString(it.first.getStringResource())
            PieEntry(value, label)
        }
        val pieDataSet = PieDataSet(pieEntry, "Fills by intelligence type").apply {
            colors = CHART_COLOR
        }
        val pieData = PieData(pieDataSet)
        pieChartIntelligenceType.legend.isEnabled = false
        pieChartIntelligenceType.description.isEnabled = true
        pieChartIntelligenceType.description = Description().apply {
            text = getString(R.string.fills_by_intelligence_type)
            textColor = ContextCompat.getColor(requireContext(), R.color.whiteColor)
            textSize =
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 6F, resources.displayMetrics)
        }
        pieChartIntelligenceType.transparentCircleRadius = 0F
        pieChartIntelligenceType.setHoleColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.transparentColor
            )
        )
        pieChartIntelligenceType.data = pieData
        pieChartIntelligenceType.holeRadius =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5F, resources.displayMetrics)
        pieChartIntelligenceType.setDrawRoundedSlices(false)
        pieChartIntelligenceType.animate()
        pieChartIntelligenceType.invalidate()
    }

    private fun selectAccountArrayAdapter(playerList: List<Player>) {
        accountArrayAdapter =
            ArrayAdapter(requireContext(), android.R.layout.select_dialog_singlechoice)
        playerList.forEach {
            accountArrayAdapter.add(it.name)
        }
    }

    private fun setInProgressRecycleView(progressList: List<InProgressCourse>) {
        recycleViewAdapter = InProgressCoursesRVAdapter(progressList, requireContext())
        recycleViewAdapter.itemClickListener = this
        rvProfileCurrentProgress.layoutManager = LinearLayoutManager(context)
        rvProfileCurrentProgress.adapter = recycleViewAdapter
    }

    private fun openAccountDialog() {
        val switchAccountAlertDialogBuilder =
            AlertDialog.Builder(context, R.style.SettingsAlertDialogStyle)
        switchPlayerAlertDialog =
            switchAccountAlertDialogBuilder.setTitle(getString(R.string.select_account))
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(getString(R.string.add_account)) { dialog, _ ->
                    addNewPlayerDialog()
                    dialog.dismiss()
                }
                .setAdapter(accountArrayAdapter) { dialog, which ->
                    val selectedAccountName = accountArrayAdapter.getItem(which)
                    selectedAccountName?.let {
                        viewModel.updateCurrentPlayer(it)
                    }
                    dialog.dismiss()
                }
                .show()
    }

    private fun addNewPlayerDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val viewGroup = view?.parent as ViewGroup
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialoge_create_account, viewGroup, false)
        toggleBtnGender = dialogView.findViewById(R.id.tbGender)
        etNewUserName = dialogView.findViewById(R.id.etName)
        btnCreateAccount = dialogView.findViewById(R.id.btnCreate)
        btnCreateAccount.setOnClickListener {
            createUserBtnClicked(
                etNewUserName.text.toString(),
                toggleBtnGender.isChecked
            )
        }
        builder.setView(dialogView)
        createPlayerAlertDialog = builder.create()
        createPlayerAlertDialog.show()
    }

    private fun createUserBtnClicked(name: String, checked: Boolean) {
        val trimmedName = name.trim()
        if (trimmedName.isEmpty() or trimmedName.isBlank()) {
            etNewUserName.error = getString(R.string.fill_this_line)
            return
        }

        val usersWithSameName =
            viewModel.accounts.value?.filter { it.name == trimmedName }?.size ?: 0
        if (usersWithSameName > 0) {
            etNewUserName.error = getString(R.string.user_exist_with_same_name)
            return
        }
        viewModel.createNewUser(trimmedName, checked)
        createPlayerAlertDialog.hide()
        switchPlayerAlertDialog.hide()
    }

    override fun onItemClick(course: InProgressCourse) {
        val bundle = when (course.intelligenceType) {
            IntelligenceType.VISUAL -> {
                bundleOf(
                    "intelligenceType" to course.intelligenceType,
                    "wordTheme" to course.wordTheme,
                    "blurType" to BlurType.NONE
                )
            }
            IntelligenceType.VISUAL_HARD -> {
                bundleOf(
                    "intelligenceType" to course.intelligenceType,
                    "wordTheme" to course.wordTheme,
                    "blurType" to BlurType.BLUR
                )
            }
            else -> {
                bundleOf(
                    "intelligenceType" to course.intelligenceType,
                    "wordTheme" to course.wordTheme
                )
            }
        }
        val direction = when (course.intelligenceType) {
            IntelligenceType.VERBAL -> R.id.action_profileFragment_to_gameVerbalFragment
            IntelligenceType.LOGICAL -> R.id.action_profileFragment_to_gameLogicalFragment
            IntelligenceType.VISUAL -> R.id.action_profileFragment_to_gameVisualFragment
            IntelligenceType.MUSICAL -> R.id.action_profileFragment_to_gameMusicalFragment
            IntelligenceType.VISUAL_HARD -> R.id.action_profileFragment_to_gameVisualFragment
            else -> throw NotImplementedError("No intelligence type implemented :${course.intelligenceType}")
        }
        findNavController().navigate(direction, bundle)
    }


}