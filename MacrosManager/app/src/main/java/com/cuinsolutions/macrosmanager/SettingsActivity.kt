package com.cuinsolutions.macrosmanager

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.*
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class SettingsActivity : AppCompatActivity() {

    private var showAds = true
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var birthDate = Date()
    private var gender = ""
    private var weightMeasurement = ""
    private var heightMeasurement = ""
    private var gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
    }

    override fun onResume() {
        super.onResume()

        val currentUser = auth.currentUser

        val regexs = Regexs()

        val settingsConstraintLayout: ConstraintLayout = findViewById<ConstraintLayout>(R.id.settingsConstraintLayout)
        val genderRadioGroup: RadioGroup = findViewById<RadioGroup>(R.id.genderRadioGroup)
        val heightRadioGroup: RadioGroup = findViewById<RadioGroup>(R.id.heightMeasurementRadioGroup)
        val weightRadioGroup: RadioGroup = findViewById<RadioGroup>(R.id.weightMeasurementRadioGroup)
        val saveButton = findViewById<Button>(R.id.saveSettingsButton)

        saveButton.text = getString(R.string.save)

        val decimalFormat = DecimalFormat("#.##")
        decimalFormat.roundingMode = RoundingMode.FLOOR


        val userPreferences = this.getSharedPreferences("userPreferences", 0)

        if (currentUser != null) {
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

                Toast.makeText(this, "There was an issue retrieving your data. Defaults have been used.", Toast.LENGTH_SHORT).show()
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
                gender = userPreferences.getString("gender", "")
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

                R.id.maleButton -> gender = "male"
                R.id.femaleButton -> gender = "female"
            }
        }

        weightRadioGroup.setOnCheckedChangeListener { group, checkedId ->

            when(checkedId) {

                R.id.weightImperial -> weightMeasurement = "imperial"
                R.id.weightMetric -> weightMeasurement = "metric"
                R.id.weightStone -> weightMeasurement = "stone"
            }
        }

        heightRadioGroup.setOnCheckedChangeListener { group, checkedId ->

            when(checkedId) {

                R.id.heightImperial -> {

                    heightMeasurement = "imperial"

                    /*if (cmEditText.visibility == View.VISIBLE) {

                        settingsConstraintLayout.removeView(cmEditText)
                        settingsConstraintLayout.removeView(cmTextView)
                        settingsConstraintLayout.removeView(feetTextView)
                        settingsConstraintLayout.removeView(feetEditText)
                        settingsConstraintLayout.removeView(inchesEditText)
                        settingsConstraintLayout.removeView(inchesTextView)
                    }

                    settingsConstraintLayout.addView(saveButton)
                    saveButtonSet.clone(settingsConstraintLayout)
                    saveButtonSet.constrainHeight(saveButton.id, ConstraintSet.WRAP_CONTENT)
                    saveButtonSet.constrainWidth(saveButton.id, ConstraintSet.WRAP_CONTENT)
                    saveButtonSet.connect(saveButton.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 16)
                    saveButtonSet.connect(saveButton.id, ConstraintSet.TOP, feetEditText.id, ConstraintSet.BOTTOM, 16)
                    saveButtonSet.applyTo(settingsConstraintLayout)*/
                }

                R.id.heightMetric -> {

                    heightMeasurement = "metric"

                    /*if (feetEditText.visibility == View.VISIBLE) {

                        settingsConstraintLayout.removeView(feetTextView)
                        settingsConstraintLayout.removeView(feetEditText)
                        settingsConstraintLayout.removeView(inchesEditText)
                        settingsConstraintLayout.removeView(inchesTextView)
                        settingsConstraintLayout.removeView(cmEditText)
                        settingsConstraintLayout.removeView(cmTextView)
                    }

                    settingsConstraintLayout.addView(saveButton)
                    saveButtonSet.clone(settingsConstraintLayout)
                    saveButtonSet.constrainHeight(saveButton.id, ConstraintSet.WRAP_CONTENT)
                    saveButtonSet.constrainWidth(saveButton.id, ConstraintSet.WRAP_CONTENT)
                    saveButtonSet.connect(saveButton.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 16)
                    saveButtonSet.connect(saveButton.id, ConstraintSet.TOP, cmEditText.id, ConstraintSet.BOTTOM, 16)
                    saveButtonSet.applyTo(settingsConstraintLayout)*/
                }
            }
        }

        saveButton.setOnClickListener {

            if (birthDate == Date()) {

                val birthDateAlert = AlertDialog.Builder(this)
                birthDateAlert.setTitle("Birthday Error").setMessage("Please choose a birthday.").setPositiveButton("OK") {

                    dialog, which ->  dialog.cancel()
                }

                birthDateAlert.show()

            } else {

                //val currentUser = auth.currentUser

                if (currentUser == null) {

                    val createUserDialog = AlertDialog.Builder(this)

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

                        Toast.makeText(this, "Data updated successfully.", Toast.LENGTH_SHORT).show()
                        finish()
                    }.addOnFailureListener {

                        Toast.makeText(this, "Could not update. Please try again.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }

    fun createUI() {

        val settingsConstraintLayout: ConstraintLayout = findViewById<ConstraintLayout>(R.id.settingsConstraintLayout)
        val genderRadioGroup: RadioGroup = findViewById<RadioGroup>(R.id.genderRadioGroup)
        val heightRadioGroup: RadioGroup = findViewById<RadioGroup>(R.id.heightMeasurementRadioGroup)
        val weightRadioGroup: RadioGroup = findViewById<RadioGroup>(R.id.weightMeasurementRadioGroup)
        val birthDateTextView: TextView = findViewById<TextView>(R.id.birthDateTextView)
        val settingsScrollView = findViewById<ScrollView>(R.id.settingsScrollView)
        val settingsAdView: AdView = findViewById<AdView>(R.id.settingsAdView)

        val feetEditText = EditText(this)
        val feetTextView = TextView(this)
        val inchesEditText = EditText(this)
        val inchesTextView = TextView(this)
        val cmEditText = EditText(this)
        val cmTextView = TextView(this)
        val saveButton = Button(this)

        val dateFormat = SimpleDateFormat("MM/dd/yyyy")

        if (dateFormat.format(birthDate) != dateFormat.format(Date())) {

            birthDateTextView.text = dateFormat.format(birthDate)
        } else {

            birthDateTextView.text = getString(R.string.set_birth_date)
        }

        when(gender) {

            "male" -> genderRadioGroup.check(R.id.maleButton)
            "female" -> genderRadioGroup.check(R.id.femaleButton)
            else -> genderRadioGroup.check(R.id.femaleButton)
        }

        if (showAds) {
            MobileAds.initialize(this, getString(R.string.admob_id))
            val adRequest = AdRequest.Builder().build()
            settingsAdView.loadAd(adRequest)
        } else {
            settingsAdView.visibility = View.GONE
            val removeAdSet = ConstraintSet()
            removeAdSet.clone(settingsConstraintLayout)
            removeAdSet.connect(settingsScrollView.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
            removeAdSet.applyTo(settingsConstraintLayout)
        }

        /*if (userPreferences.getString("gender", "") == "male") {

            genderRadioGroup.check(R.id.maleButton)
        } else if (userPreferences.getString("gender", "") == "female") {

            genderRadioGroup.check(R.id.femaleButton)
        } else {

            genderRadioGroup.check(R.id.femaleButton)
        }*/

        when(weightMeasurement) {

            "imperial" -> weightRadioGroup.check(R.id.weightImperial)
            "metric" -> weightRadioGroup.check(R.id.weightMetric)
            "stone" -> weightRadioGroup.check(R.id.weightStone)
            else -> weightRadioGroup.check(R.id.weightMetric)
        }

        /*if (userPreferences.getString("weightMeasurement", "") == "imperial") {

            weightRadioGroup.check(R.id.weightImperial)
        } else if (userPreferences.getString("weightMeasurement", "") == "metric") {

            weightRadioGroup.check(R.id.weightMetric)
        } else if (userPreferences.getString("weightMeasurement", "") == "stone") {

            weightRadioGroup.check(R.id.weightStone)
        } else {

            weightRadioGroup.check(R.id.weightImperial)
        }*/

        when(heightMeasurement) {

            "imperial" -> heightRadioGroup.check(R.id.heightImperial)
            "metric" -> heightRadioGroup.check(R.id.heightMetric)
            else -> heightRadioGroup.check(R.id.heightMetric)
        }

        /*if (userPreferences.getString("heightMeasurement", "") == "imperial") {

            heightRadioGroup.check(R.id.heightImperial)
        } else if (userPreferences.getString("heightMeasurement", "") == "metric") {

            heightRadioGroup.check(R.id.heightMetric)
        } else {

            heightRadioGroup.check(R.id.heightImperial)
        }*/

        birthDateTextView.setOnClickListener {

            val currentCalendar = Calendar.getInstance()
            val birthdayPicker = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { datePicker, year, month, date ->

                val birthCalendar = Calendar.getInstance()
                val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
                birthCalendar.set(year, month, date)
                val enteredBirthDate = dateFormat.format(birthCalendar.time)

                birthDate = birthCalendar.time
                birthDateTextView.text = enteredBirthDate

            }, currentCalendar.get(Calendar.YEAR),
                    currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.DATE))

            birthdayPicker.show()
        }
    }
}
