package com.cuinsolutions.macrosmanager

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.icu.util.Calendar
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.dzmitry_lakisau.month_year_picker_dialog.MonthYearPickerDialog
import com.cuinsolutions.macrosmanager.databinding.FragmentSettingsBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.HashMap
import java.util.Locale

class SettingsFragment : Fragment(), OnClickListener {

    private lateinit var binding: FragmentSettingsBinding
    private val macrosManagerViewModel: MacrosManagerViewModel by activityViewModels()
    private val tempUserSettings by lazy { macrosManagerViewModel.currentUserInfo.value.copy() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)

        binding.genderId = when (macrosManagerViewModel.currentUserInfo.value.gender) {
            Gender.MALE.gender -> R.id.settings_radio_male
            Gender.FEMALE.gender -> R.id.settings_radio_female
            else -> R.id.settings_radio_male
        }
        binding.weightId = when (macrosManagerViewModel.currentUserInfo.value.weightMeasurement) {
            WeightMeasurement.IMPERIAL.measurement -> R.id.settings_weight_imperial
            WeightMeasurement.METRIC.measurement -> R.id.settings_weight_metric
            WeightMeasurement.STONE.measurement -> R.id.settings_weight_stone
            else -> R.id.settings_weight_metric
        }
        binding.heightId = when (macrosManagerViewModel.currentUserInfo.value.heightMeasurement) {
            HeightMeasurement.IMPERIAL.measurement -> R.id.settings_height_imperial
            HeightMeasurement.METRIC.measurement -> R.id.settings_height_metric
            else -> R.id.settings_height_metric
        }
        binding.settingsBirthDate.text = if (macrosManagerViewModel.currentUserInfo.value.birthYear != -1) {
             getString(R.string.birth_month_year, macrosManagerViewModel.currentUserInfo.value.birthMonth + 1,
                macrosManagerViewModel.currentUserInfo.value.birthYear)
        } else {
            getString(R.string.set_birth_date)
        }

        binding.listener = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.settingsGenderRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            tempUserSettings.gender = when(checkedId) {
                R.id.settings_radio_male -> Gender.MALE.gender
                R.id.settings_radio_female -> Gender.FEMALE.gender
                else -> Gender.MALE.gender
            }
        }

        binding.settingsWeightRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            tempUserSettings.weightMeasurement = when(checkedId) {
                R.id.settings_weight_imperial -> WeightMeasurement.IMPERIAL.measurement
                R.id.settings_weight_metric -> WeightMeasurement.METRIC.measurement
                R.id.settings_weight_stone -> WeightMeasurement.STONE.measurement
                else -> WeightMeasurement.METRIC.measurement
            }
        }

        binding.settingsHeightRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            tempUserSettings.heightMeasurement = when(checkedId) {
                R.id.settings_height_imperial -> HeightMeasurement.IMPERIAL.measurement
                R.id.settings_height_metric -> HeightMeasurement.METRIC.measurement
                else -> HeightMeasurement.METRIC.measurement
            }
        }
    }

    override fun onClick(view: View) {
        when(view.id) {
            R.id.settings_birth_date -> {
                val calendar = Calendar.getInstance()
                val maxMonth = calendar.get(Calendar.MONTH)
                val maxYear = calendar.get(Calendar.YEAR) - 18
                MonthYearPickerDialog.Builder(requireContext(),
                    R.style.MonthYearPickerStyle,
                    onDateSetListener = {year, month ->
                        tempUserSettings.birthMonth = month
                        tempUserSettings.birthYear = year

                        binding.settingsBirthDate.text = getString(R.string.birth_month_year, month + 1, year)
                    }, selectedYear = maxYear, selectedMonth = maxMonth)
                    .setMaxMonth(maxMonth)
                    .setMaxYear(maxYear)
                    .setPositiveButton(R.string.set)
                    .setNegativeButton(R.string.cancel)
                    .build().show()
            }

            R.id.settings_save -> {
                if (tempUserSettings.birthMonth == -1 || tempUserSettings.birthYear == -1
                    || tempUserSettings.birthYear == Calendar.getInstance().get(Calendar.YEAR)) {
                    AlertDialog.Builder(requireContext())
                        .setTitle(R.string.birth_info_error)
                        .setMessage(R.string.birth_info_error_description)
                        .setPositiveButton(R.string.ok, null)
                        .show()

                } else {
                    viewLifecycleOwner.lifecycleScope.launch {
                        macrosManagerViewModel.saveUserSettings(tempUserSettings)
                    }

                    if (macrosManagerViewModel.auth.currentUser == null) {
                        AlertDialog.Builder(requireContext())
                            .setTitle(R.string.create_account)
                            .setMessage(R.string.create_account_description)
                            .setPositiveButton(R.string.ok) { _, _ ->
                                findNavController().navigate(SettingsFragmentDirections.navigateToSignUp())
                            }.setNegativeButton(R.string.no_thanks) { _, _ ->
                                findNavController().popBackStack()
                            }.show()
                    } else {
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }
}