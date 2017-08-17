package com.macromanager.macromanagerandroid

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet

import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import org.w3c.dom.Text

class CalculatorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)

        val calculatorConstraintLayout = findViewById<ConstraintLayout>(R.id.calculatorConstraintLayout)
        val weightTextView = findViewById<TextView>(R.id.weightTextView)
        val calculatorBottomNav = findViewById<BottomNavigationView>(R.id.calculatorBottomNav)
        var previousView = View(this)

        val userPreferences = this.getSharedPreferences("userPreferences", 0)

        Log.d("Weight Measure", userPreferences.getString("weightMeasurement", ""))
        Log.d("Birth Measure", userPreferences.getString("birthDate", ""))
        Log.d("Gender Measure", userPreferences.getString("gender", ""))
        Log.d("Height Measure", userPreferences.getString("heightMeasurement", ""))
        Log.d("CM Measure", userPreferences.getFloat("cm", 0.0f).toString())

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

                val poundsEditText = EditText(this)
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

                val kilogramsEditText = EditText(this)
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

                val stoneEditText = EditText(this)
                stoneEditText.id = View.generateViewId()
                calculatorConstraintLayout.addView(stoneEditText)
                val stoneTextView = TextView(this)
                stoneTextView.id = View.generateViewId()
                calculatorConstraintLayout.addView(stoneTextView)
                val poundsEditText = EditText(this)
                poundsEditText.id = View.generateViewId()
                calculatorConstraintLayout.addView(poundsEditText)
                val poundsTextView = TextView(this)
                poundsTextView.id = View.generateViewId()
                calculatorConstraintLayout.addView(poundsTextView)

                stoneTextView.text = "ST"
                poundsTextView.text = "LBS"

                val weightStoneSet = ConstraintSet()

                weightStoneSet.clone(calculatorConstraintLayout)
                weightStoneSet.constrainWidth(stoneEditText.id, ConstraintSet.WRAP_CONTENT)
                weightStoneSet.constrainHeight(stoneEditText.id, ConstraintSet.WRAP_CONTENT)
                weightStoneSet.constrainWidth(stoneTextView.id, ConstraintSet.WRAP_CONTENT)
                weightStoneSet.constrainHeight(stoneTextView.id, ConstraintSet.WRAP_CONTENT)
                weightStoneSet.constrainWidth(poundsEditText.id, ConstraintSet.WRAP_CONTENT)
                weightStoneSet.constrainHeight(poundsEditText.id, ConstraintSet.WRAP_CONTENT)
                weightStoneSet.constrainWidth(poundsTextView.id, ConstraintSet.WRAP_CONTENT)
                weightStoneSet.constrainHeight(poundsTextView.id, ConstraintSet.WRAP_CONTENT)
                weightStoneSet.connect(stoneEditText.id, ConstraintSet.TOP, weightTextView.id, ConstraintSet.BOTTOM, 16)
                weightStoneSet.connect(stoneEditText.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 16)
                weightStoneSet.connect(stoneTextView.id, ConstraintSet.TOP, weightTextView.id, ConstraintSet.BOTTOM, 16)
                weightStoneSet.connect(stoneTextView.id, ConstraintSet.START, stoneEditText.id, ConstraintSet.END, 16)
                weightStoneSet.connect(poundsEditText.id, ConstraintSet.TOP, weightTextView.id, ConstraintSet.BOTTOM, 16)
                weightStoneSet.connect(poundsEditText.id, ConstraintSet.START, stoneTextView.id, ConstraintSet.END, 16)
                weightStoneSet.connect(poundsTextView.id, ConstraintSet.TOP, weightTextView.id, ConstraintSet.BOTTOM, 16)
                weightStoneSet.connect(poundsTextView.id, ConstraintSet.START, poundsEditText.id, ConstraintSet.END, 16)
                weightStoneSet.applyTo(calculatorConstraintLayout)

                previousView = stoneEditText
            }
        }
    }
}
