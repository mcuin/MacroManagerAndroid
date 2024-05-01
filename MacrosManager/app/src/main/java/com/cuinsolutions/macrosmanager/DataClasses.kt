package com.cuinsolutions.macrosmanager

data class UserInfo(var email: String = "", var showAds: Boolean = true,
                    var gender: String = Gender.MALE.gender, var birthYear: Int = -1, var birthMonth: Int = -1,
                    var heightMeasurement: String = HeightMeasurement.METRIC.measurement,
                    var weightMeasurement: String = WeightMeasurement.METRIC.measurement,
                    var heightCm: Float = -1f, var weightKg: Float = -1f)

data class Macros(var dailyCalories: Int = 0, var dailyCarbs: Int = 0, var dailyFats: Int = 0, var dailyProtein: Int = 0,
                  var currentCalories: Float = 0f, var currentCarbs: Float = 0f, var currentFats: Float = 0f,
                  var currentProtein: Float = 0f)

data class CalculatorOptions(var dailyActivity: String = DailyActivityLevel.VERYLIGHT.level, var goal: String = Goal.MAINTAIN.goal,
                             var physicalActivityLifestyle: String = PhysicalActivityLifestyle.SEDENTARYADULT.lifeStyle, var dietFatPercent: Double = 25.0)

data class MacroCell(var macroName: String, var macrosDescription: String)

data class Meal(var id: Int, var mealName: String, val servingSize: Double, var mealCalories: Double, var mealCarbs: Double,
                var mealFats: Double, var mealProtein: Double)

enum class DailyActivityLevel(val level: String) {
    VERYLIGHT("veryLight"),
    LIGHT("light"),
    MODERATE("moderate"),
    HEAVY("heavy"),
    VERYHEAVY("veryHeavy")
}

enum class Goal(val goal: String) {
    BURNRECKLESS("burnReckless"),
    BURNAGGRESSIVE("burnAggressive"),
    BURNSUGGESTED("burnSuggested"),
    MAINTAIN("maintain"),
    BUILDSUGGESTED("buildSuggested"),
    BUILDAGGRESSIVE("buildAggressive"),
    BUILDRECKLESS("buildReckless")
}

enum class PhysicalActivityLifestyle(val lifeStyle: String) {
    SEDENTARYADULT("sedentaryAdult"),
    RECREATIONADULT("recreationExerciserAdult"),
    COMPETITIVEADULT("competitiveAdult"),
    BUILDINGADULT("buildingAdult"),
    DIETINGATHLETE("dietingAthlete"),
    GROWINGTEENAGER("growingTeenager")
}

enum class Gender(val gender: String) {
    FEMALE("female"),
    MALE("male")
}

enum class HeightMeasurement(val measurement: String) {
    IMPERIAL("imperial"),
    METRIC("metric")
}

enum class WeightMeasurement(val measurement: String) {
    IMPERIAL("imperial"),
    METRIC("metric"),
    STONE("stone")
}