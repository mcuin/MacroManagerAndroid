package com.cuinsolutions.macrosmanager

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuinsolutions.macrosmanager.utils.Meal
import com.cuinsolutions.macrosmanager.utils.MealsRepository
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class AddMealViewModel @Inject constructor(val mealRepository: MealsRepository): ViewModel() {

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
    var mealNameEmptyError by mutableStateOf(false)
        private set
    var mealCaloriesEmptyError by mutableStateOf(false)
        private set
    var mealCarbsEmptyError by mutableStateOf(false)
        private set
    var mealFatsEmptyError by mutableStateOf(false)
        private set
    var mealProteinEmptyError by mutableStateOf(false)
        private set
    var mealServingsEmptyError by mutableStateOf(false)
        private set

    fun updateMealName(updatedMealName: String) {
        mealNameEmptyError = updatedMealName.isEmpty()
        mealName = updatedMealName
    }
    fun updateMealCalories(updatedMealCalories: String) {
        mealCaloriesEmptyError = updatedMealCalories.isEmpty()
        mealCalories = updatedMealCalories
    }
    fun updateMealCarbs(updatedMealCarbs: String) {
        mealCarbsEmptyError = updatedMealCarbs.isEmpty()
        mealCarbs = updatedMealCarbs
    }
    fun updateMealFats(updatedMealFats: String) {
        mealFatsEmptyError = updatedMealFats.isEmpty()
        mealFats = updatedMealFats
    }
    fun updateMealProtein(updatedMealProtein: String) {
        mealProteinEmptyError = updatedMealProtein.isEmpty()
        mealProtein = updatedMealProtein
    }
    fun updateMealServings(updatedMealServings: String) {
        mealServingsEmptyError = updatedMealServings.isEmpty()
        mealServings = updatedMealServings
    }

    fun loadMeal(mealId: Int) {
        viewModelScope.launch {
            val meal = mealRepository.getMealById(mealId)
            if (meal != null) {
                mealName = meal.mealName
                mealCalories = meal.mealCalories.toString()
                mealCarbs = meal.mealCarbs.toString()
                mealFats = meal.mealFats.toString()
                mealProtein = meal.mealProtein.toString()
                mealServings = meal.servingSize.toString()
            }
        }
    }

    fun validateMeal(): Boolean {
        when {
            mealName.isEmpty() -> {
                mealNameEmptyError = true
            }
            mealCalories.isEmpty() -> {
                mealCaloriesEmptyError = true
            }
            mealCarbs.isEmpty() -> {
                mealCarbsEmptyError = true
            }
            mealFats.isEmpty() -> {
                mealFatsEmptyError = true
            }
            mealProtein.isEmpty() -> {
                mealProteinEmptyError = true
            }
            mealServings.isEmpty() -> {
                mealServingsEmptyError = true
            }
        }

        return !(mealNameEmptyError || mealCaloriesEmptyError || mealCarbsEmptyError || mealFatsEmptyError || mealProteinEmptyError || mealServingsEmptyError)
    }

    fun saveMeal() {
        val meal = Meal(mealName = mealName, mealCalories = mealCalories.toDouble(),
            mealCarbs = mealCarbs.toDouble(), mealFats = mealFats.toDouble(),
            mealProtein = mealProtein.toDouble(), servingSize = mealServings.toDouble())

        viewModelScope.launch {
            mealRepository.addMeal(meal)
        }
    }

    fun deleteMeal(mealId: Int) {
        viewModelScope.launch {
            mealRepository.deleteMeal(mealId)
        }
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