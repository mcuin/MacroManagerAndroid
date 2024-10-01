package com.cuinsolutions.macrosmanager

import android.icu.util.Calendar
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.cuinsolutions.macrosmanager.databinding.FragmentSettingsBinding
import com.cuinsolutions.macrosmanager.utils.Gender
import com.cuinsolutions.macrosmanager.utils.HeightMeasurement
import com.cuinsolutions.macrosmanager.utils.WeightMeasurement
import kotlinx.coroutines.launch

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
            Gender.MALE.id -> R.id.settings_radio_male
            Gender.FEMALE.id -> R.id.settings_radio_female
            else -> R.id.settings_radio_male
        }
        binding.weightId = when (macrosManagerViewModel.currentUserInfo.value.weightMeasurement) {
            WeightMeasurement.IMPERIAL.id -> R.id.settings_weight_imperial
            WeightMeasurement.METRIC.id -> R.id.settings_weight_metric
            WeightMeasurement.STONE.id -> R.id.settings_weight_stone
            else -> R.id.settings_weight_metric
        }
        binding.heightId = when (macrosManagerViewModel.currentUserInfo.value.heightMeasurement) {
            HeightMeasurement.IMPERIAL.id -> R.id.settings_height_imperial
            HeightMeasurement.METRIC.id -> R.id.settings_height_metric
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
                R.id.settings_radio_male -> Gender.MALE.id
                R.id.settings_radio_female -> Gender.FEMALE.id
                else -> Gender.MALE.id
            }
        }

        binding.settingsWeightRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            tempUserSettings.weightMeasurement = when(checkedId) {
                R.id.settings_weight_imperial -> WeightMeasurement.IMPERIAL.id
                R.id.settings_weight_metric -> WeightMeasurement.METRIC.id
                R.id.settings_weight_stone -> WeightMeasurement.STONE.id
                else -> WeightMeasurement.METRIC.id
            }
        }

        binding.settingsHeightRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            tempUserSettings.heightMeasurement = when(checkedId) {
                R.id.settings_height_imperial -> HeightMeasurement.IMPERIAL.id
                R.id.settings_height_metric -> HeightMeasurement.METRIC.id
                else -> HeightMeasurement.METRIC.id
            }
        }
    }

    override fun onClick(view: View) {
        when(view.id) {
            R.id.settings_birth_date -> {
                val calendar = Calendar.getInstance()
                val maxMonth = calendar.get(Calendar.MONTH)
                val maxYear = calendar.get(Calendar.YEAR) - 18
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