package com.cuinsolutions.macrosmanager

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.GridView
import android.widget.Toast
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_daily.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class DailyActivity : AppCompatActivity() {

    //var dailyMacrosGridView = findViewById<GridView>(R.id.macrosGridView)

    val showAds = false
    private lateinit var auth: FirebaseAuth
    private lateinit var fireStore: FirebaseFirestore
    private var dailyCaloriesTotal = 0
    private var currentCaloriesTotal = 0.0
    private var dailyCarbsTotal = 0
    private var currentCarbsTotal = 0.0
    private var dailyFatTotal = 0
    private var currentFatTotal = 0.0
    private var dailyProteinTotal = 0
    private var currentProteinTotal = 0.0
    private lateinit var dailyMeals: Array<HashMap<String, Any>>
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily)

        auth = FirebaseAuth.getInstance()
        fireStore = FirebaseFirestore.getInstance()

        val dailyBottomNav = findViewById<BottomNavigationView>(R.id.dailyBottomNavtigation)

        dailyBottomNav.selectedItemId = R.id.home

        dailyBottomNav.setOnNavigationItemSelectedListener { item ->

            when(item.itemId) {

                R.id.calculator -> {

                    val calculatorIntent = Intent(this, CalculatorActivity::class.java)

                    startActivity(calculatorIntent)
                }

                R.id.home -> {

                }
            }

            true
        }

        val resetIntent = Intent(this, DailyResetAlarmReciever::class.java)
        val alarmSet = (PendingIntent.getBroadcast(this, 243, resetIntent, PendingIntent.FLAG_UPDATE_CURRENT) != null)

        if (alarmSet == false) {
            setResetAlarm()
        }
    }

    override fun onResume() {
        super.onResume()

        val currentUser = auth.currentUser

        if (currentUser != null) {

            val userId = currentUser.uid
            val db = fireStore.collection("users").document(userId)

            db.get().addOnSuccessListener {

                dailyCaloriesTotal = it.getString("calories")!!.toInt()
                dailyCarbsTotal = it.getString("carbs")!!.toInt()
                dailyFatTotal = it.getString("fat")!!.toInt()
                dailyProteinTotal = it.getString("protein")!!.toInt()
                currentCaloriesTotal = it.getDouble("currentCalories")!!
                currentCarbsTotal = it.getDouble("currentCarbs")!!
                currentFatTotal = it.getDouble("currentFat")!!
                currentProteinTotal = it.getDouble("currentProtein")!!
                dailyMeals = arrayOf(hashMapOf(it.get("dailyMeals") as Pair<String, Any>))
            }.addOnFailureListener {

                Toast.makeText(this,"There was an issue getting your info. Please try again later.", Toast.LENGTH_SHORT).show()
            }
        } else {

            val userPreferences = this.getSharedPreferences("userPreferences", 0)

            if (userPreferences.contains("calories")) {
                dailyCaloriesTotal = userPreferences.getInt("calories", 0)
            }

            if (userPreferences.contains("carbs")) {
                dailyCarbsTotal = userPreferences.getInt("carbs", 0)
            }

            if (userPreferences.contains("fat")) {
                dailyFatTotal = userPreferences.getInt("fat", 0)
            }

            if (userPreferences.contains("protein")) {
                dailyProteinTotal = userPreferences.getInt("protein", 0)
            }

            if (userPreferences.contains("currentCaloriesTotal")) {
                currentCaloriesTotal = userPreferences.getString("currentCaloriesTotal", "").toDouble()
            }

            if (userPreferences.contains("currentCarbsTotal")) {
                currentCarbsTotal = userPreferences.getString("currentCarbsTotal", "").toDouble()
            }

            if (userPreferences.contains("currentFatTotal")) {
                currentFatTotal = userPreferences.getString("currentFatTotal", "").toDouble()
            }

            if (userPreferences.contains("currentProteinTotal")) {
                currentProteinTotal = userPreferences.getString("currentProteinTotal", "").toDouble()
            }

            if (userPreferences.contains("dailyMeals")) {

                val type = object : TypeToken<Pair<String, Any>>() {}.type
                dailyMeals = arrayOf(hashMapOf(gson.fromJson(userPreferences.getString("dailyMeals", ""), type)))
            } else {

                dailyMeals = arrayOf(hashMapOf<String, Any>())
            }
         }

        val dailyConstraintLayout = findViewById<ConstraintLayout>(R.id.dailyConstraintLayout)
        val dailyAdView = findViewById<AdView>(R.id.dailyAdView)
        val dailyBottomNav = findViewById<BottomNavigationView>(R.id.dailyBottomNavtigation)

        if (showAds) {
            val adRequest = AdRequest.Builder().build()
            dailyAdView.loadAd(adRequest)
        } else {
            dailyAdView.visibility = View.GONE
            val removeAdSet = ConstraintSet()
            removeAdSet.clone(dailyConstraintLayout)
            removeAdSet.connect(dailyBottomNav.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
            removeAdSet.applyTo(dailyConstraintLayout)
        }

        dailyBottomNav.selectedItemId = R.id.home
        val dailyMacrosGridView = findViewById<GridView>(R.id.macrosGridView)
        val addMealFAB = findViewById<FloatingActionButton>(R.id.addMealFloatingActionButton)

        val mealsJSONArray = dailyMeals

        addMealFAB.setOnClickListener {

            val addMealIntent = Intent(this, AddMealActivity::class.java)

            addMealIntent.putExtra("mealsJSONArray", mealsJSONArray.toString())
            startActivity(addMealIntent)
        }

        Log.d("Daily Meals", dailyMeals[0].toString())
        if (dailyMeals[0].isNotEmpty()) {

            dailyMacrosGridView.adapter = DailyMacrosGridViewAdapter(this, macrosArrayList())
            userFoodRecyclerView.layoutManager = LinearLayoutManager(this)
            userFoodRecyclerView.adapter = DailyMealsRecyclerViewAdapter(this, dailyMeals, DailyMacrosGridViewAdapter(this, macrosArrayList()))
            } else {

            dailyMacrosGridView.adapter = DailyMacrosGridViewAdapter(this, macrosArrayList())
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

    fun setResetAlarm() {
        val resetTime = Calendar.getInstance(Locale.getDefault())
        resetTime.timeInMillis = System.currentTimeMillis()
        resetTime.set(Calendar.HOUR_OF_DAY, 2)

        val resetIntent = Intent(this, DailyResetAlarmReciever::class.java)
        val resetPendingIntent = PendingIntent.getBroadcast(this, 243, resetIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val resetAlarm = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        resetAlarm.setInexactRepeating(AlarmManager.RTC, resetTime.timeInMillis, AlarmManager.INTERVAL_DAY, resetPendingIntent)
    }

    fun macrosArrayList(): ArrayList<JSONObject> {

        val macrosArrayList = ArrayList<JSONObject>()

        val caloriesJSONObject = JSONObject()
        caloriesJSONObject.put("title", "Calories")
        caloriesJSONObject.put("currentTotal", currentCaloriesTotal)
        caloriesJSONObject.put("dailyTotal", dailyCaloriesTotal)
        macrosArrayList.add(caloriesJSONObject)

        val carbsJSONObject = JSONObject()
        carbsJSONObject.put("title", "Carbs")
        carbsJSONObject.put("currentTotal", currentCarbsTotal)
        carbsJSONObject.put("dailyTotal", dailyCarbsTotal)
        macrosArrayList.add(carbsJSONObject)

        val fatJSONObject = JSONObject()
        fatJSONObject.put("title", "Fat")
        fatJSONObject.put("currentTotal", currentFatTotal)
        fatJSONObject.put("dailyTotal", dailyFatTotal)
        macrosArrayList.add(fatJSONObject)

        val proteinJSONObject = JSONObject()
        proteinJSONObject.put("title", "Protein")
        proteinJSONObject.put("currentTotal", currentProteinTotal)
        proteinJSONObject.put("dailyTotal", dailyProteinTotal)
        macrosArrayList.add(proteinJSONObject)

        return macrosArrayList
    }
}
