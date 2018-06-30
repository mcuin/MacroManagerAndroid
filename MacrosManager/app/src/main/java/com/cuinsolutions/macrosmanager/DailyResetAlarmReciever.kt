package com.cuinsolutions.macrosmanager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONArray

/**
 * Created by mykalcuin on 9/21/17.
 */

class DailyResetAlarmReciever : BroadcastReceiver() {

    override fun onReceive(p0: Context?, p1: Intent?) {

        val auth = FirebaseAuth.getInstance()
        val fireStore = FirebaseFirestore.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null) {

            val userId = currentUser.uid
            val db = fireStore.collection("users").document(userId)
            val userData = hashMapOf<String, Any>("currentCalories" to 0.0, "currentCarbs" to 0.0, "currentFat" to 0.0, "currentProtein" to 0.0,
                    "dailyMeals" to listOf<HashMap<String, Any>>())

            db.update(userData).addOnSuccessListener {

                Log.d("Daily data", "reset success")
            }.addOnFailureListener {

                Log.d("Daily data", "reset failed")
            }
        } else {

            val userPreferences = p0!!.getSharedPreferences("userPreferences", 0)
            val editor = userPreferences.edit()

            editor.putString("dailyMeals", listOf<HashMap<String, Any>>().toString())
            editor.putString("currentCaloriesTotal", (0.0).toString())
            editor.putString("currentCarbsTotal", (0.0).toString())
            editor.putString("currentFatTotal", (0.0).toString())
            editor.putString("currentProteinTotal", (0.0).toString())
            editor.apply()

            Log.d("Daily pref", "reset")
        }
    }

}
