package com.cuinsolutions.macrosmanager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuinsolutions.macrosmanager.utils.Macro
import com.cuinsolutions.macrosmanager.utils.MacrosRepository
import com.cuinsolutions.macrosmanager.utils.Meal
import com.cuinsolutions.macrosmanager.utils.MealsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DailyInfoViewModel @Inject constructor(macrosRepository: MacrosRepository, mealsRepository: MealsRepository) : ViewModel() {

    var mealsList: MutableStateFlow<List<Meal>> = MutableStateFlow(emptyList())
    private var currentCalories = 0.0
    private var currentProtein = 0.0
    private var currentFat = 0.0
    private var currentCarbs = 0.0

    val states = macrosRepository.macros.combine(mealsRepository.meals) { macros, meals ->
        if (!meals.isNullOrEmpty()) {
            for (meal in meals) {
                currentCalories += meal.mealCalories * meal.servingSize
                currentProtein += meal.mealProtein * meal.servingSize
                currentFat += meal.mealFats * meal.servingSize
                currentCarbs += meal.mealCarbs * meal.servingSize
            }
            mealsList.emit(meals)
        } else {
            currentCalories = 0.0
            currentProtein = 0.0
            currentFat = 0.0
            currentCarbs = 0.0
            mealsList.emit(emptyList())
        }
        if (macros != null) {
            val mutableMacros = macros.macros.toMutableList()
            mutableMacros.add(Macro("calories", currentCalories, macros.macros.find { it.name == "calories" }?.daily ?: 0))
            mutableMacros.add(Macro("protein", currentProtein, macros.macros.find { it.name == "protein" }?.daily ?: 0))
            mutableMacros.add(Macro("fat", currentFat, macros.macros.find { it.name == "fat" }?.daily ?: 0))
            mutableMacros.add(Macro("carbs", currentCarbs, macros.macros.find { it.name == "carbs" }?.daily ?: 0))
            DailyInfoUiState.Success(mutableMacros.toList())
        } else {
            DailyInfoUiState.Success(listOf(Macro("calories", currentCalories, 0), Macro("protein", currentProtein, 0), Macro("fat", currentFat, 0), Macro("carbs", currentCarbs, 0)))
        }}.stateIn(viewModelScope, SharingStarted.Eagerly, DailyInfoUiState.Loading)
}

sealed interface DailyInfoUiState {
    object Loading : DailyInfoUiState
    data class Success(val macros: List<Macro>) : DailyInfoUiState
}