package com.cuinsolutions.macrosmanager

import android.icu.util.Calendar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.util.Date
import kotlin.math.floor

class MacrosCalculatorViewModel: ViewModel() {

    private val centimeterFactor = 2.54
    private val kilogramsFactor = 2.205
    val calcedMacros: MutableSharedFlow<Macros> = MutableSharedFlow()

    fun heightImperialToMetric(foot: Int, inches: Double) : Float {

        val totalInches = (foot * 12) + inches

        return (totalInches * centimeterFactor).toFloat()
    }

    fun heightMetricToImperial(cm: Float): ImperialHeight {

        val totalInches = cm / centimeterFactor
        val feet = totalInches / 12
        val inches = totalInches % 12

        return ImperialHeight(floor(feet).toInt(), inches)
    }

    fun weightStoneToMetric(stone: Int, pounds: Double): Float {

        val totalPounds = (stone * 14) + pounds

        return (totalPounds * kilogramsFactor).toFloat()
    }

    fun weightImperialToMetric(pounds: Double): Float {

        return (pounds / kilogramsFactor).toFloat()
    }

    fun weightMetricToImperial(kg: Float): Float {

        return (kg * kilogramsFactor).toFloat()
    }

    fun weightMetricToStone(kg: Float): StoneWeight {

        val totalPounds = kg * kilogramsFactor
        val stone = totalPounds / 14
        val pounds = totalPounds % 14

        return StoneWeight(floor(stone).toInt(), pounds)
    }

    suspend fun calculate(cm: Float, kg: Float, settingsInfo: UserInfo, calculatorInfo: CalculatorOptions,
                  previousMacros: Macros) {

        var bmr = 0.0
        val pounds = weightMetricToImperial(kg)
        val calories: Double
        val proteinCalories: Double
        val fat: Double
        val carbs: Double
        val carbsCalories: Double
        val calendar = Calendar.getInstance()
        var age = calendar.get(Calendar.YEAR) - settingsInfo.birthYear
        if (settingsInfo.birthMonth < calendar.get(Calendar.MONTH)) {
            age -= 1
        }
        //dateFormat.format(birthDate), java.util.concurrent.TimeUnit.MILLISECONDS) / 365)

        when (settingsInfo.gender) {
            Gender.MALE.gender -> bmr = (10 * kg) + (6.25 * cm) - ((5 * age) + 5)
            Gender.FEMALE.gender -> bmr = (10 * kg) + (6.25 * cm) - ((5 * age) - 161)
        }

        val tdee: Double = when (calculatorInfo.dailyActivity) {
            DailyActivityLevel.VERYLIGHT.level -> bmr * 1.20
            DailyActivityLevel.LIGHT.level -> bmr * 1.45
            DailyActivityLevel.MODERATE.level -> bmr * 1.55
            DailyActivityLevel.HEAVY.level -> bmr * 1.75
            DailyActivityLevel.VERYHEAVY.level -> 2.00
            else -> bmr
        }

        calories = when (calculatorInfo.goal) {
            Goal.BURNSUGGESTED.goal -> tdee - (tdee * 0.15)
            Goal.BURNAGGRESSIVE.goal-> tdee - (tdee * 0.20)
            Goal.BURNRECKLESS.goal -> tdee - (tdee * 0.25)
            Goal.MAINTAIN.goal -> tdee
            Goal.BUILDSUGGESTED.goal -> tdee + (tdee * 0.05)
            Goal.BUILDAGGRESSIVE.goal -> tdee + (tdee * 0.10)
            Goal.BUILDRECKLESS.goal -> tdee + (tdee * 0.15)
            else -> tdee
        }

        val protein: Double = when (calculatorInfo.physicalActivityLifestyle) {
            PhysicalActivityLifestyle.SEDENTARYADULT.lifeStyle -> pounds * 0.4
            PhysicalActivityLifestyle.RECREATIONADULT.lifeStyle -> pounds * 0.75
            PhysicalActivityLifestyle.COMPETITIVEADULT.lifeStyle -> pounds * 0.90
            PhysicalActivityLifestyle.BUILDINGADULT.lifeStyle -> pounds * 0.90
            PhysicalActivityLifestyle.DIETINGATHLETE.lifeStyle -> pounds * 0.90
            PhysicalActivityLifestyle.GROWINGTEENAGER.lifeStyle -> pounds * 1.0
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

data class ImperialHeight(var feet: Int, var inches: Double)
data class StoneWeight(var stone: Int, val pounds: Double)