package com.macromanager.macromanagerandroid

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_add_meal.*
import org.json.JSONArray
import org.json.JSONObject

class AddMealActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_meal)

        val mealIntent = intent
        val mealNameEditText = findViewById<EditText>(R.id.mealNameEditText)
        val mealCaloriesEditText = findViewById<EditText>(R.id.mealCaloriesEditText)
        val mealCarbsEditText = findViewById<EditText>(R.id.mealCarbsEditText)
        val mealFatEditText = findViewById<EditText>(R.id.mealFatEditText)
        val mealProteinEditText = findViewById<EditText>(R.id.mealProteinEditText)
        val mealServingEditText = findViewById<EditText>(R.id.servingEditText)
        val addMealFAB = findViewById<FloatingActionButton>(R.id.saveMealFloatingActionButton)
        val userPreferences = this.getSharedPreferences("userPreferences", 0)
        val editor = userPreferences.edit()

        if (mealIntent.hasExtra("meal")) {

            val mealsJSONArray = JSONArray(userPreferences.getString("mealsJSONArray", ""))

            val mealJSONObject = JSONObject(mealsJSONArray[mealIntent.getIntExtra("meal", 0)].toString())

            mealNameEditText.setText(mealJSONObject.getString("title"))
            mealCaloriesEditText.setText(mealJSONObject.getString("calories"))
            mealCarbsEditText.setText(mealJSONObject.getString("carbs"))
            mealFatEditText.setText(mealJSONObject.getString("fat"))
            mealProteinEditText.setText(mealJSONObject.getString("protein"))
            mealServingEditText.setText(mealJSONObject.getString("serving"))
        }

        addMealFAB.setOnClickListener {

            if (!userPreferences.contains("mealsJSONArray")) {

                val mealsJSONArray = JSONArray()
                val mealJSONObject = JSONObject()

                mealJSONObject.put("title", mealNameEditText.text)
                mealJSONObject.put("calories", mealCaloriesEditText.text)
                mealJSONObject.put("carbs", mealCarbsEditText.text)
                mealJSONObject.put("fat", mealFatEditText.text)
                mealJSONObject.put("protein", mealProteinEditText.text)
                mealJSONObject.put("serving", mealServingEditText.text)

                mealsJSONArray.put(mealJSONObject)

                editor.putString("mealsJSONArray", mealsJSONArray.toString())
            } else if (mealIntent.hasExtra("meal")) {

                val mealsJSONArray = JSONArray(userPreferences.getString("mealsJSONArray", ""))
                val mealJSONObject = JSONObject(mealsJSONArray[mealIntent.getIntExtra("meal", 0)].toString())

                val mealServing = mealJSONObject.getString("serving").toDouble()
                val mealCalories = mealJSONObject.getString("calories").toDouble() * mealServing
                val mealCarbs = mealJSONObject.getString("carbs").toDouble() * mealServing
                val mealFat = mealJSONObject.getString("fat").toDouble() * mealServing
                val mealProtein = mealJSONObject.getString("protein").toDouble() * mealServing

                val currentTotalCalories = userPreferences.getString("dailyCaloriesTotal", "").toDouble() - mealCalories
                val currentTotalCarbs = userPreferences.getString("dailyCarbsTotal", "").toDouble() - mealCarbs
                val currentTotalFat = userPreferences.getString("dailyFatTotal", "").toDouble() - mealFat
                val currentTotalProtein = userPreferences.getString("dailyProteinTotal", "").toDouble() - mealProtein

                editor.putString("dailyCaloriesTotal", currentTotalCalories.toString())
                editor.putString("dailyCarbsTotal", currentTotalCarbs.toString())
                editor.putString("dailyFatTotal", currentTotalFat.toString())
                editor.putString("dailyProteinTotal", currentTotalProtein.toString())

                mealJSONObject.put("title", mealNameEditText.text)
                mealJSONObject.put("calories", mealCaloriesEditText.text)
                mealJSONObject.put("carbs", mealCarbsEditText.text)
                mealJSONObject.put("fat", mealFatEditText.text)
                mealJSONObject.put("protein", mealProteinEditText.text)
                mealJSONObject.put("serving", mealServingEditText.text)

                mealsJSONArray.put(mealIntent.getIntExtra("meal", 0), mealJSONObject)
                editor.putString("mealsJSONArray", mealsJSONArray.toString())

                editor.apply()
            } else {

                val mealsJSONArray = JSONArray(userPreferences.getString("mealJSONArray", ""))
                val mealJSONObject = JSONObject()

                mealJSONObject.put("title", mealNameEditText.text)
                mealJSONObject.put("calories", mealCaloriesEditText.text)
                mealJSONObject.put("carbs", mealCarbsEditText.text)
                mealJSONObject.put("fat", mealFatEditText.text)
                mealJSONObject.put("protein", mealProteinEditText.text)
                mealJSONObject.put("serving", mealServingEditText.text)

                mealsJSONArray.put(mealJSONObject)

                editor.putString("mealsJSONArray", mealsJSONArray.toString())
            }

            val mealServing = mealServingEditText.text.toString().toDouble()
            val mealCalories = mealCaloriesEditText.text.toString().toDouble() * mealServing
            val mealCarbs = mealCarbsEditText.text.toString().toDouble() * mealServing
            val mealFat = mealFatEditText.text.toString().toDouble() * mealServing
            val mealProtein = mealProteinEditText.text.toString().toDouble() * mealServing

            val currentTotalCalories = mealCalories + userPreferences.getString("dailyCaloriesTotal", "").toDouble()
            val currentTotalCarbs = mealCarbs + userPreferences.getString("dailyCarbsTotal", "").toDouble()
            val currentTotalFat = mealFat + userPreferences.getString("dailyFatTotal", "").toDouble()
            val currentTotalProtein = mealProtein + userPreferences.getString("dailyProteinTotal", "").toDouble()

            editor.putString("dailyCaloriesTotal", currentTotalCalories.toString())
            editor.putString("dailyCarbsTotal", currentTotalCarbs.toString())
            editor.putString("dailyFatTotal", currentTotalFat.toString())
            editor.putString("dailyProteinTotal", currentTotalProtein.toString())

            editor.apply()

            finish()
        }
    }
}
