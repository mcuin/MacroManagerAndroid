package com.cuinsolutions.macrosmanager

import android.icu.text.DecimalFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cuinsolutions.macrosmanager.databinding.MealRecyclerCellBinding
import com.cuinsolutions.macrosmanager.utils.Meal

/**
 * Created by mykalcuin on 9/13/17.
 */
class DailyMealsRecyclerViewAdapter(val onClick: (id: Int) -> Unit): RecyclerView.Adapter<DailyMealsRecyclerViewAdapter.ViewHolder>() {

    private var currentMeals = emptyList<Meal>()

    fun setMeals(meals: List<Meal>) {

        currentMeals = meals

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.meal_recycler_cell, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentMeals[position])
        holder.itemView.setOnClickListener {
            onClick(currentMeals[position].id)
        }
    }

    override fun getItemCount(): Int {

        return currentMeals.count()
    }

    class ViewHolder(val binding: MealRecyclerCellBinding) : RecyclerView.ViewHolder(binding.root) {

        private val decimalFormat = DecimalFormat("#.##")

        fun bind(meal: Meal) {
            binding.mealName = meal.mealName
            binding.servingSize = decimalFormat.format(meal.servingSize.toString())
            binding.setMealCalories(decimalFormat.format(meal.mealCalories * meal.servingSize))
            binding.setMealCarbs(decimalFormat.format(meal.mealCarbs * meal.servingSize))
            binding.setMealFats(decimalFormat.format(meal.mealFats * meal.servingSize))
            binding.setMealProtein(decimalFormat.format(meal.mealProtein * meal.servingSize))
        }
    }
}