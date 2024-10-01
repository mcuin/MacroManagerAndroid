package com.cuinsolutions.macrosmanager

import android.icu.util.Calendar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.cuinsolutions.macrosmanager.utils.CalculatorOptions
import com.cuinsolutions.macrosmanager.utils.CalculatorOptionsState
import com.cuinsolutions.macrosmanager.utils.DailyActivityLevel
import com.cuinsolutions.macrosmanager.utils.Gender
import com.cuinsolutions.macrosmanager.utils.Goal
import com.cuinsolutions.macrosmanager.utils.Macros
import com.cuinsolutions.macrosmanager.utils.PhysicalActivityLifestyle
import com.cuinsolutions.macrosmanager.utils.UserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import kotlin.math.floor

@HiltViewModel
class MacrosCalculatorViewModel @Inject constructor() : ViewModel() {

    private val centimeterFactor = 2.54
    private val kilogramsFactor = 2.205
    val calcedMacros: MutableSharedFlow<Macros> = MutableSharedFlow()
    /*private val _currentUserInfo = MutableStateFlow(UserInfo())
    val currentUserInfo = _currentUserInfo.asStateFlow()*/
    var currentUserInfo by mutableStateOf(UserInfo())
    var currentCalculatorOptions by mutableStateOf(CalculatorOptionsState())
    var userCm by mutableStateOf("")
    var userPounds by mutableStateOf("")
    var userFeet by mutableStateOf("")
    var userInches by mutableStateOf("")
    var userStone by mutableStateOf("")
    var userKg by mutableStateOf("")

    fun updateUserKg(updatedKg: String) {
        currentUserInfo.weightKg = updatedKg.toFloat()
    }

    fun updateUserCm(updatedCm: String) {
        userCm = updatedCm
    }

    fun updateUserPounds(updatedPounds: String) {
        userPounds = updatedPounds
    }

    fun updateUserFeet(updatedFeet: String) {
        userFeet = updatedFeet
    }

    fun updateUserInches(updatedInches: String) {
        userInches = updatedInches
    }

    fun updateUserStone(updatedStone: String) {
        userStone = updatedStone
    }

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
                          previousMacros: Macros
    ) {

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
            Gender.MALE.id -> bmr = (10 * kg) + (6.25 * cm) - ((5 * age) + 5)
            Gender.FEMALE.id -> bmr = (10 * kg) + (6.25 * cm) - ((5 * age) - 161)
        }

        val tdee: Double = when (calculatorInfo.dailyActivity) {
            DailyActivityLevel.VERYLIGHT.id -> bmr * 1.20
            DailyActivityLevel.LIGHT.id -> bmr * 1.45
            DailyActivityLevel.MODERATE.id -> bmr * 1.55
            DailyActivityLevel.HEAVY.id -> bmr * 1.75
            DailyActivityLevel.VERYHEAVY.id -> 2.00
            else -> bmr
        }

        calories = when (calculatorInfo.goal) {
            Goal.BURNSUGGESTED.id -> tdee - (tdee * 0.15)
            Goal.BURNAGGRESSIVE.id-> tdee - (tdee * 0.20)
            Goal.BURNRECKLESS.id -> tdee - (tdee * 0.25)
            Goal.MAINTAIN.id -> tdee
            Goal.BUILDSUGGESTED.id -> tdee + (tdee * 0.05)
            Goal.BUILDAGGRESSIVE.id -> tdee + (tdee * 0.10)
            Goal.BUILDRECKLESS.id -> tdee + (tdee * 0.15)
            else -> tdee
        }

        val protein: Double = when (calculatorInfo.physicalActivityLifestyle) {
            PhysicalActivityLifestyle.SEDENTARYADULT.id -> pounds * 0.4
            PhysicalActivityLifestyle.RECREATIONADULT.id -> pounds * 0.75
            PhysicalActivityLifestyle.COMPETITIVEADULT.id -> pounds * 0.90
            PhysicalActivityLifestyle.BUILDINGADULT.id -> pounds * 0.90
            PhysicalActivityLifestyle.DIETINGATHLETE.id -> pounds * 0.90
            else -> pounds * 0.4
        }

        proteinCalories = protein * 4

        val fatCalories: Double = calories * (calculatorInfo.dietFatPercent / 100)
        fat = fatCalories / 9

        carbsCalories = calories - (proteinCalories + fatCalories)
        carbs = carbsCalories / 4

        val macrosCopy = previousMacros.copy()

        calcedMacros.emit(macrosCopy)
    }
}

data class ImperialHeight(var feet: Int, var inches: Double)
data class StoneWeight(var stone: Int, val pounds: Double)