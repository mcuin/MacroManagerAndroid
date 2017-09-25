package com.macromanager.macromanagerandroid

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.GradientDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.constraint.solver.Goal

import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import kotlinx.android.synthetic.main.activity_settings.*
import org.joda.time.DateTime
import org.joda.time.JodaTimePermission
import org.joda.time.Years
import org.joda.time.format.DateTimeFormat
import org.json.JSONArray
import org.json.JSONObject
import org.w3c.dom.Text
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.Year
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class CalculatorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)
    }

    override fun onResume() {
        super.onResume()

        val calculatorConstraintLayout = findViewById<ConstraintLayout>(R.id.calculatorConstraintLayout)
        val weightTextView = findViewById<TextView>(R.id.weightTextView)
        val calculatorBottomNav = findViewById<BottomNavigationView>(R.id.calculatorBottomNav)
        var previousView = View(this)
        val poundsEditText = EditText(this)
        val kilogramsEditText = EditText(this)
        val stoneEditText = EditText(this)
        val stonePoundsEditText = EditText(this)

        val decimalFormat = DecimalFormat("#.##")
        decimalFormat.roundingMode = RoundingMode.FLOOR

        val userPreferences = this.getSharedPreferences("userPreferences", 0)
        val editor = userPreferences.edit()

        Log.d("Weight Measure", userPreferences.getString("weightMeasurement", ""))
        Log.d("Birth Measure", userPreferences.getString("birthDate", ""))
        Log.d("Gender Measure", userPreferences.getString("gender", ""))
        Log.d("Height Measure", userPreferences.getString("heightMeasurement", ""))
        Log.d("CM Measure", userPreferences.getString("cm", ""))

        if (!userPreferences.contains("weightMeasurement") || !userPreferences.contains("birthDate") || !userPreferences.contains("gender")
                || !userPreferences.contains("heightMeasurement") || !userPreferences.contains("cm")) {

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
        } else {

            System.out.print(userPreferences.getString("weightMeasurement", ""))
            Log.d("Weight Measure", userPreferences.getString("weightMeasurement", ""))

            if (userPreferences.getString("weightMeasurement", "") == "imperial") {

                //val poundsEditText = EditText(this)
                poundsEditText.id = View.generateViewId()
                calculatorConstraintLayout.addView(poundsEditText)
                val poundsTextView = TextView(this)
                poundsTextView.id = View.generateViewId()
                calculatorConstraintLayout.addView(poundsTextView)

                poundsTextView.text = "LBS"

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

            if (userPreferences.getString("weightMeasurement", "") == "metric") {

                //val kilogramsEditText = EditText(this)
                kilogramsEditText.id = View.generateViewId()
                calculatorConstraintLayout.addView(kilogramsEditText)
                val kilogramsTextView = TextView(this)
                kilogramsTextView.id = View.generateViewId()
                calculatorConstraintLayout.addView(kilogramsTextView)

                kilogramsTextView.text = "KG"

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

            if (userPreferences.getString("weightMeasurement", "") == "stone") {

                //val stoneEditText = EditText(this)
                stoneEditText.id = View.generateViewId()
                calculatorConstraintLayout.addView(stoneEditText)
                val stoneTextView = TextView(this)
                stoneTextView.id = View.generateViewId()
                calculatorConstraintLayout.addView(stoneTextView)
                //val poundsEditText = EditText(this)
                /*stonePoundsEditText.id = View.generateViewId()
                calculatorConstraintLayout.addView(stonePoundsEditText)
                val poundsTextView = TextView(this)
                poundsTextView.id = View.generateViewId()
                calculatorConstraintLayout.addView(poundsTextView)*/

                stoneTextView.text = "ST"
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
            dailyActivityTextView.text = "Daily Activity Level"
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
            dailyActivityVeryLightRadioButton.text = "Very Light"
            dailyActivityRadioGroup.addView(dailyActivityVeryLightRadioButton)

            val dailyActivityLightRadioButton = RadioButton(this)
            dailyActivityLightRadioButton.id = View.generateViewId()
            dailyActivityLightRadioButton.text = "Light"
            dailyActivityRadioGroup.addView(dailyActivityLightRadioButton)

            val dailyActivityModerateRadioButton = RadioButton(this)
            dailyActivityModerateRadioButton.id = View.generateViewId()
            dailyActivityModerateRadioButton.text = "Moderate"
            dailyActivityRadioGroup.addView(dailyActivityModerateRadioButton)

            val dailyActivityHeavyRadioButton = RadioButton(this)
            dailyActivityHeavyRadioButton.id = View.generateViewId()
            dailyActivityHeavyRadioButton.text = "Heavy"
            dailyActivityRadioGroup.addView(dailyActivityHeavyRadioButton)

            val dailyActivityVeryHeavyRadioButton = RadioButton(this)
            dailyActivityVeryHeavyRadioButton.id = View.generateViewId()
            dailyActivityVeryHeavyRadioButton.text = "Very Heavy"
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

            dailyActivityRadioGroup.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { radioGroup, checkedId ->

                when(checkedId) {

                    dailyActivityVeryLightRadioButton.id -> editor.putString("dailyActivity", "veryLight")
                    dailyActivityLightRadioButton.id -> editor.putString("dailyActivity", "light")
                    dailyActivityModerateRadioButton.id -> editor.putString("dailyActivity", "moderate")
                    dailyActivityHeavyRadioButton.id -> editor.putString("dailyActivity", "heavy")
                    dailyActivityVeryHeavyRadioButton.id -> editor.putString("dailyActivity", "veryHeavy")
                }
            })

            val physicalActivityLifeStyleTextView = TextView(this)
            physicalActivityLifeStyleTextView.id = View.generateViewId()
            physicalActivityLifeStyleTextView.text = "Physical Activity Lifestyle"
            calculatorConstraintLayout.addView(physicalActivityLifeStyleTextView)

            val physicalActivityLifeStyleRadioGroup = RadioGroup(this)
            physicalActivityLifeStyleRadioGroup.id = View.generateViewId()
            physicalActivityLifeStyleRadioGroup.orientation = RadioGroup.VERTICAL
            calculatorConstraintLayout.addView(physicalActivityLifeStyleRadioGroup)

            val sedentaryAdultRadioButton = RadioButton(this)
            sedentaryAdultRadioButton.id = View.generateViewId()
            sedentaryAdultRadioButton.text = "Sedentary Adult"
            physicalActivityLifeStyleRadioGroup.addView(sedentaryAdultRadioButton)
            val recreationalExerciserAdultRadioButton = RadioButton(this)
            recreationalExerciserAdultRadioButton.id = View.generateViewId()
            recreationalExerciserAdultRadioButton.text = "Adult Recreational Exerciser"
            physicalActivityLifeStyleRadioGroup.addView(recreationalExerciserAdultRadioButton)
            val competitiveAthleteAdultRadioButton = RadioButton(this)
            competitiveAthleteAdultRadioButton.id = View.generateViewId()
            competitiveAthleteAdultRadioButton.text = "Adult Competitive Athlete"
            physicalActivityLifeStyleRadioGroup.addView(competitiveAthleteAdultRadioButton)
            val buildingMuscleAdultRadioButton = RadioButton(this)
            buildingMuscleAdultRadioButton.id = View.generateViewId()
            buildingMuscleAdultRadioButton.text = "Adult Building Muscle"
            physicalActivityLifeStyleRadioGroup.addView(buildingMuscleAdultRadioButton)
            val dietingAthleteRadioButton = RadioButton(this)
            dietingAthleteRadioButton.id = View.generateViewId()
            dietingAthleteRadioButton.text = "Dieting Athlete"
            physicalActivityLifeStyleRadioGroup.addView(dietingAthleteRadioButton)
            val growingAthleteTeenager = RadioButton(this)
            growingAthleteTeenager.id = View.generateViewId()
            growingAthleteTeenager.text = "Teenage Growing Athlete"
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
            physicalActivityLifeStyleSet.connect(buildingMuscleAdultRadioButton.id, ConstraintSet.TOP, competitiveAthleteAdultRadioButton.id ,ConstraintSet.BOTTOM, 8)
            physicalActivityLifeStyleSet.connect(dietingAthleteRadioButton.id, ConstraintSet.TOP, buildingMuscleAdultRadioButton.id, ConstraintSet.BOTTOM, 8)
            physicalActivityLifeStyleSet.connect(growingAthleteTeenager.id, ConstraintSet.TOP, dietingAthleteRadioButton.id, ConstraintSet.BOTTOM, 8)
            physicalActivityLifeStyleSet.applyTo(calculatorConstraintLayout)

            physicalActivityLifeStyleRadioGroup.setOnCheckedChangeListener { radioGroup, checkedId ->

                when (checkedId) {

                    sedentaryAdultRadioButton.id -> editor.putString("physicalActivityLifestyle", "sedentaryAdult")
                    recreationalExerciserAdultRadioButton.id -> editor.putString("physicalActivityLifestyle", "recreationalExerciserAdult")
                    competitiveAthleteAdultRadioButton.id -> editor.putString("physicalActivityLifestyle", "competitiveAthleteAdult")
                    buildingMuscleAdultRadioButton.id -> editor.putString("physicalActivityLifestyle", "buildingMuscleAdult")
                    dietingAthleteRadioButton.id -> editor.putString("physicalActivityLifestyle", "dietingAthlete")
                    growingAthleteTeenager.id -> editor.putString("physicalActivityLifestyle", "growingAthleteTeenager")
                }
            }

            val fatPercentageTextView = TextView(this)
            fatPercentageTextView.id = View.generateViewId()
            fatPercentageTextView.text = "Preferred Fat Percent"
            calculatorConstraintLayout.addView(fatPercentageTextView)

            val fatPercentageImageButton = ImageButton(this)
            fatPercentageImageButton.id = View.generateViewId()
            fatPercentageImageButton.setImageResource(R.drawable.ic_info)
            calculatorConstraintLayout.addView(fatPercentageImageButton)

            val fatPercentageEditText = EditText(this)
            fatPercentageEditText.id = View.generateViewId()
            fatPercentageEditText.hint = "Percent"
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

                fatPercentDialog.setPositiveButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->

                    dialogInterface.cancel()
                })

                fatPercentDialog.show()
            }

            val goalTextView = TextView(this)
            goalTextView.id = View.generateViewId()
            goalTextView.text = "Goal"
            calculatorConstraintLayout.addView(goalTextView)

            val goalRadioGroup = RadioGroup(this)
            goalRadioGroup.id = View.generateViewId()
            goalRadioGroup.orientation = RadioGroup.VERTICAL
            calculatorConstraintLayout.addView(goalRadioGroup)

            val goalWeightLossSuggestedRadioButton = RadioButton(this)
            goalWeightLossSuggestedRadioButton.id = View.generateViewId()
            goalWeightLossSuggestedRadioButton.text = "Burn Fat Suggested (-15%)"
            goalRadioGroup.addView(goalWeightLossSuggestedRadioButton)

            val goalWeightLossAggressiveRadioButton = RadioButton(this)
            goalWeightLossAggressiveRadioButton.id = View.generateViewId()
            goalWeightLossAggressiveRadioButton.text = "Burn Fat Aggressive (-20%)"
            goalRadioGroup.addView(goalWeightLossAggressiveRadioButton)

            val goalWeightLossRecklessRadioButton = RadioButton(this)
            goalWeightLossRecklessRadioButton.id = View.generateViewId()
            goalWeightLossRecklessRadioButton.text = "Burn Fat Reckless (-25%)"
            goalRadioGroup.addView(goalWeightLossRecklessRadioButton)

            val goalMaintainRadioButton = RadioButton(this)
            goalMaintainRadioButton.id = View.generateViewId()
            goalMaintainRadioButton.text = "Maintain (+/-0%)"
            goalRadioGroup.addView(goalMaintainRadioButton)

            val goalBulkingSuggestedRadioButton = RadioButton(this)
            goalBulkingSuggestedRadioButton.id = View.generateViewId()
            goalBulkingSuggestedRadioButton.text = "Build Muscle Suggested (+5%)"
            goalRadioGroup.addView(goalBulkingSuggestedRadioButton)

            val goalBulkingAggressiveRadioButton = RadioButton(this)
            goalBulkingAggressiveRadioButton.id = View.generateViewId()
            goalBulkingAggressiveRadioButton.text = "Build Muscle Aggressive (+10%)"
            goalRadioGroup.addView(goalBulkingAggressiveRadioButton)

            val goalBulkingRecklessRadioButton = RadioButton(this)
            goalBulkingRecklessRadioButton.id = View.generateViewId()
            goalBulkingRecklessRadioButton.text = "Build Muscle Reckless (+15%)"
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

            goalRadioGroup.setOnCheckedChangeListener { radioGroup, checkedId ->

                when(checkedId) {

                    goalWeightLossSuggestedRadioButton.id -> editor.putString("goal", "weightLossSuggested")
                    goalWeightLossAggressiveRadioButton.id -> editor.putString("goal", "weightLossAggressive")
                    goalWeightLossRecklessRadioButton.id -> editor.putString("goal", "weightLossReckless")
                    goalMaintainRadioButton.id -> editor.putString("goal", "maintain")
                    goalBulkingSuggestedRadioButton.id -> editor.putString("goal", "bulkingSuggested")
                    goalBulkingAggressiveRadioButton.id -> editor.putString("goal", "bulkingAggressive")
                    goalBulkingRecklessRadioButton.id -> editor.putString("goal", "bulkingReckless")
                }
            }

            val calculateButton = Button(this)
            calculateButton.id = View.generateViewId()
            calculateButton.text = "Calculate"
            calculatorConstraintLayout.addView(calculateButton)

            val caloriesTextView = TextView(this)
            caloriesTextView.id = View.generateViewId()
            caloriesTextView.text = "Calories:"
            calculatorConstraintLayout.addView(caloriesTextView)

            val caloriesCalculatedTextView = TextView(this)
            caloriesCalculatedTextView.id = View.generateViewId()
            calculatorConstraintLayout.addView(caloriesCalculatedTextView)

            val proteinTextView = TextView(this)
            proteinTextView.id = View.generateViewId()
            proteinTextView.text = "Protein:"
            calculatorConstraintLayout.addView(proteinTextView)

            val proteinCalculatedTextView = TextView(this)
            proteinCalculatedTextView.id = View.generateViewId()
            calculatorConstraintLayout.addView(proteinCalculatedTextView)

            val fatsTextView = TextView(this)
            fatsTextView.id = View.generateViewId()
            fatsTextView.text = "Fats:"
            calculatorConstraintLayout.addView(fatsTextView)

            val fatsCalculatedTextView = TextView(this)
            fatsCalculatedTextView.id = View.generateViewId()
            calculatorConstraintLayout.addView(fatsCalculatedTextView)

            val carbsTextView = TextView(this)
            carbsTextView.id = View.generateViewId()
            carbsTextView.text = "Carbs:"
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

            calculateButton.setOnClickListener {

                editor.putString("fatPercent", fatPercentageEditText.text.toString())

                when (userPreferences.getString("weightMeasurement", "")) {

                    "imperial" -> {

                        val pounds = poundsEditText.text.toString().toDouble()
                        val kg = pounds * 0.454
                        val stone = (pounds / 14)

                        editor.putString("pounds", decimalFormat.format(pounds).toString())
                        editor.putString("kg", decimalFormat.format(kg).toString())
                        editor.putString("stone", decimalFormat.format(stone).toString())
                    }

                    "metric" -> {

                        val kg = kilogramsEditText.text.toString().toDouble()
                        val pounds = kg / 0.454
                        val stone = kg * .157

                        editor.putString("pounds", decimalFormat.format(pounds).toString())
                        editor.putString("kg", decimalFormat.format(kg).toString())
                        editor.putString("stone", decimalFormat.format(stone).toString())
                    }

                    "stone" -> {

                        val stone = stoneEditText.text.toString().toDouble()
                        val pounds = stone * 14
                        val kg = stone * 6.35

                        editor.putString("pounds", decimalFormat.format(pounds).toString())
                        editor.putString("kg", decimalFormat.format(kg).toString())
                        editor.putString("stone", decimalFormat.format(stone).toString())
                    }
                }

                editor.apply()

                var bmr = 0.0
                var tdee = 0.0
                var calories = 0.0
                var protein = 0.0
                var proteinCalories = 0.0
                var fat = 0.0
                var fatCalories = 0.0
                var carbs = 0.0
                var carbsCalories = 0.0
                val dateFormat = DateTimeFormat.forPattern("dd/MM/yyyy")//SimpleDateFormat()
                val age = Years.yearsBetween(dateFormat.parseDateTime(userPreferences.getString("birthDate", "")) as DateTime, DateTime.now())

                when(userPreferences.getString("gender", "")) {

                    "male" -> {

                        bmr = (10 * userPreferences.getString("kg", "").toDouble()) + (6.25 * userPreferences.getString("cm", "").toDouble()) - ((5 * age.years) + 5)
                    }

                    "female" -> {

                        bmr = (10 * userPreferences.getString("kg", "").toDouble()) + (6.25 * userPreferences.getString("cm", "").toDouble()) - ((5 * age.years) - 161)
                    }
                }

                when(userPreferences.getString("dailyActivity", "")) {

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

                when(userPreferences.getString("goal", "")) {

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

                editor.putString("calories", calories.toInt().toString())

                when (userPreferences.getString("physicalActivityLifestyle", "")) {

                    "sedentaryAdult" -> {

                        protein = userPreferences.getString("pounds", "").toDouble() * 0.4
                    }

                    "recreationalExerciserAdult" -> {

                        protein = userPreferences.getString("pounds", "").toDouble() * 0.75
                    }

                    "competitiveAthleteAdult" -> {

                        protein = userPreferences.getString("pounds", "").toDouble() * 0.90
                    }

                    "buildingMuscleAdult" -> {

                        protein = userPreferences.getString("pounds", "").toDouble() * 0.90
                    }

                    "dietingAthlete" -> {

                        protein = userPreferences.getString("pounds", "").toDouble() * 0.90
                    }

                    "growingAthleteTeenager" -> {

                        protein = userPreferences.getString("pounds", "").toDouble() * 1.0
                    }

                    else -> {

                        protein = userPreferences.getString("pounds", "").toDouble() * 0.4
                    }
                }

                proteinCalories = protein * 4
                editor.putString("protein", protein.toInt().toString())

                fatCalories = calories * (fatPercentageEditText.text.toString().toDouble() / 100)
                fat = fatCalories / 9

                editor.putString("fat", fat.toInt().toString())

                carbsCalories = tdee - (proteinCalories + fatCalories)
                carbs = carbsCalories / 4

                editor.putString("carbs", carbs.toInt().toString())

                editor.apply()

                caloriesCalculatedTextView.text = userPreferences.getInt("calories", 0).toString()
                proteinCalculatedTextView.text = userPreferences.getInt("protein", 0).toString() + "g"
                fatsCalculatedTextView.text = userPreferences.getInt("fat", 0).toString() + "g"
                carbsCalculatedTextView.text = userPreferences.getInt("carbs", 0).toString() + "g"
            }

            if (userPreferences.contains("pounds") && userPreferences.contains("kg") && userPreferences.contains("stone")) {

                when (userPreferences.getString("weightMeasurement", "")) {

                    "imperial" -> poundsEditText.setText(userPreferences.getString("pounds", ""))
                    "metric" -> kilogramsEditText.setText(userPreferences.getString("kg", ""))
                    "stone" -> stoneEditText.setText(userPreferences.getString("stone", ""))
                }
            }

            if (userPreferences.contains("dailyActivity")) {

                when(userPreferences.getString("dailyActivity", "")) {

                    "veryLight" -> dailyActivityRadioGroup.check(dailyActivityVeryLightRadioButton.id)
                    "light" -> dailyActivityRadioGroup.check(dailyActivityLightRadioButton.id)
                    "moderate" -> dailyActivityRadioGroup.check(dailyActivityModerateRadioButton.id)
                    "heavy" -> dailyActivityRadioGroup.check(dailyActivityHeavyRadioButton.id)
                    "veryHeavy" -> dailyActivityRadioGroup.check(dailyActivityVeryHeavyRadioButton.id)
                }
            } else {

                dailyActivityRadioGroup.check(dailyActivityVeryLightRadioButton.id)
            }

            if (userPreferences.contains("physicalActivityLifestyle")) {

                when (userPreferences.getString("physicalActivityLifestyle", "")) {

                    "sedentaryAdult" -> physicalActivityLifeStyleRadioGroup.check(sedentaryAdultRadioButton.id)
                    "recreationalExerciserAdult" -> physicalActivityLifeStyleRadioGroup.check(recreationalExerciserAdultRadioButton.id)
                    "competitiveAthleteAdult" -> physicalActivityLifeStyleRadioGroup.check(competitiveAthleteAdultRadioButton.id)
                    "buildingMuscleAdult" -> physicalActivityLifeStyleRadioGroup.check(buildingMuscleAdultRadioButton.id)
                    "dietingAthlete" -> physicalActivityLifeStyleRadioGroup.check(dietingAthleteRadioButton.id)
                    "growingAthleteTeenager" -> physicalActivityLifeStyleRadioGroup.check(growingAthleteTeenager.id)
                }
            } else {

                physicalActivityLifeStyleRadioGroup.check(sedentaryAdultRadioButton.id)
            }

            if (userPreferences.contains("fatPercent")) {

                fatPercentageEditText.setText(userPreferences.getString("fatPercent", ""))
            } else {

                fatPercentageEditText.setText("15")
            }

            if (userPreferences.contains("goal")) {

                when (userPreferences.getString("goal", "")) {

                    "weightLossSuggested" -> goalRadioGroup.check(goalWeightLossSuggestedRadioButton.id)
                    "weightLossAggressive" -> goalRadioGroup.check(goalWeightLossAggressiveRadioButton.id)
                    "weightLossReckless" -> goalRadioGroup.check(goalWeightLossRecklessRadioButton.id)
                    "maintain" -> goalRadioGroup.check(goalMaintainRadioButton.id)
                    "bulkingSuggested" -> goalRadioGroup.check(goalBulkingSuggestedRadioButton.id)
                    "bulkingAggressive" -> goalRadioGroup.check(goalBulkingAggressiveRadioButton.id)
                    "bulkingReckless" -> goalRadioGroup.check(goalBulkingRecklessRadioButton.id)
                }
            }

            if (userPreferences.contains("calories") && userPreferences.contains("protein") && userPreferences.contains("fat") && userPreferences.contains("carbs")) {

                caloriesCalculatedTextView.text = userPreferences.getInt("calories", 0).toString()
                proteinCalculatedTextView.text = userPreferences.getInt("protein", 0).toString() + "g"
                fatsCalculatedTextView.text = userPreferences.getInt("fat", 0).toString() + "g"
                carbsCalculatedTextView.text = userPreferences.getInt("carbs", 0).toString() + "g"
            } else {

                caloriesCalculatedTextView.text = "0"
                proteinCalculatedTextView.text = "0g"
                fatsCalculatedTextView.text = "0g"
                carbsCalculatedTextView.text = "0g"
            }
        }
    }
}
