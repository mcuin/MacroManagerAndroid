package com.cuinsolutions.macrosmanager

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.cuinsolutions.macrosmanager.databinding.FragmentMacrosCalculatorBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class MacrosCalculatorFragment : Fragment(), OnClickListener {

    lateinit var binding: FragmentMacrosCalculatorBinding
    val macrosManagerViewModel: MacrosManagerViewModel by activityViewModels {
        MacrosManagerViewModel.MacrosManagerFactory(requireActivity().application)
    }
    val macrosCalculatorViewModel: MacrosCalculatorViewModel by viewModels()
    private lateinit var heightMeasurement: String
    private lateinit var weightMeasurement: String
    private lateinit var gender: String
    private lateinit var birthDate: Date
    private var cm = 0.0
    private var dailyActivity = ""
    private var physicalActivityLifestyle = ""
    private var goal = ""
    private var feet: Int = 0
    private var inches = 0.0
    private var pounds = 0.0
    private var kg = 0.0
    private var stone = 0.0
    private var dietFatPercent = 0.0
    private var calories = 0
    private var carbs = 0
    private var fat = 0
    private var protein = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

            when (checkedId) {
                R.id.calculator_physical_activity_lifestyle_sedentary_adult -> physicalActivityLifestyle = "sedentaryAdult"
                R.id.calculator_physical_activity_lifestyle_adult_recreational_exerciser -> physicalActivityLifestyle = "recreationalExerciserAdult"
                R.id.calculator_physical_activity_lifestyle_adult_competitive_athlete ->physicalActivityLifestyle = "competitiveAthleteAdult"
                R.id.calculator_physical_activity_lifestyle_adult_building_muscle -> physicalActivityLifestyle = "buildingMuscleAdult"
                R.id.calculator_physical_activity_lifestyle_dieting_athlete -> physicalActivityLifestyle = "dietingAthlete"
                R.id.calculator_physical_activity_lifestyle_teen_growing_athlete -> physicalActivityLifestyle = "growingAthleteTeenager"
            }
        }

        binding.calculatorDailyActivityLevelGroup.setOnCheckedChangeListener { _, checkedId ->

            when (checkedId) {
                R.id.calculator_daily_activity_level_very_light -> dailyActivity = "veryLight"
                R.id.calculator_daily_activity_level_light -> dailyActivity = "light"
                R.id.calculator_daily_activity_level_moderate -> dailyActivity = "moderate"
                R.id.calculator_daily_activity_level_heavy -> dailyActivity = "heavy"
                R.id.calculator_daily_activity_level_very_heavy -> dailyActivity = "veryHeavy"
            }
        }

        binding.calculatorFatPercentGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.calculator_fat_percent_twenty_five -> {}
                R.id.calculator_fat_percent_thirty -> {}
                R.id.calculator_fat_percent_thirty_five -> {}
                R.id.calculator_fat_percent_custom -> {}
            }
        }

        binding.calculatorGoalGroup.setOnCheckedChangeListener { _, checkedId ->

            when (checkedId) {
                R.id.calculator_goal_burn_suggested -> goal = "weightLossSuggested"
                R.id.calculator_goal_burn_aggressive -> goal = "weightLossAggressive"
                R.id.calculator_goal_burn_reckless -> goal = "weightLossReckless"
                R.id.calculator_goal_maintain -> goal = "maintain"
                R.id.calculator_goal_build_suggested -> goal = "bulkingSuggested"
                R.id.calculator_goal_build_aggressive-> goal = "bulkingAggressive"
                R.id.calculator_goal_build_reckless -> goal = "bulkingReckless"
            }
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
                when (heightMeasurement) {
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

                when(weightMeasurement) {
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



                calculate(feetEditText, inchesEditText, cmEditText, fatPercentageEditText, poundsEditText, kilogramsEditText, stoneEditText, decimalFormat, weightMeasurement, birthDate, gender)
                caloriesCalculatedTextView.text = calories.toString()
                proteinCalculatedTextView.text = protein.toString() + "g"
                fatsCalculatedTextView.text = fat.toString() + "g"
                carbsCalculatedTextView.text = carbs.toString() + "g"
            }
        }
    }

    override fun onResume() {
        super.onResume()

        val userPreferences = this.getSharedPreferences("userPreferences", 0)
        val currentUser = auth.currentUser

        if (currentUser != null) {

            val userId = currentUser.uid
            val data = fireStore.collection("users").document(userId)
            data.get().addOnSuccessListener {
                heightMeasurement = it.getString("heightMeasurement")!!
                weightMeasurement = it.getString("weightMeasurement")!!
                val timeStamp = it.getTimestamp("birthDate")
                birthDate = timeStamp!!.toDate()
                gender = it.getString("gender")!!
                feet = it.getLong("feet")!!.toInt()
                inches = it.getDouble("inches")!!
                cm = it.getDouble("cm")!!
                kg = it.getDouble("kg")!!
                pounds = it.getDouble("pounds")!!
                stone = it.getDouble("stone")!!
                dailyActivity = it.getString("dailyActivity")!!
                physicalActivityLifestyle = it.getString("physicalActivityLifestyle")!!
                goal = it.getString("goal")!!
                dietFatPercent = it.getDouble("dietFatPercent")!!
                calories = it.get("calories").toString().toInt()
                carbs = it.get("carbs").toString().toInt()
                fat = it.get("fat").toString().toInt()
                protein = it.get("protein").toString().toInt()
            }.addOnFailureListener {

                Toast.makeText(requireContext(), "There was an issue getting your data. Please try again later.", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else if (userPreferences.contains("weightMeasurement") && userPreferences.contains("birthDate") && userPreferences.contains("gender") &&
                userPreferences.contains("heightMeasurement")){

            heightMeasurement = userPreferences.getString("heightMeasurement", "")
            weightMeasurement = userPreferences.getString("weightMeasurement", "")
            birthDate = Date(userPreferences.getLong("birthDate", 0))
            gender = userPreferences.getString("gender", "")
            if (userPreferences.contains("feet")) {
                feet = userPreferences.getInt("feet", 0)
            }

            if (userPreferences.contains("inches")) {
                inches = userPreferences.getString("inches", "").toDouble()
            }

            if (userPreferences.contains("cm")) {
                cm = userPreferences.getString("cm", "").toDouble()
            }

            if (userPreferences.contains("kg")) {
                kg = userPreferences.getString("kg", "").toDouble()
            }

            if (userPreferences.contains("pounds")) {
                pounds = userPreferences.getString("pounds", "").toDouble()
            }

            if (userPreferences.contains("stone")) {
                stone = userPreferences.getString("stone", "").toDouble()
            }

            if (userPreferences.contains("dailyActivity")) {
                dailyActivity = userPreferences.getString("dailyActivity", "")
            }

            if (userPreferences.contains("physicalActivityLifestyle")) {
                physicalActivityLifestyle = userPreferences.getString("physicalActivityLifestyle", "")
            }

            if (userPreferences.contains("goal")) {
                goal = userPreferences.getString("goal", "")
            }

            if (userPreferences.contains("dietFatPercent")) {
                dietFatPercent = userPreferences.getString("dietFatPercent", "").toDouble()
            }

            if (userPreferences.contains("calories")) {
                calories = userPreferences.getInt("calories", 0)
            }

            if (userPreferences.contains("carbs")) {
                carbs = userPreferences.getInt("carbs", 0)
            }

            if (userPreferences.contains("fat")) {
                fat = userPreferences.getInt("fat", 0)
            }

            if (userPreferences.contains("protein")) {
                protein = userPreferences.getInt("protein", 0)
            }

            loadUI(weightMeasurement, birthDate, gender, cm, userPreferences)
        } else {
            val errorDialog = AlertDialog.Builder(requireContext())

            errorDialog.setTitle("Settings Error").setMessage("Please visit the settings page first to fill out some information about yourself for use in " +
                    "calculations.")

            errorDialog.setPositiveButton("Go to Settings", DialogInterface.OnClickListener { dialogInterface, i ->

                val settingsIntent = Intent(this, SettingsActivity::class.java)

                startActivity(settingsIntent)
            })

            errorDialog.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialogInterface, i ->

                finish()
            })

            errorDialog.show()
        }
    }
}
