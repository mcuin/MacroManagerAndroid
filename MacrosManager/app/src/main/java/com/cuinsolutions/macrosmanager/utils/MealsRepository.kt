package com.cuinsolutions.macrosmanager.utils

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MealsRepository @Inject constructor(val mealsDao: MealsDao) {

    val meals: Flow<List<Meal>?> = mealsDao.getMeals()

    suspend fun addMeal(meal: Meal) {
        mealsDao.insert(meal)
    }

    suspend fun deleteMeal(mealId: Int) {
        mealsDao.deleteMealById(mealId)
    }

    suspend fun deleteAllMeals() {
        mealsDao.deleteAllMeals()
    }

    suspend fun getMealById(id: Int): Meal {
        return mealsDao.getMealById(id)
    }
}