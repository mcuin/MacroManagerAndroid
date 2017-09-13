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

        val mealNameEditText = findViewById<EditText>(R.id.mealNameEditText)
        val mealCaloriesEditText = findViewById<EditText>(R.id.mealCaloriesEditText)
        val mealCarbsEditText = findViewById<EditText>(R.id.mealCarbsEditText)
        val mealFatEditText = findViewById<EditText>(R.id.mealFatEditText)
        val mealProteinEditText = findViewById<EditText>(R.id.mealProteinEditText)
        val mealServingEditText = findViewById<EditText>(R.id.servingEditText)
        val addMealFAB = findViewById<FloatingActionButton>(R.id.saveMealFloatingActionButton)
        val userPreferences = this.getSharedPreferences("userPreferences", 0)
        val editor = userPreferences.edit()

        addMealFAB.setOnClickListener {

            if (!userPreferences.contains("mealsJSONArray")) {

                val mealsJSONArray = JSONArray()
                val mealJSONObject = JSONObject()

                mealJSONObject.put("title", mealFatEditText.text)
                mealJSONObject.put("calories", mealCaloriesEditText.text)
                mealJSONObject.put("carbs", mealCarbsEditText.text)
                mealJSONObject.put("fat", mealFatEditText.text)
                mealJSONObject.put("protein", mealProteinEditText.text)
                mealJSONObject.put("serving", mealServingEditText.text)

                mealsJSONArray.put(mealJSONObject)

                editor.putString("mealsJSONArray", mealsJSONArray.toString())
            } else {

                val mealsJSONArray = JSONArray(userPreferences.getString("mealJSONArray", ""))
                val mealJSONObject = JSONObject()

                mealJSONObject.put("title", mealFatEditText.text)
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
