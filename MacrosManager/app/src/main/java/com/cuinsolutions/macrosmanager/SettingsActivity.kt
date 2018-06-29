package com.cuinsolutions.macrosmanager

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.util.Log
import android.view.Gravity
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
import org.json.JSONArray
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.roundToInt

class SettingsActivity : AppCompatActivity() {

    private var showAds = true
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var birthDate = ""
    private var gender = ""
    private var weightMeasurement = ""
    private var heightMeasurement = ""
    private var feet: Int = 0
    private var inches = 0.0
    private var cm = 0.0
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
        val heightTextView: TextView = findViewById<TextView>(R.id.heightTextView)
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

        saveButton.text = getString(R.string.save)
        saveButton.id = View.generateViewId()

        val decimalFormat = DecimalFormat("#.##")
        decimalFormat.roundingMode = RoundingMode.FLOOR

        val saveButtonSet = ConstraintSet()
        /*saveButtonSet.clone(settingsConstraintLayout)
        saveButtonSet.constrainHeight(saveButton.id, ConstraintSet.WRAP_CONTENT)
        saveButtonSet.constrainWidth(saveButton.id, ConstraintSet.WRAP_CONTENT)
        saveButtonSet.connect(saveButton.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 16)
        saveButtonSet.applyTo(settingsConstraintLayout)*/

        val userPreferences = this.getSharedPreferences("userPreferences", 0)

        if (currentUser != null) {
            val db = firestore.collection("users").document(currentUser.uid)
            db.get().addOnSuccessListener {
                birthDate = it.getString("birthDate")!!
                gender = it.getString("gender")!!
                weightMeasurement = it.getString("weightMeasurement")!!
                heightMeasurement = it.getString("heightMeasurement")!!
                feet = it.getLong("feet")!!.toInt()
                inches = it.getDouble("inches")!!
                cm = it.getDouble("cm")!!
                showAds = it.getBoolean("showAds")!!

                createUI()
            }.addOnFailureListener {

                Toast.makeText(this, "There was an issue retrieving your data. Defaults have been used.", Toast.LENGTH_SHORT).show()
                birthDate = ""
                gender = "female"
                weightMeasurement = "metric"
                heightMeasurement = "metric"
                cm = 165.0
                val feetConversion = cm / 30.48
                val feetRemainder = feetConversion % 1
                feet = (feetConversion - feetRemainder).toInt()
                inches = (cm / 2.54) - (feet * 12) + feetRemainder
            }
        } else {

            if (userPreferences.contains("birthDate")) {
                birthDate = userPreferences.getString("birthDate", "")
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

            if (userPreferences.contains("cm")) {
                cm = userPreferences.getString("cm", "").toDouble()
            }

            if (userPreferences.contains("feet")) {
                feet = userPreferences.getInt("feet", 0)
            }

            if (userPreferences.contains("inches")) {
                inches = userPreferences.getString("inches", "").toDouble()
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

            if (saveButton.parent != null) {
                settingsConstraintLayout.removeView(saveButton)
            }

            when(checkedId) {

                R.id.heightImperial -> {

                    heightMeasurement = "imperial"

                    if (cmEditText.visibility == View.VISIBLE) {

                        settingsConstraintLayout.removeView(cmEditText)
                        settingsConstraintLayout.removeView(cmTextView)
                        settingsConstraintLayout.removeView(feetTextView)
                        settingsConstraintLayout.removeView(feetEditText)
                        settingsConstraintLayout.removeView(inchesEditText)
                        settingsConstraintLayout.removeView(inchesTextView)
                    }

                    feetEditText.id = View.generateViewId()
                    feetEditText.inputType = (InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL)
                    inchesEditText.id = View.generateViewId()
                    inchesEditText.inputType = (InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL)
                    feetTextView.id = View.generateViewId()
                    inchesTextView.id = View.generateViewId()

                    feetTextView.text = getString(R.string.feet)
                    feetTextView.gravity = Gravity.CENTER
                    inchesTextView.text = getString(R.string.inches)
                    inchesTextView.gravity = Gravity.CENTER

                    settingsConstraintLayout.addView(feetEditText)
                    settingsConstraintLayout.addView(feetTextView)
                    settingsConstraintLayout.addView(inchesEditText)
                    settingsConstraintLayout.addView(inchesTextView)

                    val feetEditTextSet = ConstraintSet()
                    feetEditTextSet.clone(settingsConstraintLayout)
                    feetEditTextSet.constrainHeight(feetEditText.id, ConstraintSet.WRAP_CONTENT)
                    feetEditTextSet.constrainWidth(feetEditText.id, ConstraintSet.WRAP_CONTENT)
                    feetEditTextSet.setMargin(ConstraintSet.PARENT_ID, ConstraintSet.START, 16)
                    feetEditTextSet.connect(feetEditText.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 16)
                    feetEditTextSet.connect(feetEditText.id, ConstraintSet.TOP, heightTextView.id, ConstraintSet.BOTTOM, 16)
                    feetEditTextSet.connect(feetEditText.id, ConstraintSet.END, feetTextView.id, ConstraintSet.START)
                    feetEditTextSet.applyTo(settingsConstraintLayout)

                    val feetTextViewSet = ConstraintSet()
                    feetTextViewSet.clone(settingsConstraintLayout)
                    feetTextViewSet.constrainWidth(feetTextView.id, ConstraintSet.WRAP_CONTENT)
                    feetTextViewSet.constrainHeight(feetTextView.id, ConstraintSet.WRAP_CONTENT)
                    feetTextViewSet.connect(feetTextView.id, ConstraintSet.TOP, heightTextView.id, ConstraintSet.BOTTOM, 16)
                    feetTextViewSet.connect(feetTextView.id, ConstraintSet.START, feetEditText.id, ConstraintSet.END)
                    feetTextViewSet.connect(feetTextView.id, ConstraintSet.END, inchesEditText.id, ConstraintSet.START, 8)
                    feetTextViewSet.applyTo(settingsConstraintLayout)

                    val inchesEditTextSet = ConstraintSet()
                    inchesEditTextSet.clone(settingsConstraintLayout)
                    inchesEditTextSet.constrainWidth(inchesEditText.id, ConstraintSet.WRAP_CONTENT)
                    inchesEditTextSet.constrainHeight(inchesEditText.id, ConstraintSet.WRAP_CONTENT)
                    inchesEditTextSet.connect(inchesEditText.id, ConstraintSet.TOP, heightTextView.id, ConstraintSet.BOTTOM, 16)
                    inchesEditTextSet.connect(inchesEditText.id, ConstraintSet.START, feetTextView.id, ConstraintSet.END, 8)
                    inchesEditTextSet.connect(inchesEditText.id, ConstraintSet.END, inchesTextView.id, ConstraintSet.START)
                    inchesEditTextSet.applyTo(settingsConstraintLayout)

                    val inchesTextViewSet = ConstraintSet()
                    inchesTextViewSet.clone(settingsConstraintLayout)
                    inchesTextViewSet.constrainWidth(inchesTextView.id, ConstraintSet.WRAP_CONTENT)
                    inchesTextViewSet.constrainHeight(inchesTextView.id, ConstraintSet.WRAP_CONTENT)
                    inchesTextViewSet.connect(inchesTextView.id, ConstraintSet.TOP, heightTextView.id, ConstraintSet.BOTTOM, 16)
                    inchesTextViewSet.connect(inchesTextView.id, ConstraintSet.START, inchesEditText.id, ConstraintSet.END)
                    inchesTextViewSet.applyTo(settingsConstraintLayout)

                    if (feet != 0 && inches != 0.0) {

                        feetEditText.setText(feet.toString())
                        inchesEditText.setText(inches.toString())
                    }

                    settingsConstraintLayout.addView(saveButton)
                    saveButtonSet.clone(settingsConstraintLayout)
                    saveButtonSet.constrainHeight(saveButton.id, ConstraintSet.WRAP_CONTENT)
                    saveButtonSet.constrainWidth(saveButton.id, ConstraintSet.WRAP_CONTENT)
                    saveButtonSet.connect(saveButton.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 16)
                    saveButtonSet.connect(saveButton.id, ConstraintSet.TOP, feetEditText.id, ConstraintSet.BOTTOM, 16)
                    saveButtonSet.applyTo(settingsConstraintLayout)
                }

                R.id.heightMetric -> {

                    heightMeasurement = "metric"

                    if (feetEditText.visibility == View.VISIBLE) {

                        settingsConstraintLayout.removeView(feetTextView)
                        settingsConstraintLayout.removeView(feetEditText)
                        settingsConstraintLayout.removeView(inchesEditText)
                        settingsConstraintLayout.removeView(inchesTextView)
                        settingsConstraintLayout.removeView(cmEditText)
                        settingsConstraintLayout.removeView(cmTextView)
                    }

                    cmEditText.id = View.generateViewId()
                    cmEditText.inputType = (InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL)
                    cmTextView.id = View.generateViewId()

                    cmTextView.text = getString(R.string.centi)

                    settingsConstraintLayout.addView(cmEditText)
                    settingsConstraintLayout.addView(cmTextView)

                    val cmEditTextSet = ConstraintSet()
                    cmEditTextSet.clone(settingsConstraintLayout)
                    cmEditTextSet.constrainHeight(cmEditText.id, ConstraintSet.WRAP_CONTENT)
                    cmEditTextSet.constrainWidth(cmEditText.id, ConstraintSet.WRAP_CONTENT)
                    cmEditTextSet.setMargin(ConstraintSet.PARENT_ID, ConstraintSet.START, R.dimen.height_text_margin)
                    cmEditTextSet.connect(cmEditText.id, ConstraintSet.TOP, heightTextView.id, ConstraintSet.BOTTOM, 16)
                    cmEditTextSet.connect(cmEditText.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 16)
                    cmEditTextSet.connect(cmEditText.id, ConstraintSet.END, cmTextView.id, ConstraintSet.START)
                    cmEditTextSet.applyTo(settingsConstraintLayout)

                    val cmTextViewSet = ConstraintSet()
                    cmTextViewSet.clone(settingsConstraintLayout)
                    cmTextViewSet.constrainWidth(cmTextView.id, ConstraintSet.WRAP_CONTENT)
                    cmTextViewSet.constrainHeight(cmTextView.id, ConstraintSet.WRAP_CONTENT)
                    cmTextViewSet.connect(cmTextView.id, ConstraintSet.START, cmEditText.id, ConstraintSet.END)
                    cmTextViewSet.connect(cmTextView.id, ConstraintSet.TOP, heightTextView.id, ConstraintSet.BOTTOM, 16)
                    cmTextViewSet.applyTo(settingsConstraintLayout)

                    if (cm != 0.0) {

                        cmEditText.setText(cm.toString())
                    }

                    settingsConstraintLayout.addView(saveButton)
                    saveButtonSet.clone(settingsConstraintLayout)
                    saveButtonSet.constrainHeight(saveButton.id, ConstraintSet.WRAP_CONTENT)
                    saveButtonSet.constrainWidth(saveButton.id, ConstraintSet.WRAP_CONTENT)
                    saveButtonSet.connect(saveButton.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 16)
                    saveButtonSet.connect(saveButton.id, ConstraintSet.TOP, cmEditText.id, ConstraintSet.BOTTOM, 16)
                    saveButtonSet.applyTo(settingsConstraintLayout)
                }
            }
        }

        saveButton.setOnClickListener {

            if (birthDate == "") {

                val birthDateAlert = AlertDialog.Builder(this)
                birthDateAlert.setTitle("Birthday Error").setMessage("Please choose a birthday.").setPositiveButton("OK") {

                    dialog, which ->  dialog.cancel()
                }

                birthDateAlert.show()

            } else if (heightRadioGroup.checkedRadioButtonId == R.id.heightImperial) {

                if (feetEditText.text.toString() == "" || inchesEditText.text.toString() == "" || (inchesEditText.text.toString().toDouble()) >= 13
                        || (inchesEditText.text.toString().toDouble()) < 0 || !regexs.validNumber(feetEditText.text.toString()) ||
                        !regexs.validNumber(inchesEditText.text.toString())) {

                    val heightAlert = AlertDialog.Builder(this)
                    heightAlert.setTitle("Height Error").setMessage("Please enter a valid number for both entries.").setPositiveButton("OK") {

                        dialog, which ->  dialog.cancel()
                    }

                    heightAlert.show()
                } else {

                    val totalInches = ((Integer.valueOf(feetEditText.text.toString())) * 12) + (inchesEditText.text.toString().toDouble())
                    val cmCalced = totalInches * 2.54

                    feet = feetEditText.text.toString().toInt()
                    inches = inchesEditText.text.toString().toDouble()//editor.putString("inches", decimalFormat.format(inchesEditText.text.toString().toDouble()).toString())
                    cm = cmCalced//editor.putString("cm", decimalFormat.format(cm).toString())

                    //finish()
                }

            } else if (heightRadioGroup.checkedRadioButtonId == R.id.heightMetric) {

                if (cmEditText.text.toString() == "" || !regexs.validNumber(cmEditText.text.toString())) {

                    val heightAlert = AlertDialog.Builder(this)
                    heightAlert.setTitle("Height Error").setMessage("Please enter a valid number for entry.").setPositiveButton("OK") {

                        dialog, which ->  dialog.cancel()
                    }

                    heightAlert.show()
                } else {

                    val feetConversion = (cmEditText.text.toString().toFloat()) / 30.48
                    val feetRemainder = feetConversion % 1
                    val feetCalced = feetConversion - feetRemainder
                    val inchesCalced = ((cmEditText.text.toString().toFloat()) / 2.54) - (feetCalced * 12) + feetRemainder

                    cm = cmEditText.text.toString().toDouble()
                    feet = feetCalced.toInt()//editor.putString("feet", decimalFormat.format(feet).toString())
                    inches = inchesCalced

                    //finish()
                }
            }

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
                            signUpIntent.putExtra("feet", feet)
                            signUpIntent.putExtra("inches", inches)
                            signUpIntent.putExtra("cm", cm)
                            signUpIntent.putExtra("birthDate", birthDate)

                            if (userPreferences.contains("dailyActivity")) {
                                signUpIntent.putExtra("dailyActivity", userPreferences.getString("dailyActivity", ""))
                            } else {
                                signUpIntent.putExtra("dailyActivity", "veryLight")
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

                            if (userPreferences.contains("currentCalories") && userPreferences.contains("currentCarbs") && userPreferences.contains("currentFat")
                            && userPreferences.contains("currentProtein")) {

                                signUpIntent.putExtra("currentCalories", userPreferences.getString("currentCalories", "").toDouble())
                                signUpIntent.putExtra("currentCarbs", userPreferences.getString("currentCarbs", "").toDouble())
                                signUpIntent.putExtra("currentFat", userPreferences.getString("currentFat", "").toDouble())
                                signUpIntent.putExtra("currentProtein", userPreferences.getString("currentProtein", "").toDouble())
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
                    userEditor.putInt("feet", feet)
                    userEditor.putString("inches", inches.toString())
                    userEditor.putString("cm", cm.toString())
                    userEditor.putString("birthDate", birthDate)
                    userEditor.apply()

                    dialog.dismiss()
                    finish()
                })

                createUserDialog.show()
            } else {

                val userId = auth.currentUser!!.uid
                val users = firestore.collection("users").document(userId)
                val userData = hashMapOf("gender" to gender, "weightMeasurement" to weightMeasurement, "heightMeasurement" to heightMeasurement, "feet" to feet,
                        "inches" to inches, "cm" to cm, "birthDate" to birthDate)

                users.update(userData).addOnSuccessListener {

                    Toast.makeText(this, "Data updated successfully.", Toast.LENGTH_SHORT).show()
                    finish()
                }.addOnFailureListener {

                    Toast.makeText(this, "Could not update. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    fun createUI() {

        val settingsConstraintLayout: ConstraintLayout = findViewById<ConstraintLayout>(R.id.settingsConstraintLayout)
        val genderRadioGroup: RadioGroup = findViewById<RadioGroup>(R.id.genderRadioGroup)
        val heightRadioGroup: RadioGroup = findViewById<RadioGroup>(R.id.heightMeasurementRadioGroup)
        val weightRadioGroup: RadioGroup = findViewById<RadioGroup>(R.id.weightMeasurementRadioGroup)
        val heightTextView: TextView = findViewById<TextView>(R.id.heightTextView)
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

        if (birthDate != "") {

            birthDateTextView.text = birthDate
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

                birthDate = enteredBirthDate
                birthDateTextView.text = birthDate

            }, currentCalendar.get(Calendar.YEAR),
                    currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.DATE))

            birthdayPicker.show()
        }
    }
}
