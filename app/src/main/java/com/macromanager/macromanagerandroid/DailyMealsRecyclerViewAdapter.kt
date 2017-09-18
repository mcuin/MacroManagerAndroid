package com.macromanager.macromanagerandroid

import android.content.Context
import android.content.SharedPreferences
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
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

        val mealJSONObject = JSONObject(mealsJSONArray[position].toString())
        holder.mealNameTextView.text = mealJSONObject.getString("title")
        holder.mealServingTextView.text = mealJSONObject.getString("serving")
        holder.mealCaloriesTextVeiw.text = mealJSONObject.getString("calories")
    }

    override fun getItemCount(): Int {

        return mealsJSONArray.length()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val mealNameTextView = itemView.findViewById<TextView>(R.id.mealTitleTextView)
        val mealServingTextView = itemView.findViewById<TextView>(R.id.mealServingTextView)
        val mealCaloriesTextVeiw = itemView.findViewById<TextView>(R.id.mealCaloriesTextView)
    }
}