package com.cuinsolutions.macrosmanager

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import java.sql.Timestamp
import kotlin.math.absoluteValue

class PreferencesManager(context: Context) {

    val preferences: SharedPreferences = context.getSharedPreferences("userPreferences", Context.MODE_PRIVATE)
    var userInfo: UserInfo
        get() = Gson().fromJson(preferences.getString("userInfo", UserInfo().toString()), UserInfo::class.java)
        set(value) = preferences.edit{ Gson().toJson(value) }
    var macros: Macros
        get() = Gson().fromJson(preferences.getString("macros", Macros().toString()), Macros::class.java)
        set(value) = preferences.edit { Gson().toJson(value) }
    var calculatorOptions: CalculatorOptions
        get() = Gson().fromJson(preferences.getString("calculatorOptions", CalculatorOptions().toString()),
            CalculatorOptions::class.java)
        set(value) = preferences.edit { Gson().toJson(value) }
    /*var showAds: Boolean
        get() = preferences.getBoolean("showAds", true)
        set(value) = preferences.edit{ putBoolean("showAds", value) }
    var gender: String
        get() = preferences.getString("gender", "").toString()
        set(value) = preferences.edit { putString("gender", value) }
    var birthMonth: Int
        get() = preferences.getInt("birthMonth", -1)
        set(value) = preferences.edit{ putInt("birthMonth", value)}
    var birthYear: Int
        get() = preferences.getInt("birthYear", -1)
        set(value) = preferences.edit{ putInt("birthYear", value)}
    var heightMeasurement: String
        get() = preferences.getString("heightMeasurement", "").toString()
        set(value) = preferences.edit { putString("heightMeasurement", value) }
    var weightMeasurement: String
        get() = preferences.getString("weightMeasurement", "").toString()
        set(value) = preferences.edit { putString("weightMeasurement", value) }
    var heightCm: Float
        get() = preferences.getFloat("heightCm", 0.0f)
        set(value) = preferences.edit { putFloat("heightCm", value) }
    var weightKg: Float
        get() = preferences.getFloat("weightKg", 0.0f)
        set(value) = preferences.edit { putFloat("weightKg", value) }
    var dailyCalories: Int
        get() = preferences.getInt("dailyCalories", -1)
        set(value) = preferences.edit { putInt("dailyCalories", value) }
    var dailyCarbs: Int
        get() = preferences.getInt("dailyCarbs", -1)
        set(value) = preferences.edit { putInt("dailyCarbs", value) }
    var dailyFats: Int
        get() = preferences.getInt("dailyFats", -1)
        set(value) = preferences.edit { putInt("dailyFats", value) }
    var dailyProtein: Int
        get() = preferences.getInt("dailyProtein", -1)
        set(value) = preferences.edit { putInt("dailyProtein", value) }
    var currentCalories: Float
        get() = preferences.getFloat("currentCalories", -1.0f)
        set(value) = preferences.edit { putFloat("currentCalories", value) }
    var currentCarbs: Float
        get() = preferences.getFloat("currentCarbs", -1.0f)
        set(value) = preferences.edit { putFloat("currentCarbs", value) }
    var currentFats: Float
        get() = preferences.getFloat("currentFats", -1.0f)
        set(value) = preferences.edit { putFloat("currentFats", value) }
    var currentProtein: Float
        get() = preferences.getFloat("currentProtein", -1.0f)
        set(value) = preferences.edit { putFloat("currentProtein", value) }
    var dailyActivity: String
        get() = preferences.getString("dailyActivity", "").toString()
        set(value) = preferences.edit { putString("dailyActivity", "") }
    var goal: String
        get() = preferences.getString("goal", "").toString()
        set(value) = preferences.edit { putString("goal", "") }
    var physicalActivityLifestyle: String
        get() = preferences.getString("physicalActivityLifestyle", "").toString()
        set(value) = preferences.edit { putString("physicalActivityLifestyle", "") }*/
}