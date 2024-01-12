package com.cuinsolutions.macrosmanager

data class UserInfo(var email: String = "", var showAds: Boolean = true,
                    var gender: String = Gender.MALE.gender, var birthYear: Int = -1, var birthMonth: Int = -1,
                    var heightMeasurement: String = HeightMeasurement.METRIC.measurement,
                    var weightMeasurement: String = WeightMeasurement.METRIC.measurement,
                    var heightCm: Float = -1f, var weightKg: Float = -1f)

data class Macros(var dailyCalories: Int = -1, var dailyCarbs: Int = -1, var dailyFats: Int = -1, var dailyProtein: Int = -1,
                  var currentCalories: Float = -1f, var currentCarbs: Float = -1f, var currentFats: Float = -1f,
                  var currentProtein: Float = -1f)

data class CalculatorOptions(var dailyActivity: String = DailyActivityLevel.VERYLIGHT.level, var goal: String = Goal.MAINTAIN.goal,
                             var physicalActivityLifestyle: String = PhysicalActivityLifestyle.SEDENTARYADULT.lifeStyle, var dietFatPercent: Double = 25.0)

data class MacroCell(var macroName: String, var macrosDescription: String)

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