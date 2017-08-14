package com.macromanager.macromanagerandroid

import android.content.Context
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.ScriptGroup
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.v7.app.AlertDialog
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val settingsConstraintLayout: ConstraintLayout = findViewById(R.id.settingsConstraintLayout) as ConstraintLayout
        val genderRadioGroup: RadioGroup = findViewById(R.id.genderRadioGroup) as RadioGroup
        val heightRadioGroup: RadioGroup = findViewById(R.id.heightMeasurmentRadioGroup) as RadioGroup
        val weightRadioGroup: RadioGroup = findViewById(R.id.weightMeasurmentRadioGroup) as RadioGroup
        val heightTextView: TextView = findViewById(R.id.heightTextView) as TextView

        val feetEditText = EditText(this)
        val feetTextView = TextView(this)
        val inchesEditText = EditText(this)
        val inchesTextView = TextView(this)
        val cmEditText = EditText(this)
        val cmTextView = TextView(this)
        val saveButton = Button(this)

        saveButton.text = "Save"
        settingsConstraintLayout.addView(saveButton)

        val saveButtonSet = ConstraintSet()
        saveButtonSet.clone(settingsConstraintLayout)
        saveButtonSet.constrainHeight(saveButton.id, ConstraintSet.WRAP_CONTENT)
        saveButtonSet.constrainWidth(saveButton.id, ConstraintSet.WRAP_CONTENT)
        saveButtonSet.connect(saveButton.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 16)
        saveButtonSet.applyTo(settingsConstraintLayout)

        var userPreferences = this.getSharedPreferences("userPreferences", 0)

        val editor = userPreferences.edit()

        genderRadioGroup.setOnCheckedChangeListener { group, checkedId ->

            when(checkedId) {

                R.id.maleButton -> editor.putString("gender", "male")
                R.id.femaleButton -> editor.putString("gender", "female")
            }
        }

        weightRadioGroup.setOnCheckedChangeListener { group, checkedId ->

            when(checkedId) {

                R.id.weightImperial -> {

                    editor.putString("weight", "imperial");
                }

                R.id.weightMetric -> {

                    editor.putString("weight", "metric")
                }

                R.id.weightStone -> {

                    editor.putString("weight", "stone")
                }
            }
        }

        heightRadioGroup.setOnCheckedChangeListener { group, checkedId ->

            when(checkedId) {

                R.id.heightImperial -> {

                    editor.putString("height", "imperial")

                    if (cmEditText.visibility == View.VISIBLE) {

                        settingsConstraintLayout.removeView(cmEditText)
                        settingsConstraintLayout.removeView(cmTextView)
                        settingsConstraintLayout.removeView(feetTextView)
                        settingsConstraintLayout.removeView(feetEditText)
                        settingsConstraintLayout.removeView(inchesEditText)
                        settingsConstraintLayout.removeView(inchesTextView)
                    }

                    feetEditText.id = View.generateViewId()
                    feetEditText.setRawInputType(InputType.TYPE_CLASS_NUMBER)
                    inchesEditText.id = View.generateViewId()
                    inchesEditText.setRawInputType(InputType.TYPE_CLASS_NUMBER)
                    feetTextView.id = View.generateViewId()
                    inchesTextView.id = View.generateViewId()

                    feetTextView.text = "FT"
                    feetTextView.gravity = Gravity.CENTER
                    inchesTextView.text = "IN"
                    inchesTextView.gravity = Gravity.CENTER

                    settingsConstraintLayout.addView(feetEditText)
                    settingsConstraintLayout.addView(feetTextView)
                    settingsConstraintLayout.addView(inchesEditText)
                    settingsConstraintLayout.addView(inchesTextView)

                    val feetEditTextSet = ConstraintSet()
                    feetEditTextSet.clone(settingsConstraintLayout)
                    feetEditTextSet.constrainHeight(feetEditText.id, ConstraintSet.WRAP_CONTENT)
                    feetEditTextSet.constrainWidth(feetEditText.id, 200)
                    feetEditTextSet.setMargin(ConstraintSet.PARENT_ID, ConstraintSet.START, 16)
                    feetEditTextSet.connect(feetEditText.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 16)
                    feetEditTextSet.connect(feetEditText.id, ConstraintSet.TOP, heightTextView.id, ConstraintSet.BOTTOM, 16)
                    feetEditTextSet.connect(feetEditText.id, ConstraintSet.END, feetTextView.id, ConstraintSet.START)
                    feetEditTextSet.applyTo(settingsConstraintLayout)

                    val feetTextViewSet = ConstraintSet()
                    feetTextViewSet.clone(settingsConstraintLayout)
                    feetTextViewSet.constrainWidth(feetTextView.id, feetEditText.height)
                    feetTextViewSet.constrainHeight(feetTextView.id, feetEditText.height)
                    feetTextViewSet.connect(feetTextView.id, ConstraintSet.TOP, heightTextView.id, ConstraintSet.BOTTOM, 16)
                    feetTextViewSet.connect(feetTextView.id, ConstraintSet.START, feetEditText.id, ConstraintSet.END)
                    feetTextViewSet.connect(feetTextView.id, ConstraintSet.END, inchesEditText.id, ConstraintSet.START, 8)
                    feetTextViewSet.applyTo(settingsConstraintLayout)

                    val inchesEditTextSet = ConstraintSet()
                    inchesEditTextSet.clone(settingsConstraintLayout)
                    inchesEditTextSet.constrainWidth(inchesEditText.id, 200)
                    inchesEditTextSet.constrainHeight(inchesEditText.id, ConstraintSet.WRAP_CONTENT)
                    inchesEditTextSet.connect(inchesEditText.id, ConstraintSet.TOP, heightTextView.id, ConstraintSet.BOTTOM, 16)
                    inchesEditTextSet.connect(inchesEditText.id, ConstraintSet.START, feetTextView.id, ConstraintSet.END, 8)
                    inchesEditTextSet.connect(inchesEditText.id, ConstraintSet.END, inchesTextView.id, ConstraintSet.START)
                    inchesEditTextSet.applyTo(settingsConstraintLayout)

                    val inchesTextViewSet = ConstraintSet()
                    inchesTextViewSet.clone(settingsConstraintLayout)
                    inchesTextViewSet.constrainWidth(inchesTextView.id, inchesEditText.height)
                    inchesTextViewSet.constrainHeight(inchesTextView.id, inchesEditText.height)
                    inchesTextViewSet.connect(inchesTextView.id, ConstraintSet.TOP, heightTextView.id, ConstraintSet.BOTTOM, 16)
                    inchesTextViewSet.connect(inchesTextView.id, ConstraintSet.START, inchesEditText.id, ConstraintSet.END)
                    inchesTextViewSet.applyTo(settingsConstraintLayout)

                    if (userPreferences.contains("feet") && userPreferences.contains("inches")) {

                        feetEditText.setText(userPreferences.getInt("feet", 0).toString())
                        inchesEditText.setText(userPreferences.getInt("inches", 0).toString())
                    }

                    saveButtonSet.clone(settingsConstraintLayout)
                    saveButtonSet.connect(saveButton.id, ConstraintSet.TOP, feetEditText.id, ConstraintSet.BOTTOM, 16)
                    saveButtonSet.applyTo(settingsConstraintLayout)
                }

                R.id.heightMetric -> {

                    editor.putString("height", "metric")

                    if (feetEditText.visibility == View.VISIBLE) {

                        settingsConstraintLayout.removeView(feetTextView)
                        settingsConstraintLayout.removeView(feetEditText)
                        settingsConstraintLayout.removeView(inchesEditText)
                        settingsConstraintLayout.removeView(inchesTextView)
                        settingsConstraintLayout.removeView(cmEditText)
                        settingsConstraintLayout.removeView(cmTextView)
                    }

                    cmEditText.id = View.generateViewId()
                    cmEditText.setRawInputType(InputType.TYPE_CLASS_NUMBER)
                    cmTextView.id = View.generateViewId()

                    cmTextView.text = "CM"

                    settingsConstraintLayout.addView(cmEditText)
                    settingsConstraintLayout.addView(cmTextView)

                    val cmEditTextSet = ConstraintSet()
                    cmEditTextSet.clone(settingsConstraintLayout)
                    cmEditTextSet.constrainHeight(cmEditText.id, ConstraintSet.WRAP_CONTENT)
                    cmEditTextSet.constrainWidth(cmEditText.id, 200)
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

                    if (userPreferences.contains("cm")) {

                        cmEditText.setText(userPreferences.getInt("cm", 0).toString())
                    }

                    saveButtonSet.clone(settingsConstraintLayout)
                    saveButtonSet.connect(saveButton.id, ConstraintSet.TOP, cmEditText.id, ConstraintSet.BOTTOM, 16)
                    saveButtonSet.applyTo(settingsConstraintLayout)
                }
            }
        }

        if (userPreferences.getString("gender", "") == "male") {

            genderRadioGroup.check(R.id.maleButton)
        } else if (userPreferences.getString("gender", "") == "female") {

            genderRadioGroup.check(R.id.femaleButton)
        } else {

            genderRadioGroup.check(R.id.femaleButton)
        }

        if (userPreferences.getString("weight", "") == "imperial") {

            weightRadioGroup.check(R.id.weightImperial)
        } else if (userPreferences.getString("weight", "") == "metric") {

            weightRadioGroup.check(R.id.weightMetric)
        } else if (userPreferences.getString("weight", "") == "stone") {

            weightRadioGroup.check(R.id.weightStone)
        } else {

            weightRadioGroup.check(R.id.weightImperial)
        }

        if (userPreferences.getString("height", "") == "imperial") {

            heightRadioGroup.check(R.id.heightImperial)
        } else if (userPreferences.getString("height", "") == "metric") {

            heightRadioGroup.check(R.id.heightMetric)
        } else {

            heightRadioGroup.check(R.id.heightImperial)
        }

        saveButton.setOnClickListener {

            if (heightRadioGroup.checkedRadioButtonId == R.id.heightImperial) {

                if (feetEditText.text.toString() == "" || inchesEditText.text.toString() == "" || Integer.valueOf(inchesEditText.text.toString()) >= 13
                        || Integer.valueOf(inchesEditText.text.toString()) < 0) {

                    val heightAlert = AlertDialog.Builder(this)
                    heightAlert.setTitle("Height Error").setMessage("Please enter a valid number for both entries.").setPositiveButton("OK") {

                        dialog, which ->  dialog.cancel()
                    }
                } else {

                    editor.putInt("feet", Integer.valueOf(feetEditText.text.toString()))
                    editor.putInt("inches", Integer.valueOf(inchesEditText.text.toString()))
                    editor.apply()
                    editor.commit()
                    finish()
                }

            } else if (heightRadioGroup.checkedRadioButtonId == R.id.heightMetric) {

                if (cmEditText.text.toString() == "") {

                    val heightAlert = AlertDialog.Builder(this)
                    heightAlert.setTitle("Height Error").setMessage("Please enter a valid number for entry.").setPositiveButton("OK") {

                        dialog, which ->  dialog.cancel()
                    }
                } else {

                    editor.putInt("cm", Integer.valueOf(cmEditText.text.toString()))
                    editor.apply()
                    editor.commit()
                    finish()
                }
            }
        }
    }
}
