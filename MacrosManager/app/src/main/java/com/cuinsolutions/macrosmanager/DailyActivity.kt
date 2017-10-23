package com.cuinsolutions.macrosmanager

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.GridView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import kotlinx.android.synthetic.main.activity_daily.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList


class DailyActivity : AppCompatActivity() {

    //var dailyMacrosGridView = findViewById<GridView>(R.id.macrosGridView)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily)

        val dailyAdView = findViewById<AdView>(R.id.dailyAdView)
        val dailyBottomNav = findViewById<BottomNavigationView>(R.id.dailyBottomNavtigation)

        val adRequest = AdRequest.Builder().build()
        dailyAdView.loadAd(adRequest)

        dailyBottomNav.selectedItemId = R.id.home

        val userPreferences = this.getSharedPreferences("userPreferences", 0)
        val editor = userPreferences.edit()

        if (!userPreferences.contains("calories")) {

            editor.putInt("calories", 0)
            editor.putInt("carbs", 0)
            editor.putInt("fat", 0)
            editor.putInt("protein", 0)
        }

        if (!userPreferences.contains("dailyCaloriesTotal")) {

            editor.putString("dailyCaloriesTotal", "0")
            editor.putString("dailyCarbsTotal", "0")
            editor.putString("dailyFatTotal", "0")
            editor.putString("dailyProteinTotal", "0")
        }

        if (!userPreferences.contains("mealsJSONArray")) {

            val mealsJSONArray = JSONArray()

            editor.putString("mealsJSONArray", mealsJSONArray.toString())
        }

        editor.commit()

        dailyBottomNav.setOnNavigationItemSelectedListener { item ->

            when(item.itemId) {

                R.id.calculator -> {

                    val calculatorIntent = Intent(this, CalculatorActivity::class.java)

                    startActivity(calculatorIntent)
                }

                R.id.home -> {

                }

            /*R.id.search -> {

            }*/
            }

            true
        }

        val resetIntent = Intent(this, DailyResetAlarmReciever::class.java)
        val alarmSet = (PendingIntent.getBroadcast(this, 243, resetIntent, PendingIntent.FLAG_UPDATE_CURRENT) != null)

        if (alarmSet == false) {
            setResetAlarm()
        }
        /**/
    }

    override fun onResume() {
        super.onResume()

        val dailyBottomNav = findViewById<BottomNavigationView>(R.id.dailyBottomNavtigation)

        dailyBottomNav.selectedItemId = R.id.home
        val dailyMacrosGridView = findViewById<GridView>(R.id.macrosGridView)
        val addMealFAB = findViewById<FloatingActionButton>(R.id.addMealFloatingActionButton)
        val userPreferences = this.getSharedPreferences("userPreferences", 0)
        val mealsJSONArray = JSONArray(userPreferences.getString("mealsJSONArray", ""))

        addMealFAB.setOnClickListener {

            val addMealIntent = Intent(this, AddMealActivity::class.java)

            addMealIntent.putExtra("mealsJSONArray", mealsJSONArray.toString())
            startActivity(addMealIntent)
        }

        //if (userPreferences.contains("mealsJSONArray")) {

        if (mealsJSONArray.length() >= 1) {



            dailyMacrosGridView.adapter = DailyMacrosGridViewAdapter(this, macrosArrayList(userPreferences))
            userFoodRecyclerView.layoutManager = LinearLayoutManager(this)
            userFoodRecyclerView.adapter = DailyMealsRecyclerViewAdapter(this, mealsJSONArray, DailyMacrosGridViewAdapter(this, macrosArrayList(userPreferences)))
            } else {

            /*val caloriesJSONObject = JSONObject()
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
            macrosArrayList.add(proteinJSONObject)*/

            dailyMacrosGridView.adapter = DailyMacrosGridViewAdapter(this, macrosArrayList(userPreferences))
            userFoodRecyclerView.adapter = null
        }

        //if (userPreferences.contains("mealsJSONArray")) {
        /*} else {

            userFoodRecyclerView.adapter = null
        }*/

        /**/
        //}
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

    fun setResetAlarm() {
        val resetTime = Calendar.getInstance(Locale.getDefault())
        resetTime.timeInMillis = System.currentTimeMillis()
        resetTime.set(Calendar.HOUR_OF_DAY, 2)

        val resetIntent = Intent(this, DailyResetAlarmReciever::class.java)
        val resetPendingIntent = PendingIntent.getBroadcast(this, 243, resetIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val resetAlarm = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        resetAlarm.setInexactRepeating(AlarmManager.RTC, resetTime.timeInMillis, AlarmManager.INTERVAL_DAY, resetPendingIntent)
    }

    fun macrosArrayList(userPreferences: SharedPreferences): ArrayList<JSONObject> {

        val macrosArrayList = ArrayList<JSONObject>()

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

        return macrosArrayList
    }
}
