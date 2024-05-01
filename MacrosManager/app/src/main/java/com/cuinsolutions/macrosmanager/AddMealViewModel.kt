package com.cuinsolutions.macrosmanager

import androidx.lifecycle.ViewModel
import kotlin.random.Random

class AddMealViewModel : ViewModel() {

    fun addMeal(mealName: String, mealServingSize: Double, mealCalories: Double, mealCarbs: Double, mealFats: Double,
                mealProtein: Double): Meal {

        val calories = mealCalories * mealServingSize
        val carbs = mealCarbs * mealServingSize
        val fats = mealFats * mealServingSize
        val protein = mealProtein * mealServingSize
        return Meal(Random.nextInt(), mealName, mealServingSize, calories, carbs, fats, protein)
    }

    fun editMeal(mealName: String, mealServingSize: Double, mealCalories: Double, mealCarbs: Double, mealFats: Double,
                 mealProtein: Double, previousMeal: Meal): Meal {
        val calories = mealCalories * mealServingSize
        val carbs = mealCarbs * mealServingSize
        val fats = mealFats * mealServingSize
        val protein = mealProtein * mealServingSize
        return previousMeal.copy(mealName =  mealName, servingSize = mealServingSize, mealCalories = calories, mealCarbs = carbs,
            mealFats = fats, mealProtein = protein)
    }
}