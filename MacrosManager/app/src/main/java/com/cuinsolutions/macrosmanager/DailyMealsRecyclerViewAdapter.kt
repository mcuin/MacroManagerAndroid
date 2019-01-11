package com.cuinsolutions.macrosmanager

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.gson.Gson

/**
 * Created by mykalcuin on 9/13/17.
 */

class DailyMealsRecyclerViewAdapter(val context: Context, val dailyMeals: List<HashMap<String, Any>>, dailyIntakeGridViewAdapter: DailyMacrosGridViewAdapter): androidx.recyclerview.widget.RecyclerView.Adapter<DailyMealsRecyclerViewAdapter.ViewHolder>() {


    /*val context = context
    //val userPreferences = context.applicationContext.getSharedPreferences("userPreferences", 0)
    val mealsJSONArray = dailyMeals//JSONArray(userPreferences.getString("mealsJSONArray", ""))
    val dailyIntakeGridViewAdapter = dailyIntakeGridViewAdapter
    val dailyActivity = DailyActivity()*/

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val mealsRecyclerCell = LayoutInflater.from(context).inflate(R.layout.meal_recycler_cell, parent, false)

        return  ViewHolder(mealsRecyclerCell)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Log.d("mealsJSONArray Adapter", dailyMeals[position].toString())

        val mealJSONObject = dailyMeals[position]
        val gson = Gson()
        holder.mealNameTextView.text = mealJSONObject["title"].toString().replace("_", " ")
        if (mealJSONObject["serving"]!!.equals("1")) {
            holder.mealServingTextView.text = mealJSONObject["serving"].toString() + " serving"
        } else {
            holder.mealServingTextView.text = mealJSONObject["serving"].toString() + " servings"
        }
        holder.mealCaloriesTextVeiw.text = mealJSONObject["calories"].toString() + " calories"

        holder.mealButton.setOnClickListener {

            val mealIntent = Intent(context, AddMealActivity::class.java)
            mealIntent.putExtra("meal", position)
            mealIntent.putExtra("dailyMeals", gson.toJson(dailyMeals))
            context.startActivity(mealIntent)
        }

        /*holder.mealButton.setOnLongClickListener {

            val deleteDialog = AlertDialog.Builder(context)
            deleteDialog.setTitle("Confirm Delete").setMessage("Are you sure you want to delete this item?").setPositiveButton("Yes") {

                dialogInterface, i ->

                val userPreferences = context.applicationContext.getSharedPreferences("userPreferences", 0)
                val editor = userPreferences.edit()
                val mealJSONObject = JSONObject(mealsJSONArray[position].toString())

                val mealServing = mealJSONObject.getString("serving").toDouble()
                val mealCalories = mealJSONObject.getString("calories").toDouble() * mealServing
                val mealCarbs = mealJSONObject.getString("carbs").toDouble() * mealServing
                val mealFat = mealJSONObject.getString("fat").toDouble() * mealServing
                val mealProtein = mealJSONObject.getString("protein").toDouble() * mealServing

                val currentTotalCalories = userPreferences.getString("dailyCaloriesTotal", "").toDouble() - mealCalories
                val currentTotalCarbs = userPreferences.getString("dailyCarbsTotal", "").toDouble() - mealCarbs
                val currentTotalFat = userPreferences.getString("dailyFatTotal", "").toDouble() - mealFat
                val currentTotalProtein = userPreferences.getString("dailyProteinTotal", "").toDouble() - mealProtein

                editor.putString("dailyCaloriesTotal", currentTotalCalories.toString())
                editor.putString("dailyCarbsTotal", currentTotalCarbs.toString())
                editor.putString("dailyFatTotal", currentTotalFat.toString())
                editor.putString("dailyProteinTotal", currentTotalProtein.toString())

                mealsJSONArray.remove(position)

                editor.putString("mealsJSONArray", mealsJSONArray.toString())
                editor.commit()

                notifyDataSetChanged()

                dailyActivity.macrosArrayList(userPreferences)
                dailyIntakeGridViewAdapter
            }.setNegativeButton("Cancel") {
                dialogInterface, i ->  dialogInterface.cancel()
            }

            deleteDialog.show()

            true
        }*/
    }

    override fun getItemCount(): Int {

        return dailyMeals.size
    }

    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        val mealNameTextView = itemView.findViewById<TextView>(R.id.mealTitleTextView)
        val mealServingTextView = itemView.findViewById<TextView>(R.id.mealServingTextView)
        val mealCaloriesTextVeiw = itemView.findViewById<TextView>(R.id.mealCaloriesTextView)
        val mealButton = itemView.findViewById<Button>(R.id.mealButton)
    }
}