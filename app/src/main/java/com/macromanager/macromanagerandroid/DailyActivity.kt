package com.macromanager.macromanagerandroid

import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.GridView
import kotlinx.android.synthetic.main.activity_daily.*
import org.json.JSONArray
import org.json.JSONObject


class DailyActivity : AppCompatActivity() {

    //var dailyMacrosGridView = findViewById<GridView>(R.id.macrosGridView)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily)

        val addMealFAB = findViewById<FloatingActionButton>(R.id.addMealFloatingActionButton)
        val dailyBottomNav = findViewById<BottomNavigationView>(R.id.dailyBottomNavtigation)

        val userPreferences = this.getSharedPreferences("userPreferences", 0)
        val editor = userPreferences.edit()

        addMealFAB.setOnClickListener {

            val addMealIntent = Intent(this, AddMealActivity::class.java)

            startActivity(addMealIntent)
        }

        dailyBottomNav.setOnNavigationItemSelectedListener { item ->

            when(item.itemId) {

                R.id.calculator -> {

                    val calculatorIntent = Intent(this, CalculatorActivity::class.java)

                    startActivity(calculatorIntent)
                }

                R.id.home -> {

                }

                R.id.search -> {

                }
            }

            true
        }
    }

    override fun onResume() {
        super.onResume()

        val dailyMacrosGridView = findViewById<GridView>(R.id.macrosGridView)
        val userPreferences = this.getSharedPreferences("userPreferences", 0)
        val editor = userPreferences.edit()

        if (userPreferences.contains("mealsJSONArray")) {

            val macrosArrayList = ArrayList<JSONObject>()

            if (userPreferences.contains("dailyCaloriesTotal") && userPreferences.contains("dailyCarbsTotal") && userPreferences.contains("dailyFatTotal")
                    && userPreferences.contains("dailyProteinTotal")) {

                val caloriesJSONObject = JSONObject()
                caloriesJSONObject.put("title", "Calories")
                caloriesJSONObject.put("currentTotal", userPreferences.getString("dailyCaloriesTotal", ""))
                caloriesJSONObject.put("dailyTotal", userPreferences.getInt("calories", 0))
                macrosArrayList.add(caloriesJSONObject)

                val carbsJSONObject = JSONObject()
                carbsJSONObject.put("title", "Carbs")
                carbsJSONObject.put("currentTotal", userPreferences.getString("dailyCarbsTotal", ""))
                carbsJSONObject.put("dailyTotal", userPreferences.getInt("carbs", 0))
                macrosArrayList.add(carbsJSONObject)

                val fatJSONObject = JSONObject()
                fatJSONObject.put("title", "Fat")
                fatJSONObject.put("currentTotal", userPreferences.getString("dailyFatTotal", ""))
                fatJSONObject.put("dailyTotal", userPreferences.getInt("fat", 0))
                macrosArrayList.add(fatJSONObject)

                val proteinJSONObject = JSONObject()
                proteinJSONObject.put("title", "Protein")
                proteinJSONObject.put("currentTotal", userPreferences.getString("dailyProteinTotal", ""))
                proteinJSONObject.put("dailyTotal", userPreferences.getInt("protein", 0))
                macrosArrayList.add(proteinJSONObject)

                dailyMacrosGridView.adapter = DailyMacrosGridViewAdapter(this, macrosArrayList)
            } else {

                editor.putString("dailyCaloriesTotal", "0")
                editor.putString("dailyCarbsTotal", "0")
                editor.putString("dailyFatTotal", "0")
                editor.putString("dailyProteinTotal", "0")
                editor.apply()

                val caloriesJSONObject = JSONObject()
                caloriesJSONObject.put("title", "Calories")
                caloriesJSONObject.put("currentTotal", userPreferences.getString("dailyCaloriesTotal", ""))
                caloriesJSONObject.put("dailyTotal", userPreferences.getString("calories", ""))
                macrosArrayList.add(caloriesJSONObject)

                val carbsJSONObject = JSONObject()
                carbsJSONObject.put("title", "Carbs")
                carbsJSONObject.put("currentTotal", userPreferences.getString("dailyCarbsTotal", ""))
                carbsJSONObject.put("dailyTotal", userPreferences.getString("carbs", ""))
                macrosArrayList.add(carbsJSONObject)

                val fatJSONObject = JSONObject()
                fatJSONObject.put("title", "Fat")
                fatJSONObject.put("currentTotal", userPreferences.getString("dailyFatTotal", ""))
                fatJSONObject.put("dailyTotal", userPreferences.getString("fat", ""))
                macrosArrayList.add(fatJSONObject)

                val proteinJSONObject = JSONObject()
                proteinJSONObject.put("title", "Protein")
                proteinJSONObject.put("currentTotal", userPreferences.getString("dailyProteinTotal", ""))
                proteinJSONObject.put("dailyTotal", userPreferences.getString("protein", ""))
                macrosArrayList.add(proteinJSONObject)

                editor.putString("macrosArrayList", macrosArrayList.toString())

                dailyMacrosGridView.adapter = DailyMacrosGridViewAdapter(this, macrosArrayList)
            }
        } else {

            if (!userPreferences.contains("dailyCaloriesTotal") || !userPreferences.contains("dailyCarbsTotal") || !userPreferences.contains("dailyFatTotal")
                    || !userPreferences.contains("dailyProteinTotal")) {
                editor.putString("dailyCaloriesTotal", "0")
                editor.putString("dailyCarbsTotal", "0")
                editor.putString("dailyFatTotal", "0")
                editor.putString("dailyProteinTotal", "0")
                editor.apply()
            }

            val caloriesJSONObject = JSONObject()
            caloriesJSONObject.put("title", "Calories")
            caloriesJSONObject.put("currentTotal", 0)
            caloriesJSONObject.put("dailyTotal", "-")
            val carbsJSONObject = JSONObject()
            carbsJSONObject.put("title", "Carbohydrates")
            carbsJSONObject.put("currentTotal", 0)
            carbsJSONObject.put("dailyTotal", "-")
            val fatJSONObject = JSONObject()
            fatJSONObject.put("title", "Fats")
            fatJSONObject.put("currentTotal", 0)
            fatJSONObject.put("dailyTotal", "-")
            val proteinJSONObject = JSONObject()
            proteinJSONObject.put("title", "Proteins")
            proteinJSONObject.put("currentTotal", 0)
            proteinJSONObject.put("dailyTotal", "-")
            val emptyMacrosArrayList = ArrayList<JSONObject>()
            emptyMacrosArrayList.add(caloriesJSONObject)
            emptyMacrosArrayList.add(carbsJSONObject)
            emptyMacrosArrayList.add(fatJSONObject)
            emptyMacrosArrayList.add(proteinJSONObject)

            Log.d("Macros size", emptyMacrosArrayList.size.toString())
            Log.d("Calories", emptyMacrosArrayList[0].getString("title").toString())
            Log.d("Carbs", emptyMacrosArrayList[1].getString("title").toString())

            editor.putString("macrosArrayList", emptyMacrosArrayList.toString())

            dailyMacrosGridView.adapter = DailyMacrosGridViewAdapter(this, emptyMacrosArrayList)
        }

        if (userPreferences.contains("mealsJSONArray")) {

            userFoodRecyclerView.layoutManager = LinearLayoutManager(this)
            userFoodRecyclerView.adapter = DailyMealsRecyclerViewAdapter(this, JSONArray(userPreferences.getString("mealsJSONArray", "")))
        } else {

            userFoodRecyclerView.adapter = null
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)

        val inflater = menuInflater
        inflater.inflate(R.menu.action_bar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item!!.itemId) {

            R.id.action_settings -> {
                val settingsIntent = Intent(this, SettingsActivity::class.java)

                startActivity(settingsIntent)

                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
