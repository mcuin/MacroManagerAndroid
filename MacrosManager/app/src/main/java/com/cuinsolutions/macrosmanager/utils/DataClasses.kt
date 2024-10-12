package com.cuinsolutions.macrosmanager.utils

import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

data class MacrosManagerPreferences(val currentDate: LocalDate)

@Immutable
@Entity(tableName = "user_info")
data class UserInfo(@PrimaryKey(autoGenerate = true) var id: Int = -1,
                    @ColumnInfo(name = "name") var name: String = "",
                    @ColumnInfo(name = "email") var email: String = "",
                    @ColumnInfo(name = "showAds") var showAds: Boolean = true,
                    @ColumnInfo(name = "gender") var gender: Int = Gender.MALE.id,
                    @ColumnInfo(name = "birthYear") var birthYear: Int = -1,
                    @ColumnInfo(name = "birthMonth") var birthMonth: Int = -1,
                    @ColumnInfo(name = "heightMeasurement") var heightMeasurement: Int = HeightMeasurement.METRIC.id,
                    @ColumnInfo(name = "weightMeasurement") var weightMeasurement: Int = WeightMeasurement.METRIC.id,
                    @ColumnInfo(name = "heightCm") var heightCm: Double = 0.0,
                    @ColumnInfo(name = "weightKg") var weightKg: Double = 0.0)

@Entity(tableName = "macros")
data class Macros(@PrimaryKey(autoGenerate = true) var id: Int = -1,
                  @ColumnInfo(name = "macros") var macros: List<Macro>)

data class Macro(val name: String, val current: Double, val daily: Int)

@Immutable
@Entity(tableName = "calculator_options")
data class CalculatorOptions(@PrimaryKey(autoGenerate = true) var id: Int = -1,
                             @ColumnInfo(name = "dailyActivity") var dailyActivity: Int = DailyActivityLevel.VERYLIGHT.id,
                             @ColumnInfo(name = "goal") var goal: Int = Goal.MAINTAIN.id,
                             @ColumnInfo(name = "physicalActivityLifestyle") var physicalActivityLifestyle: Int = PhysicalActivityLifestyle.SEDENTARYADULT.id,
                             @ColumnInfo(name = "dietFatPercentId") var dietFatPercentId: Int =  0,
                             @ColumnInfo(name = "dietFatPercent") var dietFatPercent: Double = 25.0)

data class MacroCell(var macroName: String, var macrosDescription: String)

@Entity(tableName = "meals")
data class Meal(@PrimaryKey(autoGenerate = true) var id: Int = 0,
                @ColumnInfo(name = "mealName") var mealName: String,
                @ColumnInfo(name = "servingSize") val servingSize: Double,
                @ColumnInfo(name = "mealCalories") var mealCalories: Int,
                @ColumnInfo(name = "mealCarbs") var mealCarbs: Int,
                @ColumnInfo(name = "mealFats") var mealFats: Int,
                @ColumnInfo(name = "mealProtein") var mealProtein: Int)

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

enum class DietFatPercent(val title: String, val id: Int, val percent: Double) {
    TwentyFivePercent("25%", 0, 25.0),
    ThirtyPercent("30%", 1, 30.0),
    ThirtyFivePercent("35%", 2, 35.0)
}