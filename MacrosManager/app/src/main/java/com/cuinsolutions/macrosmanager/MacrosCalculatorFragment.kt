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

                        val signInAlert = android.app.AlertDialog.Builder(requireContext())
                        val emailEditText = EditText(this)
                        val passwordEditText = EditText(this)
                        emailEditText.hint = "Email"
                        passwordEditText.hint = "Password"
                        emailEditText.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                        passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                        val signInLayout = LinearLayout(requireContext())
                        signInLayout.orientation = LinearLayout.VERTICAL
                        signInLayout.addView(emailEditText)
                        signInLayout.addView(passwordEditText)
                        signInAlert.setView(signInLayout)

                        signInAlert.setTitle("Sign In").setMessage("Please enter your email and password to sign in.").setPositiveButton("Sign In") {
                                dialog, which ->  macrosManagerViewModel.auth.signInWithEmailAndPassword(emailEditText.text.toString(), passwordEditText.text.toString()).addOnSuccessListener {
                            recreate()
                        }.addOnFailureListener {

                            val loginFailAlert = android.app.AlertDialog.Builder(requireContext())
                            loginFailAlert.setTitle("Login Failed").setMessage("Your login failed. Please try again later.").setNeutralButton("Ok") {
                                    dialog, which ->  dialog.dismiss()
                            }

                            loginFailAlert.show()
                        }
                        }.setNegativeButton("Cancel") {
                                dialog, which ->  dialog.dismiss()
                        }

                        signInAlert.show()
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
                dailyActivityInfoDialog.setPositiveButton("OK", null)
                dailyActivityInfoDialog.show()
            }
            binding.calculatorDietFatPercentInfo -> {
                val fatPercentDialog = AlertDialog.Builder(requireContext())
                fatPercentDialog.setMessage(R.string.fat_percent_explanation)
                fatPercentDialog.setPositiveButton("OK", null)
                fatPercentDialog.show()
            }
            binding.calculatorCalculate -> {
                Log.d("Calculate", "button")

                when (heightMeasurement) {

                    "imperial" -> {

                        if (feetEditText.text.toString() == "" || inchesEditText.text.toString() == "" || (inchesEditText.text.toString().toDouble()) >= 13
                            || (inchesEditText.text.toString().toDouble()) < 0 || !regexs.validNumber(feetEditText.text.toString()) ||
                            !regexs.validNumber(inchesEditText.text.toString())) {

                            val heightAlert = AlertDialog.Builder(this)
                            heightAlert.setTitle("Height Error").setMessage("Please enter a valid number for both entries.").setPositiveButton("OK") {

                                    dialog, which ->  dialog.cancel()
                            }

                            heightAlert.show()
                        } else {

                            weightCheck(poundsEditText, fatPercentageEditText, regexs, feetEditText, inchesEditText, cmEditText,
                                kilogramsEditText, stoneEditText, decimalFormat, caloriesCalculatedTextView,
                                proteinCalculatedTextView, fatsCalculatedTextView, carbsCalculatedTextView)
                        }
                    }

                    "metric" -> {

                        if (cmEditText.text.toString() == "" || !regexs.validNumber(cmEditText.text.toString())) {

                            val heightAlert = AlertDialog.Builder(this)
                            heightAlert.setTitle("Height Error").setMessage("Please enter a valid number for entry.").setPositiveButton("OK") {

                                    dialog, which ->
                                dialog.cancel()
                            }

                            heightAlert.show()
                        } else {
                            weightCheck(poundsEditText, fatPercentageEditText, regexs, feetEditText, inchesEditText, cmEditText,
                                kilogramsEditText, stoneEditText, decimalFormat, caloriesCalculatedTextView,
                                proteinCalculatedTextView, fatsCalculatedTextView, carbsCalculatedTextView)
                        }
                    }
                }
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

                Toast.makeText(this, "There was an issue getting your data. Please try again later.", Toast.LENGTH_SHORT).show()
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
            val errorDialog = AlertDialog.Builder(this)

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

    private fun calculate(feetEditText: EditText, inchesEditText: EditText, cmEditText: EditText, fatPercentageEditText: EditText, poundsEditText: EditText, kilogramsEditText: EditText, stoneEditText: EditText, decimalFormat: DecimalFormat,
                          weightMeasurement: String, birthDate: Date, gender: String) {

        val userPreferences = PreferencesManager().getInstance()

        dietFatPercent = fatPercentageEditText.text.toString().toDouble()

        when (weightMeasurement) {

            "imperial" -> {

                pounds = poundsEditText.text.toString().toDouble()
                kg = pounds * 0.454
                stone = (pounds / 14)
            }

            "metric" -> {

                kg = kilogramsEditText.text.toString().toDouble()
                pounds = kg / 0.454
                stone = kg * .157
            }

            "stone" -> {

                stone = stoneEditText.text.toString().toDouble()
                pounds = stone * 14
                kg = stone * 6.35
            }
        }

        when (heightMeasurement) {

            "imperial" -> {

                val totalInches = ((Integer.valueOf(feetEditText.text.toString())) * 12) + (inchesEditText.text.toString().toDouble())
                val cmCalced = totalInches * 2.54

                feet = feetEditText.text.toString().toInt()
                inches = inchesEditText.text.toString().toDouble()//editor.putString("inches", decimalFormat.format(inchesEditText.text.toString().toDouble()).toString())
                cm = cmCalced//editor.putString("cm", decimalFormat.format(cm).toString())
            }

            "metric"-> {

                val feetConversion = (cmEditText.text.toString().toFloat()) / 30.48
                val feetRemainder = feetConversion % 1
                val feetCalced = feetConversion - feetRemainder
                val inchesCalced = ((cmEditText.text.toString().toFloat()) / 2.54) - (feetCalced * 12) + feetRemainder

                cm = cmEditText.text.toString().toDouble()
                feet = feetCalced.toInt()//editor.putString("feet", decimalFormat.format(feet).toString())
                inches = inchesCalced
            }
        }

        var bmr = 0.0
        val tdee: Double
        val calories: Double
        val protein: Double
        val proteinCalories: Double
        val fat: Double
        val fatCalories: Double
        val carbs: Double
        val carbsCalories: Double
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")//SimpleDateFormat()
        val age = if (Date().month >= birthDate.month) {
            Date().year - birthDate.year
        } else {
            Date().year - birthDate.year - 1//(java.util.concurrent.TimeUnit.DAYS.convert(dateFormat.parse(DateTime.now().toString()).time -
        }
                //dateFormat.format(birthDate), java.util.concurrent.TimeUnit.MILLISECONDS) / 365)

        Log.d("Current Date", Date().toString())
        Log.d("Birth Date", birthDate.toString())
        Log.d("Current Year", Date().year.toString())
        Log.d("Birth Year", birthDate.year.toString())
        Log.d("Caled_Age", age.toString())
        when (gender) {

            "male" -> {

                bmr = (10 * kg) + (6.25 * cm) - ((5 * age) + 5)
            }

            "female" -> {

                bmr = (10 * kg) + (6.25 * cm) - ((5 * age) - 161)
            }
        }

        when (dailyActivity) {

            "very light" -> {

                tdee = bmr * 1.20
            }

            "light" -> {

                tdee = bmr * 1.45
            }

            "moderate" -> {

                tdee = bmr * 1.55
            }

            "heavy" -> {

                tdee = bmr * 1.75
            }

            "very heavy" -> {

                tdee = 2.00
            }

            else -> {

                tdee = bmr
            }
        }

        when (goal) {

            "weightLossSuggested" -> {

                calories = tdee - (tdee * 0.15)
            }

            "weightLossAggressive" -> {

                calories = tdee - (tdee * 0.20)
            }

            "weightLossReckless" -> {

                calories = tdee - (tdee * 0.25)
            }

            "maintain" -> {

                calories = tdee
            }

            "bulkingSuggested" -> {

                calories = tdee + (tdee * 0.05)
            }

            "bulkingAggressive" -> {

                calories = tdee + (tdee * 0.10)
            }

            "bulkingReckless" -> {

                calories = tdee + (tdee * 0.15)
            }

            else -> {

                calories = tdee
            }
        }

        when (physicalActivityLifestyle) {

            "sedentaryAdult" -> {

                protein = pounds * 0.4
            }

            "recreationalExerciserAdult" -> {

                protein = pounds * 0.75
            }

            "competitiveAthleteAdult" -> {

                protein = pounds * 0.90
            }

            "buildingMuscleAdult" -> {

                protein = pounds * 0.90
            }

            "dietingAthlete" -> {

                protein = pounds * 0.90
            }

            "growingAthleteTeenager" -> {

                protein = pounds * 1.0
            }

            else -> {

                protein = pounds * 0.4
            }
        }

        proteinCalories = protein * 4

        fatCalories = calories * (fatPercentageEditText.text.toString().toDouble() / 100)
        fat = fatCalories / 9

        Log.d("tdee", tdee.toString())
        Log.d("proteinCal", proteinCalories.toString())
        Log.d("fatCal", fatCalories.toString())

        carbsCalories = calories - (proteinCalories + fatCalories)
        carbs = carbsCalories / 4

        val currentUser = auth.currentUser

        if (currentUser != null) {

            val userId = currentUser.uid
            val db = fireStore.collection("users").document(userId)
            val updateData = hashMapOf("pounds" to pounds, "kg" to kg, "stone" to stone, "calories" to calories.toInt(), "carbs" to carbs.toInt(), "fat" to fat.toInt(),
                    "protein" to protein.toInt(), "dietFatPercent" to dietFatPercent, "dailyActivity" to dailyActivity, "physicalActivityLifestyle" to physicalActivityLifestyle,
                    "goal" to goal)
            db.update(updateData).addOnSuccessListener {
                Toast.makeText(requireContext(), "Your data has been successfully updated.", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "There was an issue updating the data. Please try again later.", Toast.LENGTH_SHORT).show()
            }
        } else {

            val createUserDialog = AlertDialog.Builder(requireContext())

            createUserDialog.setTitle("Create Account").setMessage("Would you like to create an account to " +
                    "store your settings and macros?").setPositiveButton("OK") { dialog, which ->

            }
                .setNegativeButton("No Thanks", { dialog, which ->

            })

            createUserDialog.show()
        }
    }

    fun weightCheck(poundsEditText: EditText, fatPercentageEditText: EditText, regexs: Regexs, feetEditText: EditText, inchesEditText: EditText, cmEditText: EditText,
                    kilogramsEditText: EditText, stoneEditText: EditText, decimalFormat: DecimalFormat, caloriesCalculatedTextView: TextView,
                    proteinCalculatedTextView: TextView, fatsCalculatedTextView: TextView, carbsCalculatedTextView: TextView) {

        if (weightMeasurement == "imperial") {

            if (poundsEditText.text.toString() == "" || fatPercentageEditText.text.toString() == "" || !regexs.validNumber(poundsEditText.text.toString())
                    || !regexs.validNumber(fatPercentageEditText.text.toString())) {

                val weightErrorDialog = AlertDialog.Builder(this)

                weightErrorDialog.setTitle("Weight Error").setMessage("Please enter a valid entry for all fields.").setPositiveButton("OK") {

                    dialog, which ->
                    dialog.cancel()
                }

                weightErrorDialog.show()
            } else {

                calculate(feetEditText, inchesEditText, cmEditText, fatPercentageEditText, poundsEditText, kilogramsEditText, stoneEditText, decimalFormat, weightMeasurement, birthDate, gender)
                caloriesCalculatedTextView.text = calories.toString()
                proteinCalculatedTextView.text = protein.toString() + "g"
                fatsCalculatedTextView.text = fat.toString() + "g"
                carbsCalculatedTextView.text = carbs.toString() + "g"
            }
        } else if (weightMeasurement == "metric") {

            if (kilogramsEditText.text.toString() == "" || fatPercentageEditText.text.toString() == "" || !regexs.validNumber(kilogramsEditText.text.toString())
                    || !regexs.validNumber(fatPercentageEditText.toString())) {

                val weightErrorDialog = AlertDialog.Builder(this)

                weightErrorDialog.setTitle("Weight Error").setMessage("Please enter a valid entry for all fields.").setPositiveButton("OK") {

                    dialog, which ->
                    dialog.cancel()
                }

                weightErrorDialog.show()
            } else {

                calculate(feetEditText, inchesEditText, cmEditText, fatPercentageEditText, poundsEditText, kilogramsEditText, stoneEditText, decimalFormat, weightMeasurement, birthDate, gender)
                caloriesCalculatedTextView.text = calories.toString()
                proteinCalculatedTextView.text = protein.toString() + "g"
                fatsCalculatedTextView.text = fat.toString() + "g"
                carbsCalculatedTextView.text = carbs.toString() + "g"
            }
        } else if (weightMeasurement == "stone") {

            if (stoneEditText.text.toString() == "" || fatPercentageEditText.text.toString() == "" || !regexs.validNumber(stoneEditText.text.toString())
                    || !regexs.validNumber(fatPercentageEditText.text.toString())) {

                val weightErrorDialog = AlertDialog.Builder(this)

                weightErrorDialog.setTitle("Weight Error").setMessage("Please enter a valid entry fo all fields").setPositiveButton("OK") {

                    dialog, which ->
                    dialog.cancel()
                }

                weightErrorDialog.show()
            } else {

                calculate(feetEditText, inchesEditText, cmEditText, fatPercentageEditText, poundsEditText, kilogramsEditText, stoneEditText, decimalFormat, weightMeasurement, birthDate, gender)
                caloriesCalculatedTextView.text = calories.toString()
                proteinCalculatedTextView.text = protein.toString() + "g"
                fatsCalculatedTextView.text = fat.toString() + "g"
                carbsCalculatedTextView.text = carbs.toString() + "g"
            }
        } else {

            Log.d("Inside calc", "calculator")

        }
    }
}
