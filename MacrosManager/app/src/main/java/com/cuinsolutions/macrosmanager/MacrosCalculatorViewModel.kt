package com.cuinsolutions.macrosmanager

import android.icu.util.Calendar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.util.Date

class MacrosCalculatorViewModel: ViewModel() {

    private val centimeterFactor = 30.48
    private val kilogramsFactor = 0.455
    val calcedMacros: MutableSharedFlow<Macros> = MutableSharedFlow()

    fun heightImperialToMetric(foot: Int, inches: Double) : Double {

        val totalInches = (foot * 12) + inches

        return totalInches * centimeterFactor
    }

    fun weightStoneToMetric(stone: Int, pounds: Double): Double {

        val totalPounds = (stone * 14) + pounds

        return totalPounds * kilogramsFactor
    }

    fun weightImperialToMetric(pounds: Double): Double {

        return pounds * kilogramsFactor
    }

    fun weightMetricToImperial(kg: Double): Double {

        return kg * 2.205
    }

    suspend fun calculate(cm: Double, kg: Double, settingsInfo: UserInfo, calculatorInfo: CalculatorOptions,
                  previousMacros: Macros) {

        var bmr = 0.0
        val pounds = weightMetricToImperial(kg)
        val calories: Double
        val proteinCalories: Double
        val fat: Double
        val carbs: Double
        val carbsCalories: Double
        var age = Calendar.YEAR - settingsInfo.birthYear
        if (settingsInfo.birthMonth < Calendar.MONTH) {
            age -= 1
        }
        //dateFormat.format(birthDate), java.util.concurrent.TimeUnit.MILLISECONDS) / 365)

        when (settingsInfo.gender) {
            "male" -> bmr = (10 * kg) + (6.25 * cm) - ((5 * age) + 5)
            "female" -> bmr = (10 * kg) + (6.25 * cm) - ((5 * age) - 161)
        }

        val tdee: Double = when (calculatorInfo.dailyActivity) {
            "very light" -> bmr * 1.20
            "light" -> bmr * 1.45
            "moderate" -> bmr * 1.55
            "heavy" -> bmr * 1.75
            "very heavy" -> 2.00
            else -> bmr
        }

        calories = when (calculatorInfo.goal) {
            "weightLossSuggested" -> tdee - (tdee * 0.15)
            "weightLossAggressive" -> tdee - (tdee * 0.20)
            "weightLossReckless" -> tdee - (tdee * 0.25)
            "maintain" -> tdee
            "bulkingSuggested" -> tdee + (tdee * 0.05)
            "bulkingAggressive" -> tdee + (tdee * 0.10)
            "bulkingReckless" -> tdee + (tdee * 0.15)
            else -> tdee
        }

        val protein: Double = when (calculatorInfo.physicalActivityLifestyle) {
            "sedentaryAdult" -> pounds * 0.4
            "recreationalExerciserAdult" -> pounds * 0.75
            "competitiveAthleteAdult" -> pounds * 0.90
            "buildingMuscleAdult" -> pounds * 0.90
            "dietingAthlete" -> pounds * 0.90
            "growingAthleteTeenager" -> pounds * 1.0
            else -> pounds * 0.4
        }

        proteinCalories = protein * 4

        val fatCalories: Double = calories * (calculatorInfo.dietFatPercent / 100)
        fat = fatCalories / 9

        carbsCalories = calories - (proteinCalories + fatCalories)
        carbs = carbsCalories / 4

        val macrosCopy = previousMacros.copy()
        macrosCopy.dailyCalories = calories.toInt()
        macrosCopy.dailyCarbs = carbs.toInt()
        macrosCopy.dailyFats = fat.toInt()
        macrosCopy.dailyProtein = protein.toInt()

        calcedMacros.emit(macrosCopy)
    }
}