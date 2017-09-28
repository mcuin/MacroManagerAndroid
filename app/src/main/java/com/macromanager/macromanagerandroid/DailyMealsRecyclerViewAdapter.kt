package com.macromanager.macromanagerandroid

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by mykalcuin on 9/13/17.
 */

class DailyMealsRecyclerViewAdapter(context: Context, mealsJSONArray: JSONArray): RecyclerView.Adapter<DailyMealsRecyclerViewAdapter.ViewHolder>() {


    val context = context
    val mealsJSONArray = mealsJSONArray

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val mealsRecyclerCell = LayoutInflater.from(context).inflate(R.layout.meal_recycler_cell, parent, false)

        return  ViewHolder(mealsRecyclerCell)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Log.d("Meals", mealsJSONArray.length().toString())
        val mealJSONObject = JSONObject(mealsJSONArray[position].toString())
        holder.mealNameTextView.text = mealJSONObject.getString("title")
        if (mealJSONObject.getString("serving").equals("1")) {
            holder.mealServingTextView.text = mealJSONObject.getString("serving") + " serving"
        } else {
            holder.mealServingTextView.text = mealJSONObject.getString("serving") + " servings"
        }
        holder.mealCaloriesTextVeiw.text = mealJSONObject.getString("calories") + " calories"

        holder.mealButton.setOnClickListener {

            val mealIntent = Intent(context, AddMealActivity::class.java)
            mealIntent.putExtra("meal", position)
            context.startActivity(mealIntent)
        }
    }

    override fun getItemCount(): Int {

        return mealsJSONArray.length()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val mealNameTextView = itemView.findViewById<TextView>(R.id.mealTitleTextView)
        val mealServingTextView = itemView.findViewById<TextView>(R.id.mealServingTextView)
        val mealCaloriesTextVeiw = itemView.findViewById<TextView>(R.id.mealCaloriesTextView)
        val mealButton = itemView.findViewById<Button>(R.id.mealButton)
    }
}