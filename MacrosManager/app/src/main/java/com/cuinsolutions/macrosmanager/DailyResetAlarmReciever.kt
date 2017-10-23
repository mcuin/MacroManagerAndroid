package com.cuinsolutions.macrosmanager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.json.JSONArray

/**
 * Created by mykalcuin on 9/21/17.
 */

class DailyResetAlarmReciever : BroadcastReceiver() {

    override fun onReceive(p0: Context?, p1: Intent?) {

        val userPreferences = p0!!.getSharedPreferences("userPreferences", 0)
        val editor = userPreferences.edit()

        val mealsJSONArray = JSONArray()
        editor.putString("mealsJSONArray", mealsJSONArray.toString())
        editor.putString("dailyCaloriesTotal", (0).toString())
        editor.putString("dailyCarbsTotal", (0).toString())
        editor.putString("dailyFatTotal", (0).toString())
        editor.putString("dailyProteinTotal", (0).toString())
        editor.apply()
    }

}
