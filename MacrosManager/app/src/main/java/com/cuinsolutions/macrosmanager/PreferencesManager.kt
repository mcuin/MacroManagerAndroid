package com.cuinsolutions.macrosmanager

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.cuinsolutions.macrosmanager.utils.CalculatorOptions
import com.cuinsolutions.macrosmanager.utils.Macros
import com.cuinsolutions.macrosmanager.utils.Meal
import com.cuinsolutions.macrosmanager.utils.UserInfo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PreferencesManager(context: Context) {

    val preferences: SharedPreferences = context.getSharedPreferences("userPreferences", Context.MODE_PRIVATE)

    var userInfo: UserInfo
        get() = Gson().fromJson(preferences.getString("userInfo", UserInfo().toString()), UserInfo::class.java)
        set(value) = preferences.edit{ putString("userInfo", Gson().toJson(value)) }

    var macros: Macros
        get() = Gson().fromJson(preferences.getString("macros", Macros(listOf()).toString()), Macros::class.java)
        set(value) = preferences.edit { putString("macros", Gson().toJson(value)) }

    var calculatorOptions: CalculatorOptions
        get() = Gson().fromJson(preferences.getString("calculatorOptions", CalculatorOptions().toString()),
            CalculatorOptions::class.java)
        set(value) = preferences.edit { putString("calculatorOptions", Gson().toJson(value)) }

    var meals: MutableList<Meal>
        get() {
            val token = object: TypeToken<MutableList<Meal>>(){}.type
            return Gson().fromJson(preferences.getString("meals", mutableListOf<Meal>().toString()), token)
        }
        set(value) = preferences.edit { putString("meals", Gson().toJson(value)) }
}