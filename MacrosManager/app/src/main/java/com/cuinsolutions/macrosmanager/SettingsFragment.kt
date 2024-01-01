package com.cuinsolutions.macrosmanager

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
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
import androidx.fragment.app.viewModels
import com.cuinsolutions.macrosmanager.databinding.FragmentSettingsBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.HashMap
import java.util.Locale

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private var showAds = true
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var birthDate = Date()
    private var gender = ""
    private var weightMeasurement = ""
    private var heightMeasurement = ""
    private var gson = Gson()
    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)

        val currentUser = auth.currentUser

        val regexs = Regexs()

        val settingsConstraintLayout = binding.settingsConstraintLayout
        val genderRadioGroup = binding.settingsGenderRadioGroup
        val heightRadioGroup = binding.settingsHeightRadioGroup
        val weightRadioGroup = binding.settingsWeightRadioGroup
        val saveButton = binding.settingsSave

        saveButton.text = getString(R.string.save)

        val decimalFormat = DecimalFormat("#.##")
        decimalFormat.roundingMode = RoundingMode.FLOOR


        //val userPreferences = PreferencesManager().getInstance()

        /*if (currentUser != null) {
            val db = firestore.collection("users").document(currentUser.uid)
            db.get().addOnSuccessListener {
                val timestamp = it.getTimestamp("birthDate")!!
                birthDate = timestamp.toDate()
                gender = it.getString("gender")!!
                weightMeasurement = it.getString("weightMeasurement")!!
                heightMeasurement = it.getString("heightMeasurement")!!
                showAds = it.getBoolean("showAds")!!

                createUI()
            }.addOnFailureListener {

                Toast.makeText(requireContext(), "There was an issue retrieving your data. Defaults have been used.", Toast.LENGTH_SHORT).show()
                birthDate = Date()
                gender = "female"
                weightMeasurement = "metric"
                heightMeasurement = "metric"
                /*cm = 165.0
                val feetConversion = cm / 30.48
                val feetRemainder = feetConversion % 1
                feet = (feetConversion - feetRemainder).toInt()
                inches = (cm / 2.54) - (feet * 12) + feetRemainder*/
            }
        } else {

            if (userPreferences.contains("birthDate")) {
                birthDate = Date(userPreferences.getLong("birthDate", 0))
            }

            if (userPreferences.contains("gender")) {
                gender = userPreferences.getString(, "")
            }

            if (userPreferences.contains("weightMeasurement")) {
                weightMeasurement = userPreferences.getString("weightMeasurement", "")
            }

            if (userPreferences.contains("heightMeasurement")) {
                heightMeasurement = userPreferences.getString("heightMeasurement", "")
            }

            createUI()
        }

        genderRadioGroup.setOnCheckedChangeListener { group, checkedId ->

            when(checkedId) {

                R.id.settings_radio_male -> gender = "male"
                R.id.settings_radio_female -> gender = "female"
            }
        }

        weightRadioGroup.setOnCheckedChangeListener { group, checkedId ->

            when(checkedId) {

                R.id.settings_weight_imperial -> weightMeasurement = "imperial"
                R.id.settings_weight_metric -> weightMeasurement = "metric"
                R.id.settings_weight_stone -> weightMeasurement = "stone"
            }
        }

        heightRadioGroup.setOnCheckedChangeListener { group, checkedId ->

            when(checkedId) {

                R.id.settings_height_imperial -> {
                    heightMeasurement = "imperial"
                }

                R.id.settings_height_metric -> {
                    heightMeasurement = "metric"
                }
            }
        }

        saveButton.setOnClickListener {

            if (birthDate == Date()) {

                val birthDateAlert = AlertDialog.Builder(requireContext())
                birthDateAlert.setTitle("Birthday Error").setMessage("Please choose a birthday.").setPositiveButton("OK") {

                        dialog, which ->  dialog.cancel()
                }

                birthDateAlert.show()

            } else {

                //val currentUser = auth.currentUser

                if (currentUser == null) {

                    val createUserDialog = AlertDialog.Builder(requireContext())

                    createUserDialog.setTitle("Create Account").setMessage("Would you like to create an account to " +
                            "store your settings and macros?").setPositiveButton("OK",
                        DialogInterface.OnClickListener { dialog, which ->
                            val signUpIntent = Intent(this, SignUpActivity::class.java)

                            signUpIntent.putExtra("gender", gender)
                            signUpIntent.putExtra("weightMeasurement", weightMeasurement)
                            signUpIntent.putExtra("heightMeasurement", heightMeasurement)
                            signUpIntent.putExtra("birthDate", birthDate.time)

                            if (userPreferences.contains("dailyActivity")) {
                                signUpIntent.putExtra("dailyActivity", userPreferences.getString("dailyActivity", ""))
                            } else {
                                signUpIntent.putExtra("dailyActivity", "veryLight")
                            }

                            if (userPreferences.contains("feet")) {

                                signUpIntent.putExtra("feet", userPreferences.getInt("feet", 0))
                            } else {

                                signUpIntent.putExtra("feet", 5)
                            }

                            if (userPreferences.contains("inches")) {

                                signUpIntent.putExtra("inches", userPreferences.getString("inches", "").toDouble())
                            } else {
                                signUpIntent.putExtra("inches", 5)
                            }

                            if (userPreferences.contains("cm")) {

                                signUpIntent.putExtra("cm", userPreferences.getString("cm", "").toDouble())
                            } else {

                                signUpIntent.putExtra("cm", 165)
                            }

                            if (userPreferences.contains("physicalActivityLifestyle")) {
                                signUpIntent.putExtra("physicalActivityLifestyle", userPreferences.getString("physicalActivityLifestyle", ""))
                            } else {
                                signUpIntent.putExtra("physicalActivityLifestyle", "sedentaryAdult")
                            }

                            if (userPreferences.contains("goal")) {
                                signUpIntent.putExtra("goal", userPreferences.getString("goal", ""))
                            } else {
                                signUpIntent.putExtra("goal", "maintain")
                            }

                            if (userPreferences.contains("pounds") && userPreferences.contains("kg") && userPreferences.contains("stone")) {
                                signUpIntent.putExtra("pounds", userPreferences.getString("pounds", "").toDouble())
                                signUpIntent.putExtra("kg", userPreferences.getString("kg", "").toDouble())
                                signUpIntent.putExtra("stone", userPreferences.getString("stone", "").toDouble())
                            } else {
                                signUpIntent.putExtra("pounds", 0.0)
                                signUpIntent.putExtra("kg", 0.0)
                                signUpIntent.putExtra("stone", 0.0)
                            }

                            if (userPreferences.contains("dietFatPercent") && userPreferences.contains("calories") && userPreferences.contains("carbs") &&
                                userPreferences.contains("fat") && userPreferences.contains("protein")) {

                                signUpIntent.putExtra("dietFatPercent", userPreferences.getString("dietFatPercent", "").toDouble())
                                signUpIntent.putExtra("calories", userPreferences.getInt("calories", 0))
                                signUpIntent.putExtra("carbs", userPreferences.getInt("carbs", 0))
                                signUpIntent.putExtra("fat", userPreferences.getInt("fat", 0))
                                signUpIntent.putExtra("protein", userPreferences.getInt("protein", 0))
                            } else {

                                signUpIntent.putExtra("dietFatPercent", 0.0)
                                signUpIntent.putExtra("calories", 0)
                                signUpIntent.putExtra("carbs", 0)
                                signUpIntent.putExtra("fat", 0)
                                signUpIntent.putExtra("protein", 0)
                            }

                            if (userPreferences.contains("currentCaloriesTotal") && userPreferences.contains("currentCarbsTotal") && userPreferences.contains("currentFatTotal")
                                && userPreferences.contains("currentProteinTotal")) {

                                signUpIntent.putExtra("currentCalories", userPreferences.getString("currentCaloriesTotal", "").toDouble())
                                signUpIntent.putExtra("currentCarbs", userPreferences.getString("currentCarbsTotal", "").toDouble())
                                signUpIntent.putExtra("currentFat", userPreferences.getString("currentFatTotal", "").toDouble())
                                signUpIntent.putExtra("currentProtein", userPreferences.getString("currentProteinTotal", "").toDouble())
                            } else {

                                signUpIntent.putExtra("currentCalories", 0.0)
                                signUpIntent.putExtra("currentCarbs", 0.0)
                                signUpIntent.putExtra("currentFat", 0.0)
                                signUpIntent.putExtra("currentProtein", 0.0)
                            }

                            if (userPreferences.contains("dailyMeals")) {

                                val type = object : TypeToken<Pair<String, Any>>() {}.type
                                val dailyMealsString = userPreferences.getString("dailyMeals", "")
                                val dailyMeals: List<HashMap<String, Any>> = gson.fromJson(dailyMealsString, object : TypeToken<List<HashMap<String, Any>>>() {}.type)

                                signUpIntent.putExtra("dailyMeals", dailyMeals.toString())
                            } else {

                                val dailyMeals = listOf<HashMap<String, Any>>()
                                signUpIntent.putExtra("dailyMeals", dailyMeals.toString())
                            }

                            startActivity(signUpIntent)

                            finish()
                        }).setNegativeButton("No Thanks", { dialog, which ->

                        val userEditor = userPreferences.edit()
                        userEditor.putString("gender", gender)
                        userEditor.putString("weightMeasurement", weightMeasurement)
                        userEditor.putString("heightMeasurement", heightMeasurement)
                        userEditor.putLong("birthDate", birthDate.time)
                        userEditor.apply()

                        dialog.dismiss()
                        finish()
                    })

                    createUserDialog.show()
                } else {

                    val userId = auth.currentUser!!.uid
                    val users = firestore.collection("users").document(userId)
                    val userData = hashMapOf("gender" to gender, "weightMeasurement" to weightMeasurement, "heightMeasurement" to heightMeasurement, "birthDate" to birthDate)

                    users.update(userData as Map<String, Any>).addOnSuccessListener {

                        Toast.makeText(requireContext(), "Data updated successfully.", Toast.LENGTH_SHORT).show()
                        finish()
                    }.addOnFailureListener {

                        Toast.makeText(requireContext(), "Could not update. Please try again.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        val dateFormat = SimpleDateFormat("MM/dd/yyyy")

        if (dateFormat.format(birthDate) != dateFormat.format(Date())) {

            birthDateTextView.text = dateFormat.format(birthDate)
        } else {

            birthDateTextView.text = getString(R.string.set_birth_date)
        }

        when(gender) {
            "male" -> genderRadioGroup.check(R.id.settings_radio_male)
            "female" -> genderRadioGroup.check(R.id.settings_radio_female)
            else -> genderRadioGroup.check(R.id.settings_radio_female)
        }

        when(weightMeasurement) {

            "imperial" -> weightRadioGroup.check(R.id.settings_weight_imperial)
            "metric" -> weightRadioGroup.check(R.id.settings_weight_metric)
            "stone" -> weightRadioGroup.check(R.id.settings_weight_stone)
            else -> weightRadioGroup.check(R.id.settings_weight_metric)
        }

        when(heightMeasurement) {
            "imperial" -> heightRadioGroup.check(R.id.settings_height_imperial)
            "metric" -> heightRadioGroup.check(R.id.settings_height_metric)
            else -> heightRadioGroup.check(R.id.settings_height_metric)
        }

        /*if (userPreferences.getString("heightMeasurement", "") == "imperial") {

            heightRadioGroup.check(R.id.heightImperial)
        } else if (userPreferences.getString("heightMeasurement", "") == "metric") {

            heightRadioGroup.check(R.id.heightMetric)
        } else {

            heightRadioGroup.check(R.id.heightImperial)
        }*/

        binding.settingsBirthDate.setOnClickListener {

            val currentCalendar = Calendar.getInstance()
            val birthdayPicker = DatePickerDialog(requireContext(), { datePicker, year, month, date ->

                val birthCalendar = Calendar.getInstance()
                val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
                birthCalendar.set(year, month, date)
                val enteredBirthDate = dateFormat.format(birthCalendar.time)

                birthDate = birthCalendar.time
                birthDateTextView.text = enteredBirthDate

            }, currentCalendar.get(Calendar.YEAR),
                currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.DATE))

            birthdayPicker.show()
        }*/

        return binding.root
    }
}