package com.cuinsolutions.macrosmanager

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.LinearLayoutManager
import android.text.InputType
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.GridView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class DailyActivity : AppCompatActivity() {

    //var dailyMacrosGridView = findViewById<GridView>(R.id.macrosGridView)

    private var showAds = true
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
    private var dailyMeals = listOf<HashMap<String, Any>>()
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily)

        auth = FirebaseAuth.getInstance()
        fireStore = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder().setTimestampsInSnapshotsEnabled(true).build()
        fireStore.firestoreSettings = settings

        val resetIntent = Intent(this, DailyResetAlarmReciever::class.java)
        val alarmSet = (PendingIntent.getBroadcast(this, 243, resetIntent, PendingIntent.FLAG_UPDATE_CURRENT) != null)

        if (alarmSet == false) {
            setResetAlarm()
        } else {
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

                Log.d("Firestore", "Success")
                dailyCaloriesTotal = it.getDouble("calories")!!.toInt()
                dailyCarbsTotal = it.getDouble("carbs")!!.toInt()
                dailyFatTotal = it.getDouble("fat")!!.toInt()
                dailyProteinTotal = it.getDouble("protein")!!.toInt()
                currentCaloriesTotal = it.getDouble("currentCalories")!!
                currentCarbsTotal = it.getDouble("currentCarbs")!!
                currentFatTotal = it.getDouble("currentFat")!!
                currentProteinTotal = it.getDouble("currentProtein")!!
                showAds = it.getBoolean("showAds")!!
                dailyMeals = gson.fromJson(it.get("dailyMeals").toString(), object : TypeToken<List<HashMap<String, Any>>>() {}.type)

                Log.d("Firestore", dailyCaloriesTotal.toString())
                Log.d("Firestore", dailyCarbsTotal.toString())
                Log.d("Firestore", dailyFatTotal.toString())
                Log.d("Firestore", dailyProteinTotal.toString())
                Log.d("Firestore", currentCaloriesTotal.toString())
                Log.d("Firestore", currentCarbsTotal.toString())
                Log.d("Firestore", currentFatTotal.toString())
                Log.d("Firestore", currentProteinTotal.toString())
                Log.d("Firestore", showAds.toString())
                Log.d("Firestore", dailyMeals.toString())

                createUI()
            }.addOnFailureListener {

                Toast.makeText(this,"There was an issue getting your info. Please try again later.", Toast.LENGTH_SHORT).show()
            }
        } else {

            Log.d("No", "firestore")
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
                Log.d("Gson Json", userPreferences.getString("dailyMeals", ""))
                dailyMeals = gson.fromJson(userPreferences.getString("dailyMeals", ""), object : TypeToken<List<HashMap<String, Any>>>() {}.type)

                Log.d("Meals", dailyMeals.toString())
            } else {

                dailyMeals = listOf<HashMap<String, Any>>()

               //editor.putString(gson.toJson(dailyMeals.toString()), "dailyMeals")

            }

            createUI()
         }


        val dailyBottomNav = findViewById<BottomNavigationView>(R.id.dailyBottomNavtigation)
        val addMealFAB = findViewById<FloatingActionButton>(R.id.addMealFloatingActionButton)

        //val dailyBottomNav = findViewById<BottomNavigationView>(R.id.dailyBottomNavtigation)

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

        //dailyBottomNav.selectedItemId = R.id.home

        //val mealsJSONArray = dailyMeals

        addMealFAB.setOnClickListener {

            val userPreferences = this.getSharedPreferences("userPreferences", 0)

            val addMealIntent = Intent(this, AddMealActivity::class.java)

            /*if (userPreferences.contains("dailyMeals")) {
                addMealIntent.putExtra("dailyMeals", gson.toJson(dailyMeals.toString()))
            } else {*/
                addMealIntent.putExtra("dailyMeals", gson.toJson(dailyMeals))
            //}

            startActivity(addMealIntent)
        }

        //Log.d("Daily Meals", dailyMeals.toString())

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)

        val inflater = menuInflater

        if (auth.currentUser != null) {
            inflater.inflate(R.menu.action_bar_menu, menu)
        } else {
            inflater.inflate(R.menu.no_user_menu, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item!!.itemId) {

            R.id.action_settings -> {
                val settingsIntent = Intent(this, SettingsActivity::class.java)

                startActivity(settingsIntent)

                return true
            }

            R.id.action_sign_in -> {

                val signInAlert = AlertDialog.Builder(this)
                val emailEditText = EditText(this)
                val passwordEditText = EditText(this)
                emailEditText.hint = "Email"
                passwordEditText.hint = "Password"
                emailEditText.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                val signInLayout = LinearLayout(this)
                signInLayout.orientation = LinearLayout.VERTICAL
                signInLayout.addView(emailEditText)
                signInLayout.addView(passwordEditText)
                signInAlert.setView(signInLayout)

                signInAlert.setTitle("Sign In").setMessage("Please enter your email and password to sign in.").setPositiveButton("Sign In") {
                    dialog, which ->  auth.signInWithEmailAndPassword(emailEditText.text.toString(), passwordEditText.text.toString()).addOnSuccessListener {
                    recreate()
                }.addOnFailureListener {

                    val loginFailAlert = AlertDialog.Builder(this)
                    loginFailAlert.setTitle("Login Failed").setMessage("Your login failed. Please try again later.").setNeutralButton("Ok") {
                        dialog, which ->  dialog.dismiss()
                    }

                    loginFailAlert.show()
                }
                }.setNegativeButton("Cancel") {
                    dialog, which ->  dialog.dismiss()
                }

                signInAlert.show()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun setResetAlarm() {
        val resetTime = Calendar.getInstance(Locale.getDefault())
        resetTime.timeInMillis = System.currentTimeMillis()
        resetTime.set(Calendar.HOUR_OF_DAY, 2)
        resetTime.set(Calendar.MINUTE, 0)
        resetTime.set(Calendar.SECOND, 0)

        val resetIntent = Intent(this, DailyResetAlarmReciever::class.java)
        val resetPendingIntent = PendingIntent.getBroadcast(this, 243, resetIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val resetAlarm = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        Log.d("Reset", "set")

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

    fun createUI() {

        val dailyConstraintLayout = findViewById<ConstraintLayout>(R.id.dailyConstraintLayout)
        val dailyMacrosGridView = findViewById<GridView>(R.id.macrosGridView)
        val dailyAdView = findViewById<AdView>(R.id.dailyAdView)
        val dailyBottomNav = findViewById<BottomNavigationView>(R.id.dailyBottomNavtigation)
        val userFoodRecyclerView = findViewById<RecyclerView>(R.id.userFoodRecyclerView)

        if (showAds) {
            Log.d("Ads", "running")
            MobileAds.initialize(this, getString(R.string.admob_id))
            val adRequest = AdRequest.Builder().build()
            dailyAdView.loadAd(adRequest)
        } else {
            dailyAdView.visibility = View.GONE
            val removeAdSet = ConstraintSet()
            removeAdSet.clone(dailyConstraintLayout)
            removeAdSet.connect(dailyBottomNav.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
            removeAdSet.applyTo(dailyConstraintLayout)
        }

        if (dailyMeals.size != 0) {

            dailyMacrosGridView.adapter = DailyMacrosGridViewAdapter(this, macrosArrayList())
            userFoodRecyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
            userFoodRecyclerView.adapter = DailyMealsRecyclerViewAdapter(this, dailyMeals, DailyMacrosGridViewAdapter(this, macrosArrayList()))
        } else {

            dailyMacrosGridView.adapter = DailyMacrosGridViewAdapter(this, macrosArrayList())
            userFoodRecyclerView.adapter = null
        }
    }
}
