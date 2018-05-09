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
import com.google.firebase.auth.FirebaseAuth
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class SettingsActivity : AppCompatActivity() {

    val showAds = true
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        auth = FirebaseAuth.getInstance()

        val regexs = Regexs()

        val settingsConstraintLayout: ConstraintLayout = findViewById<ConstraintLayout>(R.id.settingsConstraintLayout)
        val genderRadioGroup: RadioGroup = findViewById<RadioGroup>(R.id.genderRadioGroup)
        val heightRadioGroup: RadioGroup = findViewById<RadioGroup>(R.id.heightMeasurementRadioGroup)
        val weightRadioGroup: RadioGroup = findViewById<RadioGroup>(R.id.weightMeasurementRadioGroup)
        val heightTextView: TextView = findViewById<TextView>(R.id.heightTextView)
        val birthDateTextView: TextView = findViewById<TextView>(R.id.birthDateTextView)
        val settingsAdView: AdView = findViewById<AdView>(R.id.settingsAdView)

        val adRequest = AdRequest.Builder().build()
        settingsAdView.loadAd(adRequest)

        val feetEditText = EditText(this)
        val feetTextView = TextView(this)
        val inchesEditText = EditText(this)
        val inchesTextView = TextView(this)
        val cmEditText = EditText(this)
        val cmTextView = TextView(this)
        val saveButton = Button(this)

        val decimalFormat = DecimalFormat("#.##")
        decimalFormat.roundingMode = RoundingMode.FLOOR

        saveButton.text = getString(R.string.save)
        settingsConstraintLayout.addView(saveButton)
        saveButton.id = View.generateViewId()

        val saveButtonSet = ConstraintSet()
        saveButtonSet.clone(settingsConstraintLayout)
        saveButtonSet.constrainHeight(saveButton.id, ConstraintSet.WRAP_CONTENT)
        saveButtonSet.constrainWidth(saveButton.id, ConstraintSet.WRAP_CONTENT)
        saveButtonSet.connect(saveButton.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 16)
        saveButtonSet.applyTo(settingsConstraintLayout)

        val userPreferences = this.getSharedPreferences("userPreferences", 0)

        Log.d("mealsJSONArray Setting", userPreferences.getString("mealsJSONArray", ""))

        if (userPreferences.contains("birthDate")) {

            birthDateTextView.setText(userPreferences.getString("birthDate", ""))
        } else {

            birthDateTextView.setText(getString(R.string.set_birth_date))
        }

        val editor = userPreferences.edit()

        birthDateTextView.setOnClickListener {

            val currentCalendar = Calendar.getInstance()
            val birthdayPicker = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { datePicker, year, month, date ->

                val birthCalendar = Calendar.getInstance()
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                birthCalendar.set(year, month, date)
                val birthDate = dateFormat.format(birthCalendar.time)
                editor.putString("birthDate", birthDate)

                editor.commit()

                birthDateTextView.setText(userPreferences.getString("birthDate", ""))

            }, currentCalendar.get(Calendar.YEAR),
                    currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.DATE))

            birthdayPicker.show()
        }

        genderRadioGroup.setOnCheckedChangeListener { group, checkedId ->

            when(checkedId) {

                R.id.maleButton -> editor.putString("gender", "male")
                R.id.femaleButton -> editor.putString("gender", "female")
            }
        }

        weightRadioGroup.setOnCheckedChangeListener { group, checkedId ->

            when(checkedId) {

                R.id.weightImperial -> {

                    editor.putString("weightMeasurement", "imperial");
                }

                R.id.weightMetric -> {

                    editor.putString("weightMeasurement", "metric")
                }

                R.id.weightStone -> {

                    editor.putString("weightMeasurement", "stone")
                }
            }
        }

        heightRadioGroup.setOnCheckedChangeListener { group, checkedId ->

            when(checkedId) {

                R.id.heightImperial -> {

                    editor.putString("heightMeasurement", "imperial")

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

                    if (userPreferences.contains("feet") && userPreferences.contains("inches")) {

                        feetEditText.setText(userPreferences.getString("feet", ""))
                        inchesEditText.setText(userPreferences.getString("inches", ""))
                    }

                    saveButtonSet.clone(settingsConstraintLayout)
                    saveButtonSet.connect(saveButton.id, ConstraintSet.TOP, feetEditText.id, ConstraintSet.BOTTOM, 16)
                    saveButtonSet.applyTo(settingsConstraintLayout)
                }

                R.id.heightMetric -> {

                    editor.putString("heightMeasurement", "metric")

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

                    if (userPreferences.contains("cm")) {

                        cmEditText.setText(userPreferences.getString("cm", ""))
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

        if (userPreferences.getString("weightMeasurement", "") == "imperial") {

            weightRadioGroup.check(R.id.weightImperial)
        } else if (userPreferences.getString("weightMeasurement", "") == "metric") {

            weightRadioGroup.check(R.id.weightMetric)
        } else if (userPreferences.getString("weightMeasurement", "") == "stone") {

            weightRadioGroup.check(R.id.weightStone)
        } else {

            weightRadioGroup.check(R.id.weightImperial)
        }

        if (userPreferences.getString("heightMeasurement", "") == "imperial") {

            heightRadioGroup.check(R.id.heightImperial)
        } else if (userPreferences.getString("heightMeasurement", "") == "metric") {

            heightRadioGroup.check(R.id.heightMetric)
        } else {

            heightRadioGroup.check(R.id.heightImperial)
        }

        saveButton.setOnClickListener {

            if (!userPreferences.contains("birthDate")) {

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
                    val cm = totalInches * 2.54

                    editor.putString("feet", feetEditText.text.toString())
                    editor.putString("inches", decimalFormat.format(inchesEditText.text.toString().toDouble()).toString())
                    editor.putString("cm", decimalFormat.format(cm).toString())
                    editor.commit()

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
                    val feet = feetConversion - feetRemainder
                    val inches = ((cmEditText.text.toString().toFloat()) / 2.54) - (feet * 12) + feetRemainder

                    editor.putString("cm", decimalFormat.format(cmEditText.text.toString().toDouble()).toString())
                    editor.putString("feet", decimalFormat.format(feet).toString())
                    editor.putString("inches", decimalFormat.format(inches).toString())
                    editor.commit()

                    //finish()
                }
            }

            val currentUser = auth.currentUser

            Log.d("Current user", currentUser.toString())

            if (currentUser == null) {

                val createUserDialog = AlertDialog.Builder(this)

                createUserDialog.setTitle("Create Account").setMessage("Would you like to create an account to " +
                        "store your settings and macros?").setPositiveButton("OK",
                        DialogInterface.OnClickListener { dialog, which ->
                            val signUpIntent = Intent(this, SignUpActivity::class.java)

                            signUpIntent.putExtra("gender", userPreferences.getString("gender", ""))
                            signUpIntent.putExtra("weightMeasurement", userPreferences.getString("weightMeasurement", ""))
                            signUpIntent.putExtra("heightMeasurement", userPreferences.getString("heightMeasurement", ""))
                            signUpIntent.putExtra("feet", userPreferences.getString("feet", ""))
                            signUpIntent.putExtra("inches", userPreferences.getString("inches", ""))
                            signUpIntent.putExtra("cm", userPreferences.getString("cm", ""))
                            signUpIntent.putExtra("birthDate", userPreferences.getString("birthDate", ""))
                            startActivity(signUpIntent)

                            finish()
                }).setNegativeButton("No Thanks", DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                    finish()
                })

                createUserDialog.show()
            }

            //finish()
        }
    }

    override fun onResume() {
        super.onResume()

        val currentUser = auth.currentUser

        val settingsConstraintLayout: ConstraintLayout = findViewById<ConstraintLayout>(R.id.settingsConstraintLayout)
        val settingsScrollView = findViewById<ScrollView>(R.id.settingsScrollView)
        val settingsAdView: AdView = findViewById<AdView>(R.id.settingsAdView)

        if (showAds) {
            val adRequest = AdRequest.Builder().build()
            settingsAdView.loadAd(adRequest)
        } else {
            settingsAdView.visibility = View.GONE
            val removeAdSet = ConstraintSet()
            removeAdSet.clone(settingsConstraintLayout)
            removeAdSet.connect(settingsScrollView.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
            removeAdSet.applyTo(settingsConstraintLayout)
        }

    }
}
