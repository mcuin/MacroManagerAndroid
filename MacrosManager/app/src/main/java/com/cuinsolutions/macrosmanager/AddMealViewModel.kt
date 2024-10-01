package com.cuinsolutions.macrosmanager

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.cuinsolutions.macrosmanager.utils.Meal
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class AddMealViewModel @Inject constructor(): ViewModel() {

    var mealName by mutableStateOf("")
        private set
    var mealCalories by mutableStateOf("")
        private set
    var mealCarbs by mutableStateOf("")
        private set
    var mealFats by mutableStateOf("")
        private set
    var mealProtein by mutableStateOf("")
        private set
    var mealServings by mutableStateOf("")
        private set

    fun updateMealName(updatedMealName: String) {
        mealName = updatedMealName
    }
    fun updateMealCalories(updatedMealCalories: String) {
        mealCalories = updatedMealCalories
    }
    fun updateMealCarbs(updatedMealCarbs: String) {
        mealCarbs = updatedMealCarbs
    }
    fun updateMealFats(updatedMealFats: String) {
        mealFats = updatedMealFats
    }
    fun updateMealProtein(updatedMealProtein: String) {
        mealProtein = updatedMealProtein
    }
    fun updateMealServings(updatedMealServings: String) {
        mealServings = updatedMealServings
    }


    fun addMeal(mealName: String, mealServingSize: Double, mealCalories: Double, mealCarbs: Double, mealFats: Double,
                mealProtein: Double): Meal {

        val calories = mealCalories * mealServingSize
        val carbs = mealCarbs * mealServingSize
        val fats = mealFats * mealServingSize
        val protein = mealProtein * mealServingSize
        return Meal(Random.nextInt(), mealName, mealServingSize, calories, carbs, fats, protein)
    }

    fun editMeal(mealName: String, mealServingSize: Double, mealCalories: Double, mealCarbs: Double, mealFats: Double,
                 mealProtein: Double, previousMeal: Meal
    ): Meal {
        val calories = mealCalories * mealServingSize
        val carbs = mealCarbs * mealServingSize
        val fats = mealFats * mealServingSize
        val protein = mealProtein * mealServingSize
        return previousMeal.copy(mealName =  mealName, servingSize = mealServingSize, mealCalories = calories, mealCarbs = carbs,
            mealFats = fats, mealProtein = protein)
    }
}