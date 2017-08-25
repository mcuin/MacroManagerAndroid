package com.macromanager.macromanagerandroid

import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily)

        val dailyMacrosGridView = findViewById<GridView>(R.id.macrosGridView)
        val userFoodRecyclerView = findViewById<RecyclerView>(R.id.userFoodRecyclerView)
        val dailyBottomNav = findViewById<BottomNavigationView>(R.id.dailyBottomNavtigation)

        val userPreferences = this.getSharedPreferences("userPreferences", 0)

        if (userPreferences.contains("macrosList")) {

            val macrosJSONArray = JSONObject(userPreferences.getString("macrosList", ""))

            Log.d("Inside", "macro if")
            Log.d("macros array", macrosJSONArray.toString())
            Log.d("macros length", macrosJSONArray.length().toString())
            Log.d("macros 0", macrosJSONArray.keys().toString())

            //macrosGridView.adapter = DailyMacrosGridViewAdapter(this, macrosJSONArray)
        } else {

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

            dailyMacrosGridView.adapter = DailyMacrosGridViewAdapter(this, emptyMacrosArrayList)
        }

        userFoodRecyclerView.adapter = null

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
