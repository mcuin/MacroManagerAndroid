package com.cuinsolutions.macrosmanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.cuinsolutions.macrosmanager.databinding.FragmentMacrosCalculatorBinding
import kotlinx.coroutines.launch

class MacrosCalculatorFragment : Fragment(), OnClickListener {

    lateinit var binding: FragmentMacrosCalculatorBinding
    private val macrosManagerViewModel: MacrosManagerViewModel by activityViewModels {
        MacrosManagerViewModel.MacrosManagerFactory(requireActivity().application)
    }
    private val macrosCalculatorViewModel: MacrosCalculatorViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_macros_calculator, container, false)

        binding.listener = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                if (macrosManagerViewModel.auth.currentUser != null) {
                    menuInflater.inflate(R.menu.action_bar_menu, menu)
                } else {
                    menuInflater.inflate(R.menu.no_user_menu, menu)
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {

                    R.id.action_settings -> {
                        findNavController().navigate(MacrosCalculatorFragmentDirections.navigateToSettings())
                    }

                    R.id.action_sign_in -> {
                        findNavController().navigate(MacrosCalculatorFragmentDirections.navigateToSignIn())
                    }
                }

                return true
            }
        })

        binding.calculatorPhysicalActivityLifestyleGroup.setOnCheckedChangeListener { _, checkedId ->

            macrosManagerViewModel.currentUserCalculatorOptions.physicalActivityLifestyle = when (checkedId) {
                R.id.calculator_physical_activity_lifestyle_sedentary_adult -> PhysicalActivityLifestyle.SEDENTARYADULT.lifeStyle
                R.id.calculator_physical_activity_lifestyle_adult_recreational_exerciser -> PhysicalActivityLifestyle.SEDENTARYADULT.lifeStyle
                R.id.calculator_physical_activity_lifestyle_adult_competitive_athlete -> PhysicalActivityLifestyle.SEDENTARYADULT.lifeStyle
                R.id.calculator_physical_activity_lifestyle_adult_building_muscle -> PhysicalActivityLifestyle.SEDENTARYADULT.lifeStyle
                R.id.calculator_physical_activity_lifestyle_dieting_athlete -> PhysicalActivityLifestyle.SEDENTARYADULT.lifeStyle
                R.id.calculator_physical_activity_lifestyle_teen_growing_athlete -> PhysicalActivityLifestyle.SEDENTARYADULT.lifeStyle
                else -> PhysicalActivityLifestyle.SEDENTARYADULT.lifeStyle
            }
        }

        binding.calculatorDailyActivityLevelGroup.setOnCheckedChangeListener { _, checkedId ->

            macrosManagerViewModel.currentUserCalculatorOptions.dailyActivity = when (checkedId) {
                R.id.calculator_daily_activity_level_very_light -> DailyActivityLevel.VERYLIGHT.level
                R.id.calculator_daily_activity_level_light -> DailyActivityLevel.LIGHT.level
                R.id.calculator_daily_activity_level_moderate -> DailyActivityLevel.MODERATE.level
                R.id.calculator_daily_activity_level_heavy -> DailyActivityLevel.HEAVY.level
                R.id.calculator_daily_activity_level_very_heavy -> DailyActivityLevel.VERYHEAVY.level
                else -> DailyActivityLevel.VERYLIGHT.level
            }
        }

        binding.calculatorFatPercentGroup.setOnCheckedChangeListener { _, checkedId ->
            binding.customFat = false
            when (checkedId) {
                R.id.calculator_fat_percent_twenty_five -> macrosManagerViewModel.currentUserCalculatorOptions.dietFatPercent = 25.0
                R.id.calculator_fat_percent_thirty -> macrosManagerViewModel.currentUserCalculatorOptions.dietFatPercent = 30.0
                R.id.calculator_fat_percent_thirty_five -> macrosManagerViewModel.currentUserCalculatorOptions.dietFatPercent = 35.0
                R.id.calculator_fat_percent_custom -> {
                    binding.customFat = true
                }
            }
        }

        binding.calculatorGoalGroup.setOnCheckedChangeListener { _, checkedId ->
            macrosManagerViewModel.currentUserCalculatorOptions.goal = when (checkedId) {
                R.id.calculator_goal_burn_suggested -> Goal.BURNSUGGESTED.goal
                R.id.calculator_goal_burn_aggressive -> Goal.BURNAGGRESSIVE.goal
                R.id.calculator_goal_burn_reckless -> Goal.BURNRECKLESS.goal
                R.id.calculator_goal_maintain -> Goal.MAINTAIN.goal
                R.id.calculator_goal_build_suggested -> Goal.BUILDSUGGESTED.goal
                R.id.calculator_goal_build_aggressive-> Goal.BUILDAGGRESSIVE.goal
                R.id.calculator_goal_build_reckless -> Goal.BUILDRECKLESS.goal
                else -> Goal.MAINTAIN.goal
            }
        }

        if (macrosManagerViewModel.currentUserInfo.birthMonth != -1) {
            binding.heightMeasurement = macrosManagerViewModel.currentUserInfo.heightMeasurement
            binding.weightMeasurement = macrosManagerViewModel.currentUserInfo.weightMeasurement

        } else {
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.settings_error)
                .setMessage(R.string.settings_error_description)
                .setPositiveButton(R.string.go_to_settings) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                    findNavController().navigate(MacrosCalculatorFragmentDirections.navigateToSettings())
                }
                .setNegativeButton(R.string.cancel) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                    findNavController().popBackStack()
                }
                .show()
        }

    }

    override fun onClick(view: View) {
        when (view) {
            binding.calculatorDailyActivityLevelInfo -> {
                val dailyActivityInfoDialog = AlertDialog.Builder(requireContext())
                dailyActivityInfoDialog.setMessage(R.string.daily_activity_level_explanation)
                dailyActivityInfoDialog.setPositiveButton(R.string.ok, null)
                dailyActivityInfoDialog.show()
            }
            binding.calculatorDietFatPercentInfo -> {
                val fatPercentDialog = AlertDialog.Builder(requireContext())
                fatPercentDialog.setMessage(R.string.fat_percent_explanation)
                fatPercentDialog.setPositiveButton(R.string.ok, null)
                fatPercentDialog.show()
            }
            binding.calculatorCalculate -> {
                var centimeters = -1.0
                var kilograms = -1.0
                when (macrosManagerViewModel.currentUserInfo.heightMeasurement) {
                    "imperial" -> {
                        if (binding.calculatorHeightFeetEdit.text.toString().isBlank() ||
                            binding.calculatorHeightInchesEdit.text.toString().isBlank() ||
                            !(0.0..12.0).contains(binding.calculatorHeightInchesEdit.text.toString().toDouble())
                            || !Regexs().validNumber(binding.calculatorHeightFeetEdit.text.toString()) ||
                            !Regexs().validNumber(binding.calculatorHeightInchesEdit.text.toString())) {

                            AlertDialog.Builder(requireContext())
                            .setTitle(R.string.height_error)
                            .setMessage(R.string.height_entry_errors)
                            .setPositiveButton(R.string.ok) { dialog, _ ->
                                dialog.cancel()
                            }.show()

                            return
                        }

                        centimeters = macrosCalculatorViewModel.heightImperialToMetric(binding.calculatorHeightFeetEdit.text.toString().toInt(),
                            binding.calculatorHeightInchesEdit.text.toString().toDouble())
                    }

                    "metric" -> {
                        if (binding.calculatorHeightCentimetersEdit.text.toString().isBlank() ||
                            !Regexs().validNumber(binding.calculatorHeightCentimetersEdit.text.toString())) {

                            AlertDialog.Builder(requireContext())
                                .setTitle(R.string.height_error)
                                .setMessage(R.string.height_entry_error)
                                .setPositiveButton(R.string.ok) { dialog, _ ->
                                    dialog.cancel()
                                }.show()

                            return
                        }

                        centimeters = binding.calculatorHeightCentimetersEdit.text.toString().toDouble()
                    }
                }

                when(macrosManagerViewModel.currentUserInfo.weightMeasurement) {
                    "imperial" -> {
                        if (binding.calculatorWeightPoundsEdit.text.toString().isBlank() ||
                             !Regexs().validNumber(binding.calculatorWeightPoundsEdit.text.toString())) {

                            AlertDialog.Builder(requireContext())
                                .setTitle(R.string.weight_error)
                                .setMessage(R.string.weight_entry_error)
                                .setPositiveButton(R.string.ok) { dialog, _ ->
                                    dialog.cancel()
                                }.show()

                            return
                        }

                        kilograms = macrosCalculatorViewModel.weightImperialToMetric(binding.calculatorWeightPoundsEdit.text.toString().toDouble())
                    }
                    "metric" -> {
                        if (binding.calculatorWeightKilogramsEdit.text.toString().isBlank() ||
                            !Regexs().validNumber(binding.calculatorWeightKilogramsEdit.text.toString())) {

                            AlertDialog.Builder(requireContext())
                                .setTitle(R.string.weight_error)
                                .setMessage(R.string.weight_entry_error)
                                .setPositiveButton(R.string.ok) { dialog, _ ->
                                    dialog.cancel()
                                }.show()

                            return
                        }

                        kilograms = binding.calculatorWeightKilogramsEdit.text.toString().toDouble()
                    }
                    "stone" -> {
                        if (binding.calculatorWeightStoneEdit.text.toString().isBlank() ||
                            !Regexs().validNumber(binding.calculatorWeightStoneEdit.text.toString())) {

                            AlertDialog.Builder(requireContext())
                                .setTitle(R.string.weight_error)
                                .setMessage(R.string.weight_entry_errors)
                                .setPositiveButton(R.string.ok) { dialog, _ ->
                                    dialog.cancel()
                                }.show()

                            return
                        }

                        kilograms = macrosCalculatorViewModel.weightStoneToMetric(binding.calculatorWeightStoneEdit.text.toString().toInt(),
                            binding.calculatorWeightPoundsEdit.text.toString().toDouble())
                    }
                }

                if (binding.calculatorFatPercentCustom.isChecked &&
                    (binding.calculatorFatPercentCustomEdit.text.toString().isBlank() || !
                    Regexs().validNumber(binding.calculatorFatPercentCustomEdit.text.toString()))) {

                    AlertDialog.Builder(requireContext())
                        .setTitle(R.string.custom_fat_error)
                        .setMessage(R.string.custom_fat_entry_error)
                        .setPositiveButton(R.string.ok) { dialog, _ ->
                            dialog.cancel()
                        }.show()

                    return
                }

                lifecycleScope.launch {
                    macrosCalculatorViewModel.calculate(centimeters, kilograms, macrosManagerViewModel.currentUserInfo,
                        macrosManagerViewModel.currentUserCalculatorOptions, macrosManagerViewModel.currentUserMacros)
                }
            }
        }
    }
}
