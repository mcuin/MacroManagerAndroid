package com.cuinsolutions.macrosmanager.utils

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf

data class UserInfo(var email: String = "", var showAds: Boolean = true,
                    var gender: Int = Gender.MALE.id, var birthYear: Int = -1, var birthMonth: Int = -1,
                    var heightMeasurement: Int = HeightMeasurement.METRIC.id,
                    var weightMeasurement: Int = WeightMeasurement.METRIC.id,
                    var heightCm: Float = -1f, var weightKg: Float = -1f)

data class Macros(val macros: List<Macro>)

data class Macro(val name: String, val current: Double, val daily: Int)

data class CalculatorOptions(var dailyActivity: Int = DailyActivityLevel.VERYLIGHT.id, var goal: Int = Goal.MAINTAIN.id,
                             var physicalActivityLifestyle: Int = PhysicalActivityLifestyle.SEDENTARYADULT.id, var dietFatPercent: Double = 25.0)

@Immutable
data class CalculatorOptionsState(var dailyActivity: Int = DailyActivityLevel.VERYLIGHT.id,
                                  var physicalActivityLifestyle: Int = PhysicalActivityLifestyle.SEDENTARYADULT.id,
                                  var dietFatPercent: Double = 25.0,
                                  var goal: Int = Goal.MAINTAIN.id)

data class MacroCell(var macroName: String, var macrosDescription: String)

data class Meal(var id: Int, var mealName: String, val servingSize: Double, var mealCalories: Double, var mealCarbs: Double,
                var mealFats: Double, var mealProtein: Double)

enum class DailyActivityLevel(val title: String, val id: Int) {
    VERYLIGHT("Very Light", 0),
    LIGHT("Light", 1),
    MODERATE("Moderate", 2),
    HEAVY("Heavy", 3),
    VERYHEAVY("Very Heavy", 4)
}

enum class Goal(val title: String, val id: Int) {
    MAINTAIN("Maintain (+/- 0%)", 0),
    BUILDSUGGESTED("Build Suggested (+5%)", 1),
    BUILDAGGRESSIVE("Build Aggressive (+10%)", 2),
    BUILDRECKLESS("Build Reckless (+15%)", 3),
    BURNSUGGESTED("Burn Suggested (-15%)", 4),
    BURNAGGRESSIVE("Burn Aggressive (-20%)", 5),
    BURNRECKLESS("Burn Reckless (-25%)", 6)
}

enum class PhysicalActivityLifestyle(val title: String, val id: Int) {
    SEDENTARYADULT("Sedentary Adult", 0),
    RECREATIONADULT("Adult Recreation Exerciser", 1),
    COMPETITIVEADULT("Adult Competitive Athlete", 2),
    BUILDINGADULT("Adult Building Muscle", 3),
    DIETINGATHLETE("Dieting Athlete", 4)
}

enum class Gender(val title: String, val id: Int) {
    FEMALE("Female", 0),
    MALE("Male", 1)
}

enum class HeightMeasurement(val title: String, val id: Int) {
    IMPERIAL("Imperial", 0),
    METRIC("Metric", 1)
}

enum class WeightMeasurement(val title: String, val id: Int) {
    IMPERIAL("Imperial", 0),
    METRIC("Metric", 1),
    STONE("Stone", 2)
}

enum class DietFatPercent(val title: String, val id: Int) {
    TwentyFivePercent("25%", 0),
    ThirtyPercent("30%", 1),
    ThirtyFivePercent("35%", 2),
}