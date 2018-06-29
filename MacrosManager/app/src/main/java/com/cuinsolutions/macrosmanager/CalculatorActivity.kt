package com.cuinsolutions.macrosmanager

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.joda.time.DateTime
import org.joda.time.Years
import org.joda.time.format.DateTimeFormat
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.roundToInt

class CalculatorActivity : AppCompatActivity() {

    private var showAds = true
    private lateinit var auth: FirebaseAuth
    private lateinit var fireStore: FirebaseFirestore
    private lateinit var weightMeasurement: String
    private lateinit var gender: String
    private lateinit var birthDate: String
    private var cm = 0.0
    private lateinit var dailyActivity: String
    private lateinit var physicalActivityLifestyle: String
    private lateinit var goal: String
    private var pounds = 0.0
    private var kg = 0.0
    private var stone = 0.0
    private var dietFatPercent = 0.0
    private var calories = 0
    private var carbs = 0
    private var fat = 0
    private var protein = 0
    lateinit var calculatorBottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)

        auth = FirebaseAuth.getInstance()
        fireStore = FirebaseFirestore.getInstance()

        val regexs = Regexs()


        calculatorBottomNav = findViewById<BottomNavigationView>(R.id.calculatorBottomNav)

        calculatorBottomNav.selectedItemId = R.id.calculator
        calculatorBottomNav.setOnNavigationItemSelectedListener { item ->

            when(item.itemId) {

                R.id.home -> {

                    val homeIntent = Intent(this, DailyActivity::class.java)

                    startActivity(homeIntent)
                }

                R.id.calculator -> {

                }
            }

            true
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
                weightMeasurement = it.getString("weightMeasurement")!!
                birthDate = it.getString("birthDate")!!
                gender = it.getString("gender")!!
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
                showAds = it.getBoolean("showAds")!!
                loadUI(weightMeasurement, birthDate, gender, cm, userPreferences)
            }.addOnFailureListener {

                Toast.makeText(this, "There was an issue getting your data. Please try again later.", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else if (userPreferences.contains("weightMeasurement") && userPreferences.contains("birthDate") && userPreferences.contains("gender") &&
                userPreferences.contains("heightMeasurement") && userPreferences.contains("cm")){

            weightMeasurement = userPreferences.getString("weightMeasurement", "")
            birthDate = userPreferences.getString("birthDate", "")
            gender = userPreferences.getString("gender", "")
            cm = userPreferences.getString("cm", "").toDouble()
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

        val calculatorConstraintLayout = findViewById<ConstraintLayout>(R.id.calculatorConstraintLayout)
        val calculatorAdView = findViewById<AdView>(R.id.calculatorAdView)

        if (showAds) {
            val adRequest = AdRequest.Builder().build()
            calculatorAdView.loadAd(adRequest)
        } else {
            calculatorAdView.visibility = View.GONE
            val removeAdViewSet = ConstraintSet()
            removeAdViewSet.clone(calculatorConstraintLayout)
            removeAdViewSet.connect(calculatorBottomNav.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
            removeAdViewSet.applyTo(calculatorConstraintLayout)
        }
    }

    private fun calculate(fatPercentageEditText: EditText, poundsEditText: EditText, kilogramsEditText: EditText, stoneEditText: EditText, decimalFormat: DecimalFormat,
                          weightMeasurement: String, birthDate: String, gender: String, cm: Double) {

        val userPreferences = this.getSharedPreferences("userPreferences", 0)

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

        var bmr = 0.0
        val tdee: Double
        val calories: Double
        val protein: Double
        val proteinCalories: Double
        val fat: Double
        val fatCalories: Double
        val carbs: Double
        val carbsCalories: Double
        val dateFormat = DateTimeFormat.forPattern("dd/MM/yyyy")//SimpleDateFormat()
        val age = Years.yearsBetween(dateFormat.parseDateTime(birthDate) as DateTime, DateTime.now())

        Log.d("Age", age.toString())
        when (gender) {

            "male" -> {

                bmr = (10 * kg) + (6.25 * cm) - ((5 * age.years) + 5)
            }

            "female" -> {

                bmr = (10 * kg) + (6.25 * cm) - ((5 * age.years) - 161)
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
                Toast.makeText(this, "Your data has been successfully updated.", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "There was an issue updating the data. Please try again later.", Toast.LENGTH_SHORT).show()
            }
        } else {

            val createUserDialog = AlertDialog.Builder(this)

            createUserDialog.setTitle("Create Account").setMessage("Would you like to create an account to " +
                    "store your settings and macros?").setPositiveButton("OK",
                    DialogInterface.OnClickListener { dialog, which ->
                        val signUpIntent = Intent(this, SignUpActivity::class.java)

                        signUpIntent.putExtra("gender", userPreferences.getString("gender", ""))
                        signUpIntent.putExtra("weightMeasurement", weightMeasurement)
                        signUpIntent.putExtra("heightMeasurement", userPreferences.getString("heightMeasurement", ""))
                        signUpIntent.putExtra("feet", userPreferences.getInt("feet", 0))
                        signUpIntent.putExtra("inches", userPreferences.getString("inches", ""))
                        signUpIntent.putExtra("cm", cm)
                        signUpIntent.putExtra("birthDate", birthDate)
                        signUpIntent.putExtra("dailyActivity", dailyActivity)
                        signUpIntent.putExtra("physicalActivityLifestyle", physicalActivityLifestyle)
                        signUpIntent.putExtra("goal", goal)
                        signUpIntent.putExtra("pounds", pounds)
                        signUpIntent.putExtra("kg", kg)
                        signUpIntent.putExtra("stone", stone)
                        signUpIntent.putExtra("dietFatPercent", dietFatPercent)
                        signUpIntent.putExtra("calories", calories.roundToInt())
                        signUpIntent.putExtra("carbs", carbs.roundToInt())
                        signUpIntent.putExtra("fat", fat.roundToInt())
                        signUpIntent.putExtra("protein", protein.roundToInt())
                        startActivity(signUpIntent)

                        finish()
                    }).setNegativeButton("No Thanks", { dialog, which ->

                val userEditor = userPreferences.edit()

                userEditor.putString("dailyActivity", dailyActivity)
                userEditor.putString("physicalActivityLifestyle", physicalActivityLifestyle)
                userEditor.putString("goal", goal)
                userEditor.putString("pounds", pounds.toString())
                userEditor.putString("kg", kg.toString())
                userEditor.putString("stone", stone.toString())
                userEditor.putString("dietFatPercent", dietFatPercent.toString())
                userEditor.putInt("calories", calories.roundToInt())
                userEditor.putInt("carbs", carbs.roundToInt())
                userEditor.putInt("fat", fat.roundToInt())
                userEditor.putInt("protein", protein.roundToInt())
                userEditor.apply()

                dialog.dismiss()
                finish()
            })

            createUserDialog.show()
        }
    }

    fun loadUI(weightMeasurement: String, birthDate: String, gender: String, cm: Double, userPreferences: SharedPreferences) {

        val regexs = Regexs()
        val calculatorConstraintLayout = findViewById<ConstraintLayout>(R.id.calculatorConstraintLayout)
        val weightTextView = findViewById<TextView>(R.id.weightTextView)
        var previousView = View(this)
        val poundsEditText = EditText(this)
        val kilogramsEditText = EditText(this)
        val stoneEditText = EditText(this)
        val stonePoundsEditText = EditText(this)

        val decimalFormat = DecimalFormat("#.##")
        decimalFormat.roundingMode = RoundingMode.FLOOR

        if (weightMeasurement == "imperial") {

            //val poundsEditText = EditText(this)
            poundsEditText.id = View.generateViewId()
            calculatorConstraintLayout.addView(poundsEditText)
            poundsEditText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            val poundsTextView = TextView(this)
            poundsTextView.id = View.generateViewId()
            calculatorConstraintLayout.addView(poundsTextView)

            poundsTextView.text = getString(R.string.pounds)

            val weightImperialPoundEditTextSet = ConstraintSet()

            weightImperialPoundEditTextSet.clone(calculatorConstraintLayout)
            weightImperialPoundEditTextSet.constrainHeight(poundsEditText.id, ConstraintSet.WRAP_CONTENT)
            weightImperialPoundEditTextSet.constrainWidth(poundsEditText.id, ConstraintSet.WRAP_CONTENT)
            weightImperialPoundEditTextSet.connect(poundsEditText.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 16)
            weightImperialPoundEditTextSet.connect(poundsEditText.id, ConstraintSet.TOP, weightTextView.id, ConstraintSet.BOTTOM, 16)
            weightImperialPoundEditTextSet.applyTo(calculatorConstraintLayout)

            val weightImperialPoundTextView = ConstraintSet()

            weightImperialPoundTextView.clone(calculatorConstraintLayout)
            weightImperialPoundTextView.constrainWidth(poundsTextView.id, ConstraintSet.WRAP_CONTENT)
            weightImperialPoundTextView.constrainHeight(poundsTextView.id, ConstraintSet.WRAP_CONTENT)
            weightImperialPoundTextView.connect(poundsTextView.id, ConstraintSet.START, poundsEditText.id, ConstraintSet.END, 16)
            weightImperialPoundTextView.connect(poundsTextView.id, ConstraintSet.TOP, weightTextView.id, ConstraintSet.BOTTOM, 16)
            weightImperialPoundTextView.applyTo(calculatorConstraintLayout)

            previousView = poundsEditText
        }

        if (weightMeasurement == "metric") {

            //val kilogramsEditText = EditText(this)
            kilogramsEditText.id = View.generateViewId()
            calculatorConstraintLayout.addView(kilogramsEditText)
            kilogramsEditText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            val kilogramsTextView = TextView(this)
            kilogramsTextView.id = View.generateViewId()
            calculatorConstraintLayout.addView(kilogramsTextView)

            kilogramsTextView.text = getString(R.string.kilogram)

            val weightMetricSet = ConstraintSet()

            weightMetricSet.clone(calculatorConstraintLayout)
            weightMetricSet.constrainHeight(kilogramsEditText.id, ConstraintSet.WRAP_CONTENT)
            weightMetricSet.constrainWidth(kilogramsEditText.id, ConstraintSet.WRAP_CONTENT)
            weightMetricSet.constrainHeight(kilogramsTextView.id, ConstraintSet.WRAP_CONTENT)
            weightMetricSet.constrainWidth(kilogramsTextView.id, ConstraintSet.WRAP_CONTENT)
            weightMetricSet.connect(kilogramsEditText.id, ConstraintSet.TOP, weightTextView.id, ConstraintSet.BOTTOM, 16)
            weightMetricSet.connect(kilogramsEditText.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 16)
            weightMetricSet.connect(kilogramsTextView.id, ConstraintSet.START, kilogramsEditText.id, ConstraintSet.END, 16)
            weightMetricSet.connect(kilogramsTextView.id, ConstraintSet.TOP, weightTextView.id, ConstraintSet.BOTTOM, 16)
            weightMetricSet.applyTo(calculatorConstraintLayout)

            previousView = kilogramsEditText
        }

        if (weightMeasurement == "stone") {

            //val stoneEditText = EditText(this)
            stoneEditText.id = View.generateViewId()
            calculatorConstraintLayout.addView(stoneEditText)
            stoneEditText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            val stoneTextView = TextView(this)
            stoneTextView.id = View.generateViewId()
            calculatorConstraintLayout.addView(stoneTextView)
            //val poundsEditText = EditText(this)
            /*stonePoundsEditText.id = View.generateViewId()
            calculatorConstraintLayout.addView(stonePoundsEditText)
            val poundsTextView = TextView(this)
            poundsTextView.id = View.generateViewId()
            calculatorConstraintLayout.addView(poundsTextView)*/

            stoneTextView.text = getString(R.string.stone)
            //poundsTextView.text = "LBS"

            val weightStoneSet = ConstraintSet()

            weightStoneSet.clone(calculatorConstraintLayout)
            weightStoneSet.constrainWidth(stoneEditText.id, ConstraintSet.WRAP_CONTENT)
            weightStoneSet.constrainHeight(stoneEditText.id, ConstraintSet.WRAP_CONTENT)
            weightStoneSet.constrainWidth(stoneTextView.id, ConstraintSet.WRAP_CONTENT)
            weightStoneSet.constrainHeight(stoneTextView.id, ConstraintSet.WRAP_CONTENT)
            weightStoneSet.constrainWidth(stonePoundsEditText.id, ConstraintSet.WRAP_CONTENT)
            weightStoneSet.constrainHeight(stonePoundsEditText.id, ConstraintSet.WRAP_CONTENT)
            /*weightStoneSet.constrainWidth(poundsTextView.id, ConstraintSet.WRAP_CONTENT)
            weightStoneSet.constrainHeight(poundsTextView.id, ConstraintSet.WRAP_CONTENT)*/
            weightStoneSet.connect(stoneEditText.id, ConstraintSet.TOP, weightTextView.id, ConstraintSet.BOTTOM, 16)
            weightStoneSet.connect(stoneEditText.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 16)
            weightStoneSet.connect(stoneTextView.id, ConstraintSet.TOP, weightTextView.id, ConstraintSet.BOTTOM, 16)
            weightStoneSet.connect(stoneTextView.id, ConstraintSet.START, stoneEditText.id, ConstraintSet.END, 16)
            /*weightStoneSet.connect(stonePoundsEditText.id, ConstraintSet.TOP, weightTextView.id, ConstraintSet.BOTTOM, 16)
            weightStoneSet.connect(stonePoundsEditText.id, ConstraintSet.START, stoneTextView.id, ConstraintSet.END, 16)
            weightStoneSet.connect(poundsTextView.id, ConstraintSet.TOP, weightTextView.id, ConstraintSet.BOTTOM, 16)
            weightStoneSet.connect(poundsTextView.id, ConstraintSet.START, poundsEditText.id, ConstraintSet.END, 16)*/
            weightStoneSet.applyTo(calculatorConstraintLayout)

            previousView = stoneEditText
        }

        val dailyActivityTextView = TextView(this)
        dailyActivityTextView.id = View.generateViewId()
        dailyActivityTextView.text = getString(R.string.daily_activity_level)
        calculatorConstraintLayout.addView(dailyActivityTextView)

        val dailyActivityRadioGroup = RadioGroup(this)
        dailyActivityRadioGroup.id = View.generateViewId()
        dailyActivityRadioGroup.orientation = RadioGroup.VERTICAL
        calculatorConstraintLayout.addView(dailyActivityRadioGroup)

        val dailyActivityInfoButton = ImageButton(this)
        dailyActivityInfoButton.id = View.generateViewId()
        dailyActivityInfoButton.setImageResource(R.drawable.ic_info)
        calculatorConstraintLayout.addView(dailyActivityInfoButton)

        val dailyActivityVeryLightRadioButton = RadioButton(this)
        dailyActivityVeryLightRadioButton.id = View.generateViewId()
        dailyActivityVeryLightRadioButton.text = getString(R.string.very_light)
        dailyActivityRadioGroup.addView(dailyActivityVeryLightRadioButton)
        dailyActivityVeryLightRadioButton.isSelected = true

        val dailyActivityLightRadioButton = RadioButton(this)
        dailyActivityLightRadioButton.id = View.generateViewId()
        dailyActivityLightRadioButton.text = getString(R.string.light)
        dailyActivityRadioGroup.addView(dailyActivityLightRadioButton)

        val dailyActivityModerateRadioButton = RadioButton(this)
        dailyActivityModerateRadioButton.id = View.generateViewId()
        dailyActivityModerateRadioButton.text = getString(R.string.moderate)
        dailyActivityRadioGroup.addView(dailyActivityModerateRadioButton)

        val dailyActivityHeavyRadioButton = RadioButton(this)
        dailyActivityHeavyRadioButton.id = View.generateViewId()
        dailyActivityHeavyRadioButton.text = getString(R.string.heavy)
        dailyActivityRadioGroup.addView(dailyActivityHeavyRadioButton)

        val dailyActivityVeryHeavyRadioButton = RadioButton(this)
        dailyActivityVeryHeavyRadioButton.id = View.generateViewId()
        dailyActivityVeryHeavyRadioButton.text = getString(R.string.very_heavy)
        dailyActivityRadioGroup.addView(dailyActivityVeryHeavyRadioButton)

        val dailyActivitySet = ConstraintSet()
        dailyActivitySet.clone(calculatorConstraintLayout)
        dailyActivitySet.constrainHeight(dailyActivityTextView.id, ConstraintSet.WRAP_CONTENT)
        dailyActivitySet.constrainWidth(dailyActivityTextView.id, ConstraintSet.WRAP_CONTENT)
        dailyActivitySet.connect(dailyActivityTextView.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 16)
        dailyActivitySet.connect(dailyActivityTextView.id, ConstraintSet.TOP, previousView.id, ConstraintSet.BOTTOM, 16)
        dailyActivitySet.connect(dailyActivityTextView.id, ConstraintSet.END, dailyActivityInfoButton.id, ConstraintSet.START, 16)
        dailyActivitySet.constrainHeight(dailyActivityInfoButton.id, ConstraintSet.WRAP_CONTENT)
        dailyActivitySet.constrainWidth(dailyActivityInfoButton.id, ConstraintSet.WRAP_CONTENT)
        dailyActivitySet.connect(dailyActivityInfoButton.id, ConstraintSet.START, dailyActivityTextView.id, ConstraintSet.END, 16)
        dailyActivitySet.connect(dailyActivityInfoButton.id, ConstraintSet.TOP, previousView.id, ConstraintSet.BOTTOM, 16)
        dailyActivitySet.constrainHeight(dailyActivityRadioGroup.id, ConstraintSet.WRAP_CONTENT)
        dailyActivitySet.constrainWidth(dailyActivityRadioGroup.id, 0)
        dailyActivitySet.connect(dailyActivityRadioGroup.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 16)
        dailyActivitySet.connect(dailyActivityRadioGroup.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 16)
        dailyActivitySet.connect(dailyActivityRadioGroup.id, ConstraintSet.TOP, dailyActivityInfoButton.id, ConstraintSet.BOTTOM, 16)
        dailyActivitySet.connect(dailyActivityVeryLightRadioButton.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 16)
        dailyActivitySet.connect(dailyActivityLightRadioButton.id, ConstraintSet.TOP, dailyActivityVeryLightRadioButton.id, ConstraintSet.BOTTOM, 8)
        dailyActivitySet.connect(dailyActivityModerateRadioButton.id, ConstraintSet.TOP, dailyActivityLightRadioButton.id, ConstraintSet.BOTTOM, 8)
        dailyActivitySet.connect(dailyActivityHeavyRadioButton.id, ConstraintSet.TOP, dailyActivityModerateRadioButton.id, ConstraintSet.BOTTOM, 8)
        dailyActivitySet.connect(dailyActivityVeryHeavyRadioButton.id, ConstraintSet.TOP, dailyActivityHeavyRadioButton.id, ConstraintSet.BOTTOM, 8)
        dailyActivitySet.applyTo(calculatorConstraintLayout)

        if (dailyActivity != "") {

            when(dailyActivity) {

                "veryLight" -> dailyActivityRadioGroup.check(dailyActivityVeryLightRadioButton.id)
                "light" -> dailyActivityRadioGroup.check(dailyActivityLightRadioButton.id)
                "moderate" -> dailyActivityRadioGroup.check(dailyActivityModerateRadioButton.id)
                "heavy" -> dailyActivityRadioGroup.check(dailyActivityHeavyRadioButton.id)
                "veryHeavy" -> dailyActivityRadioGroup.check(dailyActivityVeryHeavyRadioButton.id)
                else -> dailyActivityRadioGroup.check(dailyActivityVeryLightRadioButton.id)
            }
        } else {

            dailyActivityRadioGroup.check(dailyActivityVeryLightRadioButton.id)
        }

        dailyActivityInfoButton.setOnClickListener {

            val dailyActivityInfoDialog = AlertDialog.Builder(this)

            dailyActivityInfoDialog.setMessage("Very Light: Sitting, little walking, little other activities in a day " + "\n\n"
                    + "Light: Typing, lab work, some walking in a day" + "\n\n" + "Moderate: Light manual labor job with 1-2 hours weight training or other training"
                    + "\n\n" + "Heavy: Heavy manual labor job with 2-4 hours weight training or sports" + "\n\n" + "Very Heavy: Moderate or heavy for 8 hours or more " +
                    "a day plus 2-4 hours intense training")

            dailyActivityInfoDialog.setPositiveButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->

                dialogInterface.cancel()
            })

            dailyActivityInfoDialog.show()
        }

        dailyActivityRadioGroup.setOnCheckedChangeListener({ _, checkedId ->

            when (checkedId) {

                dailyActivityVeryLightRadioButton.id -> dailyActivity = "veryLight"
                dailyActivityLightRadioButton.id -> dailyActivity = "light"
                dailyActivityModerateRadioButton.id -> dailyActivity = "moderate"
                dailyActivityHeavyRadioButton.id -> dailyActivity = "heavy"
                dailyActivityVeryHeavyRadioButton.id -> dailyActivity = "veryHeavy"
            }
        })

        val physicalActivityLifeStyleTextView = TextView(this)
        physicalActivityLifeStyleTextView.id = View.generateViewId()
        physicalActivityLifeStyleTextView.text = getString(R.string.physical_activity_lifestyle)
        calculatorConstraintLayout.addView(physicalActivityLifeStyleTextView)

        val physicalActivityLifeStyleRadioGroup = RadioGroup(this)
        physicalActivityLifeStyleRadioGroup.id = View.generateViewId()
        physicalActivityLifeStyleRadioGroup.orientation = RadioGroup.VERTICAL
        calculatorConstraintLayout.addView(physicalActivityLifeStyleRadioGroup)

        val sedentaryAdultRadioButton = RadioButton(this)
        sedentaryAdultRadioButton.id = View.generateViewId()
        sedentaryAdultRadioButton.text = getString(R.string.sedentary_adult)
        sedentaryAdultRadioButton.isSelected = true
        physicalActivityLifeStyleRadioGroup.addView(sedentaryAdultRadioButton)
        val recreationalExerciserAdultRadioButton = RadioButton(this)
        recreationalExerciserAdultRadioButton.id = View.generateViewId()
        recreationalExerciserAdultRadioButton.text = getString(R.string.adult_recreational_exerciser)
        physicalActivityLifeStyleRadioGroup.addView(recreationalExerciserAdultRadioButton)
        val competitiveAthleteAdultRadioButton = RadioButton(this)
        competitiveAthleteAdultRadioButton.id = View.generateViewId()
        competitiveAthleteAdultRadioButton.text = getString(R.string.adult_competitive_athlete)
        physicalActivityLifeStyleRadioGroup.addView(competitiveAthleteAdultRadioButton)
        val buildingMuscleAdultRadioButton = RadioButton(this)
        buildingMuscleAdultRadioButton.id = View.generateViewId()
        buildingMuscleAdultRadioButton.text = getString(R.string.adult_building_muscle)
        physicalActivityLifeStyleRadioGroup.addView(buildingMuscleAdultRadioButton)
        val dietingAthleteRadioButton = RadioButton(this)
        dietingAthleteRadioButton.id = View.generateViewId()
        dietingAthleteRadioButton.text = getString(R.string.dieting_athlete)
        physicalActivityLifeStyleRadioGroup.addView(dietingAthleteRadioButton)
        val growingAthleteTeenager = RadioButton(this)
        growingAthleteTeenager.id = View.generateViewId()
        growingAthleteTeenager.text = getString(R.string.teen_growing_athlete)
        physicalActivityLifeStyleRadioGroup.addView(growingAthleteTeenager)

        val physicalActivityLifeStyleSet = ConstraintSet()
        physicalActivityLifeStyleSet.clone(calculatorConstraintLayout)
        physicalActivityLifeStyleSet.constrainHeight(physicalActivityLifeStyleTextView.id, ConstraintSet.WRAP_CONTENT)
        physicalActivityLifeStyleSet.constrainWidth(physicalActivityLifeStyleTextView.id, ConstraintSet.WRAP_CONTENT)
        physicalActivityLifeStyleSet.connect(physicalActivityLifeStyleTextView.id, ConstraintSet.TOP, dailyActivityRadioGroup.id, ConstraintSet.BOTTOM, 16)
        physicalActivityLifeStyleSet.connect(physicalActivityLifeStyleTextView.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 16)
        physicalActivityLifeStyleSet.constrainHeight(physicalActivityLifeStyleRadioGroup.id, ConstraintSet.WRAP_CONTENT)
        physicalActivityLifeStyleSet.constrainWidth(physicalActivityLifeStyleRadioGroup.id, 0)
        physicalActivityLifeStyleSet.connect(physicalActivityLifeStyleRadioGroup.id, ConstraintSet.TOP, physicalActivityLifeStyleTextView.id, ConstraintSet.BOTTOM, 16)
        physicalActivityLifeStyleSet.connect(physicalActivityLifeStyleRadioGroup.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 16)
        physicalActivityLifeStyleSet.connect(physicalActivityLifeStyleRadioGroup.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 16)
        physicalActivityLifeStyleSet.connect(recreationalExerciserAdultRadioButton.id, ConstraintSet.TOP, sedentaryAdultRadioButton.id, ConstraintSet.BOTTOM, 8)
        physicalActivityLifeStyleSet.connect(competitiveAthleteAdultRadioButton.id, ConstraintSet.TOP, recreationalExerciserAdultRadioButton.id, ConstraintSet.BOTTOM, 8)
        physicalActivityLifeStyleSet.connect(buildingMuscleAdultRadioButton.id, ConstraintSet.TOP, competitiveAthleteAdultRadioButton.id, ConstraintSet.BOTTOM, 8)
        physicalActivityLifeStyleSet.connect(dietingAthleteRadioButton.id, ConstraintSet.TOP, buildingMuscleAdultRadioButton.id, ConstraintSet.BOTTOM, 8)
        physicalActivityLifeStyleSet.connect(growingAthleteTeenager.id, ConstraintSet.TOP, dietingAthleteRadioButton.id, ConstraintSet.BOTTOM, 8)
        physicalActivityLifeStyleSet.applyTo(calculatorConstraintLayout)

        if (physicalActivityLifestyle != "") {

            when(physicalActivityLifestyle) {

                "sedentaryAdult" -> physicalActivityLifeStyleRadioGroup.check(sedentaryAdultRadioButton.id)
                "recreationalExerciserAdult" -> physicalActivityLifeStyleRadioGroup.check(recreationalExerciserAdultRadioButton.id)
                "competitiveAthleteAdult" -> physicalActivityLifeStyleRadioGroup.check(competitiveAthleteAdultRadioButton.id)
                "buildingMuscleAdult" -> physicalActivityLifeStyleRadioGroup.check(buildingMuscleAdultRadioButton.id)
                "dietingAthlete" -> physicalActivityLifeStyleRadioGroup.check(dietingAthleteRadioButton.id)
                "growingAthleteTeenager" -> physicalActivityLifeStyleRadioGroup.check(growingAthleteTeenager.id)
                else -> physicalActivityLifeStyleRadioGroup.check(sedentaryAdultRadioButton.id)
            }

        } else {

            physicalActivityLifeStyleRadioGroup.check(sedentaryAdultRadioButton.id)
        }

        physicalActivityLifeStyleRadioGroup.setOnCheckedChangeListener { _, checkedId ->

            when (checkedId) {

                sedentaryAdultRadioButton.id -> physicalActivityLifestyle = "sedentaryAdult"
                recreationalExerciserAdultRadioButton.id -> physicalActivityLifestyle = "recreationalExerciserAdult"
                competitiveAthleteAdultRadioButton.id ->physicalActivityLifestyle = "competitiveAthleteAdult"
                buildingMuscleAdultRadioButton.id -> physicalActivityLifestyle = "buildingMuscleAdult"
                dietingAthleteRadioButton.id -> physicalActivityLifestyle = "dietingAthlete"
                growingAthleteTeenager.id -> physicalActivityLifestyle = "growingAthleteTeenager"
            }
        }

        val fatPercentageTextView = TextView(this)
        fatPercentageTextView.id = View.generateViewId()
        fatPercentageTextView.text = getString(R.string.preferred_fat)
        calculatorConstraintLayout.addView(fatPercentageTextView)

        val fatPercentageImageButton = ImageButton(this)
        fatPercentageImageButton.id = View.generateViewId()
        fatPercentageImageButton.setImageResource(R.drawable.ic_info)
        calculatorConstraintLayout.addView(fatPercentageImageButton)

        val fatPercentageEditText = EditText(this)
        fatPercentageEditText.id = View.generateViewId()
        //fatPercentageEditText.hint = "Percent"
        fatPercentageEditText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        calculatorConstraintLayout.addView(fatPercentageEditText)

        val fatPercentagePercentTextView = TextView(this)
        fatPercentagePercentTextView.id = View.generateViewId()
        fatPercentagePercentTextView.text = "%"
        calculatorConstraintLayout.addView(fatPercentagePercentTextView)

        val fatPercentageSet = ConstraintSet()
        fatPercentageSet.clone(calculatorConstraintLayout)
        fatPercentageSet.constrainHeight(fatPercentageTextView.id, ConstraintSet.WRAP_CONTENT)
        fatPercentageSet.constrainWidth(fatPercentageTextView.id, ConstraintSet.WRAP_CONTENT)
        fatPercentageSet.connect(fatPercentageTextView.id, ConstraintSet.TOP, physicalActivityLifeStyleRadioGroup.id, ConstraintSet.BOTTOM, 16)
        fatPercentageSet.connect(fatPercentageTextView.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 16)
        fatPercentageSet.connect(fatPercentageImageButton.id, ConstraintSet.START, fatPercentageTextView.id, ConstraintSet.END, 16)
        fatPercentageSet.connect(fatPercentageImageButton.id, ConstraintSet.TOP, physicalActivityLifeStyleRadioGroup.id, ConstraintSet.BOTTOM, 16)
        fatPercentageSet.connect(fatPercentageEditText.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 16)
        fatPercentageSet.connect(fatPercentageEditText.id, ConstraintSet.TOP, fatPercentageTextView.id, ConstraintSet.BOTTOM, 16)
        fatPercentageSet.connect(fatPercentagePercentTextView.id, ConstraintSet.START, fatPercentageEditText.id, ConstraintSet.END, 16)
        fatPercentageSet.connect(fatPercentagePercentTextView.id, ConstraintSet.TOP, fatPercentageTextView.id, ConstraintSet.BOTTOM, 16)
        fatPercentageSet.applyTo(calculatorConstraintLayout)

        fatPercentageImageButton.setOnClickListener {

            val fatPercentDialog = AlertDialog.Builder(this)

            fatPercentDialog.setMessage("13-18: Please stay in the 25-35% range for best results." + "\n\n" + "19+: Please stay in the 20-35% range for best results."
                    + "\n\n" + "Enjoy any percentage regardless of what your current goal is as long as you stay within the suggested ranges." + "\n\n" +
                    "Note: Try to keep saturated fats under 10%.")

            fatPercentDialog.setPositiveButton("OK", { dialogInterface, i ->

                dialogInterface.cancel()
            })

            fatPercentDialog.show()
        }

        val goalTextView = TextView(this)
        goalTextView.id = View.generateViewId()
        goalTextView.text = getString(R.string.goal)
        calculatorConstraintLayout.addView(goalTextView)

        val goalRadioGroup = RadioGroup(this)
        goalRadioGroup.id = View.generateViewId()
        goalRadioGroup.orientation = RadioGroup.VERTICAL
        calculatorConstraintLayout.addView(goalRadioGroup)

        val goalWeightLossSuggestedRadioButton = RadioButton(this)
        goalWeightLossSuggestedRadioButton.id = View.generateViewId()
        goalWeightLossSuggestedRadioButton.text = getString(R.string.burn_suggested)
        goalRadioGroup.addView(goalWeightLossSuggestedRadioButton)

        val goalWeightLossAggressiveRadioButton = RadioButton(this)
        goalWeightLossAggressiveRadioButton.id = View.generateViewId()
        goalWeightLossAggressiveRadioButton.text = getString(R.string.burn_aggressive)
        goalRadioGroup.addView(goalWeightLossAggressiveRadioButton)

        val goalWeightLossRecklessRadioButton = RadioButton(this)
        goalWeightLossRecklessRadioButton.id = View.generateViewId()
        goalWeightLossRecklessRadioButton.text = getString(R.string.burn_reckless)
        goalRadioGroup.addView(goalWeightLossRecklessRadioButton)

        val goalMaintainRadioButton = RadioButton(this)
        goalMaintainRadioButton.id = View.generateViewId()
        goalMaintainRadioButton.text = getString(R.string.maintain)
        goalMaintainRadioButton.isSelected = true
        goalRadioGroup.addView(goalMaintainRadioButton)

        val goalBulkingSuggestedRadioButton = RadioButton(this)
        goalBulkingSuggestedRadioButton.id = View.generateViewId()
        goalBulkingSuggestedRadioButton.text = getString(R.string.build_suggested)
        goalRadioGroup.addView(goalBulkingSuggestedRadioButton)

        val goalBulkingAggressiveRadioButton = RadioButton(this)
        goalBulkingAggressiveRadioButton.id = View.generateViewId()
        goalBulkingAggressiveRadioButton.text = getString(R.string.build_aggressive)
        goalRadioGroup.addView(goalBulkingAggressiveRadioButton)

        val goalBulkingRecklessRadioButton = RadioButton(this)
        goalBulkingRecklessRadioButton.id = View.generateViewId()
        goalBulkingRecklessRadioButton.text = getString(R.string.build_reckless)
        goalRadioGroup.addView(goalBulkingRecklessRadioButton)

        val goalSet = ConstraintSet()
        goalSet.clone(calculatorConstraintLayout)
        goalSet.constrainHeight(goalTextView.id, ConstraintSet.WRAP_CONTENT)
        goalSet.constrainWidth(goalTextView.id, ConstraintSet.WRAP_CONTENT)
        goalSet.connect(goalTextView.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 16)
        goalSet.connect(goalTextView.id, ConstraintSet.TOP, fatPercentageEditText.id, ConstraintSet.BOTTOM, 16)
        goalSet.constrainHeight(goalRadioGroup.id, ConstraintSet.WRAP_CONTENT)
        goalSet.constrainWidth(goalRadioGroup.id, 0)
        goalSet.connect(goalRadioGroup.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 16)
        goalSet.connect(goalRadioGroup.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 16)
        goalSet.connect(goalRadioGroup.id, ConstraintSet.TOP, goalTextView.id, ConstraintSet.BOTTOM, 16)
        goalSet.connect(goalWeightLossSuggestedRadioButton.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 8)
        goalSet.connect(goalWeightLossAggressiveRadioButton.id, ConstraintSet.TOP, goalWeightLossSuggestedRadioButton.id, ConstraintSet.BOTTOM, 8)
        goalSet.connect(goalWeightLossRecklessRadioButton.id, ConstraintSet.TOP, goalWeightLossAggressiveRadioButton.id, ConstraintSet.BOTTOM, 8)
        goalSet.connect(goalMaintainRadioButton.id, ConstraintSet.TOP, goalWeightLossRecklessRadioButton.id, ConstraintSet.BOTTOM, 8)
        goalSet.connect(goalBulkingSuggestedRadioButton.id, ConstraintSet.TOP, goalMaintainRadioButton.id, ConstraintSet.BOTTOM, 8)
        goalSet.connect(goalBulkingAggressiveRadioButton.id, ConstraintSet.TOP, goalBulkingSuggestedRadioButton.id, ConstraintSet.BOTTOM, 8)
        goalSet.connect(goalBulkingRecklessRadioButton.id, ConstraintSet.TOP, goalBulkingAggressiveRadioButton.id, ConstraintSet.BOTTOM, 8)
        goalSet.applyTo(calculatorConstraintLayout)

        if (goal != "") {

            when(goal) {

                "weightLossSuggested" -> goalRadioGroup.check(goalWeightLossSuggestedRadioButton.id)
                "weightLossAggressive" -> goalRadioGroup.check(goalWeightLossAggressiveRadioButton.id)
                "weightLossReckless" -> goalRadioGroup.check(goalWeightLossRecklessRadioButton.id)
                "maintain" -> goalRadioGroup.check(goalMaintainRadioButton.id)
                "bulkingSuggested" -> goalRadioGroup.check(goalBulkingSuggestedRadioButton.id)
                "bulkingAggressive" -> goalRadioGroup.check(goalBulkingAggressiveRadioButton.id)
                "bulkingReckless" -> goalRadioGroup.check(goalBulkingRecklessRadioButton.id)
                else -> goalRadioGroup.check(goalMaintainRadioButton.id)
            }
        } else {

            goalRadioGroup.check(goalMaintainRadioButton.id)
        }

        goalRadioGroup.setOnCheckedChangeListener { radioGroup, checkedId ->

            when (checkedId) {

                goalWeightLossSuggestedRadioButton.id -> goal = "weightLossSuggested"
                goalWeightLossAggressiveRadioButton.id -> goal = "weightLossAggressive"
                goalWeightLossRecklessRadioButton.id -> goal = "weightLossReckless"
                goalMaintainRadioButton.id -> goal = "maintain"
                goalBulkingSuggestedRadioButton.id -> goal = "bulkingSuggested"
                goalBulkingAggressiveRadioButton.id -> goal = "bulkingAggressive"
                goalBulkingRecklessRadioButton.id -> goal = "bulkingReckless"
            }
        }

        val calculateButton = Button(this)
        calculateButton.id = View.generateViewId()
        calculateButton.text = getString(R.string.calc)
        calculatorConstraintLayout.addView(calculateButton)

        val caloriesTextView = TextView(this)
        caloriesTextView.id = View.generateViewId()
        caloriesTextView.text = getString(R.string.calories)
        calculatorConstraintLayout.addView(caloriesTextView)

        val caloriesCalculatedTextView = TextView(this)
        caloriesCalculatedTextView.id = View.generateViewId()
        calculatorConstraintLayout.addView(caloriesCalculatedTextView)

        val proteinTextView = TextView(this)
        proteinTextView.id = View.generateViewId()
        proteinTextView.text = getString(R.string.protein)
        calculatorConstraintLayout.addView(proteinTextView)

        val proteinCalculatedTextView = TextView(this)
        proteinCalculatedTextView.id = View.generateViewId()
        calculatorConstraintLayout.addView(proteinCalculatedTextView)

        val fatsTextView = TextView(this)
        fatsTextView.id = View.generateViewId()
        fatsTextView.text = getString(R.string.fat)
        calculatorConstraintLayout.addView(fatsTextView)

        val fatsCalculatedTextView = TextView(this)
        fatsCalculatedTextView.id = View.generateViewId()
        calculatorConstraintLayout.addView(fatsCalculatedTextView)

        val carbsTextView = TextView(this)
        carbsTextView.id = View.generateViewId()
        carbsTextView.text = getString(R.string.carbs)
        calculatorConstraintLayout.addView(carbsTextView)

        val carbsCalculatedTextView = TextView(this)
        carbsCalculatedTextView.id = View.generateViewId()
        calculatorConstraintLayout.addView(carbsCalculatedTextView)

        val calculationSet = ConstraintSet()
        calculationSet.clone(calculatorConstraintLayout)
        calculationSet.constrainHeight(calculateButton.id, ConstraintSet.WRAP_CONTENT)
        calculationSet.constrainWidth(calculateButton.id, ConstraintSet.WRAP_CONTENT)
        calculationSet.connect(calculateButton.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 16)
        calculationSet.connect(calculateButton.id, ConstraintSet.TOP, goalRadioGroup.id, ConstraintSet.BOTTOM, 16)
        calculationSet.constrainHeight(caloriesTextView.id, ConstraintSet.WRAP_CONTENT)
        calculationSet.constrainWidth(caloriesTextView.id, ConstraintSet.WRAP_CONTENT)
        calculationSet.connect(caloriesTextView.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 16)
        calculationSet.connect(caloriesTextView.id, ConstraintSet.TOP, calculateButton.id, ConstraintSet.BOTTOM, 16)
        calculationSet.constrainHeight(caloriesCalculatedTextView.id, ConstraintSet.WRAP_CONTENT)
        calculationSet.constrainWidth(caloriesCalculatedTextView.id, ConstraintSet.WRAP_CONTENT)
        calculationSet.connect(caloriesCalculatedTextView.id, ConstraintSet.START, caloriesTextView.id, ConstraintSet.END, 16)
        calculationSet.connect(caloriesCalculatedTextView.id, ConstraintSet.TOP, calculateButton.id, ConstraintSet.BOTTOM, 16)
        calculationSet.constrainHeight(proteinTextView.id, ConstraintSet.WRAP_CONTENT)
        calculationSet.constrainWidth(proteinTextView.id, ConstraintSet.WRAP_CONTENT)
        calculationSet.connect(proteinTextView.id, ConstraintSet.START, caloriesCalculatedTextView.id, ConstraintSet.END, 16)
        calculationSet.connect(proteinTextView.id, ConstraintSet.TOP, calculateButton.id, ConstraintSet.BOTTOM, 16)
        calculationSet.constrainHeight(proteinCalculatedTextView.id, ConstraintSet.WRAP_CONTENT)
        calculationSet.constrainWidth(proteinCalculatedTextView.id, ConstraintSet.WRAP_CONTENT)
        calculationSet.connect(proteinCalculatedTextView.id, ConstraintSet.START, proteinTextView.id, ConstraintSet.END, 16)
        calculationSet.connect(proteinCalculatedTextView.id, ConstraintSet.TOP, calculateButton.id, ConstraintSet.BOTTOM, 16)
        calculationSet.constrainHeight(fatsTextView.id, ConstraintSet.WRAP_CONTENT)
        calculationSet.constrainWidth(fatsTextView.id, ConstraintSet.WRAP_CONTENT)
        calculationSet.connect(fatsTextView.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 16)
        calculationSet.connect(fatsTextView.id, ConstraintSet.TOP, caloriesTextView.id, ConstraintSet.BOTTOM, 16)
        calculationSet.constrainHeight(fatsCalculatedTextView.id, ConstraintSet.WRAP_CONTENT)
        calculationSet.constrainWidth(fatsCalculatedTextView.id, ConstraintSet.WRAP_CONTENT)
        calculationSet.connect(fatsCalculatedTextView.id, ConstraintSet.START, fatsTextView.id, ConstraintSet.END, 16)
        calculationSet.connect(fatsCalculatedTextView.id, ConstraintSet.TOP, caloriesTextView.id, ConstraintSet.BOTTOM, 16)
        calculationSet.constrainHeight(carbsTextView.id, ConstraintSet.WRAP_CONTENT)
        calculationSet.constrainWidth(carbsTextView.id, ConstraintSet.WRAP_CONTENT)
        calculationSet.connect(carbsTextView.id, ConstraintSet.START, fatsCalculatedTextView.id, ConstraintSet.END, 16)
        calculationSet.connect(carbsTextView.id, ConstraintSet.TOP, caloriesTextView.id, ConstraintSet.BOTTOM, 16)
        calculationSet.constrainHeight(carbsCalculatedTextView.id, ConstraintSet.WRAP_CONTENT)
        calculationSet.constrainWidth(carbsCalculatedTextView.id, ConstraintSet.WRAP_CONTENT)
        calculationSet.connect(carbsCalculatedTextView.id, ConstraintSet.START, carbsTextView.id, ConstraintSet.END, 16)
        calculationSet.connect(carbsCalculatedTextView.id, ConstraintSet.TOP, caloriesTextView.id, ConstraintSet.BOTTOM, 16)
        calculationSet.applyTo(calculatorConstraintLayout)

        if (pounds != 0.0 && kg != 0.0 && stone != 0.0) {

            when (weightMeasurement) {

                "imperial" -> poundsEditText.setText(pounds.toString())
                "metric" -> kilogramsEditText.setText(kg.toString())
                "stone" -> stoneEditText.setText(stone.toString())
            }
        }

        if (dietFatPercent != 0.0) {

            fatPercentageEditText.setText(dietFatPercent.toString())
        }

        if (calories != 0 && carbs != 0 && fat != 0 && protein != 0) {

            caloriesCalculatedTextView.text = calories.toString()
            proteinCalculatedTextView.text = protein.toString() + getString(R.string.grams)
            fatsCalculatedTextView.text = fat.toString() + "g"
            carbsCalculatedTextView.text = carbs.toString() + "g"
        } else {

            caloriesCalculatedTextView.text = "0"
            proteinCalculatedTextView.text = getString(R.string.empty_macros)
            fatsCalculatedTextView.text = getString(R.string.empty_macros)
            carbsCalculatedTextView.text = getString(R.string.empty_macros)
        }

        calculateButton.setOnClickListener {

            Log.d("Calculate", "button")

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

                    calculate(fatPercentageEditText, poundsEditText, kilogramsEditText, stoneEditText, decimalFormat, weightMeasurement, birthDate, gender, cm)
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

                    calculate(fatPercentageEditText, poundsEditText, kilogramsEditText, stoneEditText, decimalFormat, weightMeasurement, birthDate, gender, cm)
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

                    calculate(fatPercentageEditText, poundsEditText, kilogramsEditText, stoneEditText, decimalFormat, weightMeasurement, birthDate, gender, cm)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)

        val inflater = menuInflater

        if (auth.currentUser != null) {
            inflater.inflate(R.menu.action_bar_menu, menu)
        } else {
            inflater.inflate(R.menu.no_user_menu, menu)
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item!!.itemId) {

            R.id.action_settings -> {
                val settingsIntent = Intent(this, SettingsActivity::class.java)

                startActivity(settingsIntent)

                return true
            }

            R.id.action_sign_in -> {

                val signInAlert = android.app.AlertDialog.Builder(this)
                val emailEditText = EditText(this)
                val passwordEditText = EditText(this)
                emailEditText.hint = "Email"
                passwordEditText.hint = "Password"
                emailEditText.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                val signInLayout = LinearLayout(this)
                signInLayout.orientation = LinearLayout.VERTICAL
                signInLayout.addView(emailEditText)
                signInLayout.addView(passwordEditText)
                signInAlert.setView(signInLayout)

                signInAlert.setTitle("Sign In").setMessage("Please enter your email and password to sign in.").setPositiveButton("Sign In") {
                    dialog, which ->  auth.signInWithEmailAndPassword(emailEditText.text.toString(), passwordEditText.text.toString()).addOnSuccessListener {
                    recreate()
                }.addOnFailureListener {

                    val loginFailAlert = android.app.AlertDialog.Builder(this)
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

        return super.onOptionsItemSelected(item)
    }
}
