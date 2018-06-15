package com.cuinsolutions.macrosmanager

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_add_meal.*
import kotlinx.android.synthetic.main.activity_daily.*
import org.json.JSONArray
import org.json.JSONObject

class AddMealActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var currentCaloriesTotal = 0.0
    private var currentCarbsTotal = 0.0
    private var currentFatTotal = 0.0
    private var currentProteinTotal = 0.0
    private val gson = Gson()
    val type = object : TypeToken<Pair<String, Any>>() {}.type
    private var mealsJSONArray = JSONArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_meal)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
    }

    override fun onResume() {
        super.onResume()

        val regexs = Regexs()

        val mealIntent = intent
        val mealNameEditText = findViewById<EditText>(R.id.mealNameEditText)
        val mealCaloriesEditText = findViewById<EditText>(R.id.mealCaloriesEditText)
        val mealCarbsEditText = findViewById<EditText>(R.id.mealCarbsEditText)
        val mealFatEditText = findViewById<EditText>(R.id.mealFatEditText)
        val mealProteinEditText = findViewById<EditText>(R.id.mealProteinEditText)
        val mealServingEditText = findViewById<EditText>(R.id.servingEditText)
        val addMealFAB = findViewById<FloatingActionButton>(R.id.saveMealFloatingActionButton)
        val userPreferences = this.getSharedPreferences("userPreferences", 0)
        //val editor = userPreferences.edit()

        val currentUser = auth.currentUser

        if (currentUser != null) {

            val userId = currentUser.uid
            val db = firestore.collection("users").document(userId)

            db.get().addOnSuccessListener {

                currentCaloriesTotal = it.getDouble("currentCalories")!!
                currentCarbsTotal = it.getDouble("currentCarbs")!!
                currentFatTotal = it.getDouble("currentFat")!!
                currentProteinTotal = it.getDouble("currentProtein")!!
                //mealsJSONArray = JSONArray(it.get("dailMeals"))
            }.addOnFailureListener {

                Toast.makeText(this, "There was an issue getting meal data. Please try again later.", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {

            if (userPreferences.contains("currentCaloriesTotal")) {

                currentCaloriesTotal = userPreferences.getString("currentCaloriesTotal", "").toDouble()
                Log.d("current cals total", currentCaloriesTotal.toString())
            }

            if (userPreferences.contains("currentCarbsTotal")) {

                currentCarbsTotal = userPreferences.getString("currentCarbsTotal", "").toDouble()
            }

            if (userPreferences.contains("currentFatTotalTotal")) {

                currentFatTotal = userPreferences.getString("currentFatTotal", "").toDouble()
            }

            if (userPreferences.contains("currentProteinTotal")) {

                currentProteinTotal = userPreferences.getString("currentProteinTotal", "").toDouble()
            }

        }


        if (mealIntent.hasExtra("meal")) {

            mealsJSONArray = JSONArray(mealIntent.getStringExtra("dailyMeals"))

            val mealJSONObject = JSONObject(mealsJSONArray[mealIntent.getIntExtra("meal", 0)].toString())

            mealNameEditText.setText(mealJSONObject.getString("title"))
            mealCaloriesEditText.setText(mealJSONObject.getString("calories"))
            mealCarbsEditText.setText(mealJSONObject.getString("carbs"))
            mealFatEditText.setText(mealJSONObject.getString("fat"))
            mealProteinEditText.setText(mealJSONObject.getString("protein"))
            mealServingEditText.setText(mealJSONObject.getString("serving"))
        }

        addMealFAB.setOnClickListener {

            if (mealNameEditText.text.toString() == "" || mealCaloriesEditText.text.toString() == "" || !regexs.validNumber(mealCaloriesEditText.text.toString())
                    || mealCarbsEditText.text.toString() == "" || !regexs.validNumber(mealCarbsEditText.text.toString()) || mealFatEditText.text.toString() == ""
                    || !regexs.validNumber(mealFatEditText.text.toString()) || mealProteinEditText.text.toString() == "" ||
                    !regexs.validNumber(mealProteinEditText.text.toString()) || servingEditText.text.toString() == "" ||
                    !regexs.validNumber(mealServingEditText.text.toString())) {

                val mealDialogError = AlertDialog.Builder(this)

                mealDialogError.setTitle("Entry Error").setMessage("Please fill out all fields with valid entries.").setPositiveButton("OK") {

                    dialogInterface, i ->
                    dialogInterface.cancel()
                }

                mealDialogError.show()
            } else {
                /*if (!userPreferences.contains("mealsJSONArray")) {

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

            } else*/

                if (mealIntent.hasExtra("meal")) {

                    Log.d("mealsJSONArray Meal", mealIntent.getStringExtra("dailyMeals"))
                    //val mealsJSONArray = JSONArray(mealIntent.getStringExtra("mealsJSONArray"))
                    val mealJSONObject = JSONObject(mealsJSONArray[mealIntent.getIntExtra("meal", 0)].toString())

                    Log.d("update object", mealJSONObject.toString())
                    val mealServing = mealJSONObject.getString("serving").toDouble()
                    val mealCalories = mealJSONObject.getString("calories").toDouble() * mealServing
                    val mealCarbs = mealJSONObject.getString("carbs").toDouble() * mealServing
                    val mealFat = mealJSONObject.getString("fat").toDouble() * mealServing
                    val mealProtein = mealJSONObject.getString("protein").toDouble() * mealServing

                    currentCaloriesTotal = currentCaloriesTotal - mealCalories
                    currentCarbsTotal = currentCarbsTotal - mealCarbs
                    currentFatTotal = currentFatTotal - mealFat
                    currentProteinTotal = currentProteinTotal - mealProtein

                    mealJSONObject.put("title", mealNameEditText.text)
                    mealJSONObject.put("calories", mealCaloriesEditText.text)
                    mealJSONObject.put("carbs", mealCarbsEditText.text)
                    mealJSONObject.put("fat", mealFatEditText.text)
                    mealJSONObject.put("protein", mealProteinEditText.text)
                    mealJSONObject.put("serving", mealServingEditText.text)

                    Log.d("Meal object after", mealJSONObject.toString())
                    mealsJSONArray.put(mealIntent.getIntExtra("meal", 0), mealJSONObject)  //.put(mealIntent.getIntExtra("meal", 0), mealJSONObject)

                } else {

                    //val mealsJSONArray = JSONArray(mealIntent.getStringExtra("mealsJSONArray"))
                    Log.d("mealJSONArrayBefore", mealsJSONArray.toString())
                    val mealJSONObject = JSONObject()

                    mealJSONObject.put("title", mealNameEditText.text)
                    mealJSONObject.put("calories", mealCaloriesEditText.text)
                    mealJSONObject.put("carbs", mealCarbsEditText.text)
                    mealJSONObject.put("fat", mealFatEditText.text)
                    mealJSONObject.put("protein", mealProteinEditText.text)
                    mealJSONObject.put("serving", mealServingEditText.text)

                    mealsJSONArray.put(mealJSONObject)
                }

                val mealServing = mealServingEditText.text.toString().toDouble()
                val mealCalories = mealCaloriesEditText.text.toString().toDouble() * mealServing
                val mealCarbs = mealCarbsEditText.text.toString().toDouble() * mealServing
                val mealFat = mealFatEditText.text.toString().toDouble() * mealServing
                val mealProtein = mealProteinEditText.text.toString().toDouble() * mealServing

                Log.d("Meal Serving", mealServing.toString())
                Log.d("Current cal", currentCaloriesTotal.toString())
                Log.d("Meal cal", mealCalories.toString())
                Log.d("Current carb", currentCarbsTotal.toString())
                Log.d("meal carbs", mealCarbs.toString())
                currentCaloriesTotal = mealCalories + currentCaloriesTotal
                currentCarbsTotal = mealCarbs + currentCarbsTotal
                currentFatTotal = mealFat + currentFatTotal
                currentProteinTotal = mealProtein + currentProteinTotal

                if (currentUser != null) {

                    val userId = currentUser.uid
                    val db = firestore.collection("users").document(userId)
                    val updateData = hashMapOf<String, Any>("currentCalories" to currentCaloriesTotal, "currentCarbs" to currentCarbsTotal, "currentFat"
                            to currentFatTotal, "currentProtein" to currentProteinTotal, "dailyMeals" to mealsJSONArray)
                    db.update(updateData).addOnSuccessListener {

                        if (intent.hasExtra("meal")) {
                            Toast.makeText(this, "Meal has been updated.", Toast.LENGTH_SHORT).show()
                        } else {

                            Toast.makeText(this, "Meal has been added.", Toast.LENGTH_SHORT).show()
                        }

                        finish()
                    }.addOnFailureListener {

                        Toast.makeText(this, "There was an issue saving. Please try again later.", Toast.LENGTH_SHORT).show()
                    }
                } else {

                    val userEditor = userPreferences.edit()

                    userEditor.putString("dailyMeals", mealsJSONArray.toString())
                    userEditor.putString("currentCaloriesTotal", currentCaloriesTotal.toString())
                    userEditor.putString("currentCarbsTotal", currentCarbsTotal.toString())
                    userEditor.putString("currentFatTotal", currentFatTotal.toString())
                    userEditor.putString("currentProteinTotal", currentProteinTotal.toString())

                    userEditor.apply()
                }

                finish()
            }
        }
    }
}
