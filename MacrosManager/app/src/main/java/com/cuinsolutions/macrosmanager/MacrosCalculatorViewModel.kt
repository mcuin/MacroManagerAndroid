package com.cuinsolutions.macrosmanager

import android.icu.text.DecimalFormat
import android.icu.util.Calendar
import android.util.Range
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuinsolutions.macrosmanager.utils.CalculatorOptions
import com.cuinsolutions.macrosmanager.utils.CalculatorOptionsRepository
import com.cuinsolutions.macrosmanager.utils.DailyActivityLevel
import com.cuinsolutions.macrosmanager.utils.Gender
import com.cuinsolutions.macrosmanager.utils.Goal
import com.cuinsolutions.macrosmanager.utils.HeightMeasurement
import com.cuinsolutions.macrosmanager.utils.Macro
import com.cuinsolutions.macrosmanager.utils.Macros
import com.cuinsolutions.macrosmanager.utils.MacrosRepository
import com.cuinsolutions.macrosmanager.utils.PhysicalActivityLifestyle
import com.cuinsolutions.macrosmanager.utils.UserInfo
import com.cuinsolutions.macrosmanager.utils.UserInfoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.floor

@HiltViewModel
class MacrosCalculatorViewModel @Inject constructor(private val userInfoRepository: UserInfoRepository,
                                                    private val calculatorOptionsRepository: CalculatorOptionsRepository,
                                                    private val macrosRepository: MacrosRepository) : ViewModel() {

    private val centimeterFactor = 2.54
    private val kilogramsFactor = 2.205
    private lateinit var currentMacros: List<Macro>
    var userCm by mutableStateOf("")
    var userPounds by mutableStateOf("")
    var userFeet by mutableStateOf("")
    var userInches by mutableStateOf("")
    var userStone by mutableStateOf("")
    var userKg by mutableStateOf("")
    var userDietFatPercent by mutableStateOf("")
    var currentUserInfo by mutableStateOf(UserInfo())
    var currentCalculatorOptions by mutableStateOf(CalculatorOptions())
    val states = combine(userInfoRepository.userInfo, calculatorOptionsRepository.calculatorOptions, macrosRepository.macros) { userInfo, calculatorOptions, macros ->
        if (userInfo != null) {
            if (userInfo.birthMonth < 0 ||
                userInfo.birthYear < 0 ||
                userInfo.gender < 0 ||
                userInfo.heightMeasurement < 0 ||
                userInfo.weightMeasurement < 0) {
                CalculatorState.SettingsError
            } else {
                currentUserInfo = userInfo
                if (calculatorOptions != null) {
                    currentCalculatorOptions = calculatorOptions
                    if (calculatorOptions.dietFatPercentId == 3) {
                        userDietFatPercent = validDietFatPercent(calculatorOptions.dietFatPercent.toString())
                    }
                }
                if (macros != null) {
                    currentMacros = macros.macros
                } else {
                    currentMacros = listOf(Macro("calories", 0.0, 0), Macro("protein", 0.0, 0), Macro("fat", 0.0, 0), Macro("carbs", 0.0, 0))
                }
                if (userInfo.heightMeasurement == HeightMeasurement.METRIC.id) {
                    userCm = validHeightCm(userInfo.heightCm.toString())
                } else {
                    val imperialHeight = heightMetricToImperial(userInfo.heightCm)
                    userFeet = validHeightFeet(imperialHeight.feet.toString())
                    userInches = validHeightInches(imperialHeight.inches.toString())
                }
                when (userInfo.weightMeasurement) {
                    HeightMeasurement.METRIC.id -> {
                        userKg = validWeightKg(userInfo.weightKg.toString())
                    }
                    HeightMeasurement.IMPERIAL.id -> {
                        userPounds = validWeightPounds(weightMetricToImperial(userInfo.weightKg).toString())
                    }
                    else -> {
                        val stoneWeight = weightMetricToStone(userInfo.weightKg)
                        userStone = validWeightStone(stoneWeight.stone.toString())
                        userPounds = validWeightPounds(stoneWeight.pounds.toString())
                    }
                }
                CalculatorState.Success(userInfo)
            }
        } else {
            CalculatorState.SettingsError
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), CalculatorState.Loading)
    val calcedMacros: MutableSharedFlow<Macros> = MutableSharedFlow()

    var calculatorOptionsCustomDietFatPercent by mutableStateOf(currentCalculatorOptions.dietFatPercent.toString())

    fun updateCalculatorOptionDailyActivity(updatedDailyActivity: Int) {
        currentCalculatorOptions = currentCalculatorOptions.copy(dailyActivity = updatedDailyActivity)
    }
    fun updateCalculatorOptionGoal(updatedGoal: Int) {
        currentCalculatorOptions = currentCalculatorOptions.copy(goal = updatedGoal)
    }
    fun updateCalculatorOptionDietFatId(updatedDietFatPercentId: Int, updatedDietFatPercent: Double) {
        currentCalculatorOptions = currentCalculatorOptions.copy(dietFatPercentId = updatedDietFatPercentId, dietFatPercent = updatedDietFatPercent)
    }
    fun updateCalculatorOptionPhysicalActivityLifestyle(updatedPhysicalActivityLifestyle: Int) {
        currentCalculatorOptions = currentCalculatorOptions.copy(physicalActivityLifestyle = updatedPhysicalActivityLifestyle)
    }
    fun updateCustomDietFatPercent(updatedCustomDietFatPercent: String) {
        calculatorOptionsCustomDietFatPercent = updatedCustomDietFatPercent
        if (updatedCustomDietFatPercent.isNotEmpty() && updatedCustomDietFatPercent.toDouble() in 20.0..35.0) {
            currentCalculatorOptions = currentCalculatorOptions.copy(dietFatPercent = updatedCustomDietFatPercent.toDouble())
        }
    }

    fun updateUserKg(updatedKg: String) {
        userKg = updatedKg
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

    fun updateDietFatPercent(updatedDietFatPercent: String) {
        userDietFatPercent = updatedDietFatPercent
    }

    fun validHeightCm(updatedCm: String): String {
        if (updatedCm.isEmpty()) {
            return ""
        } else {
            val filteredCm = updatedCm.filterIndexed { index, c ->
                c.isDigit() || (c == '.' && index != 0 && updatedCm.indexOf('.') == index) }

            return if (filteredCm.count { it == '.' } == 1) {
                val wholeNumber = filteredCm.substringBefore('.')
                val decimal = filteredCm.substringAfter('.')
                wholeNumber.take(3) + "." + decimal.take(2)
            } else {
                filteredCm.take(3)
            }
        }
    }

    fun validHeightFeet(updatedFeet: String): String {
        return if (updatedFeet.isEmpty()) {
            ""
        } else {
            updatedFeet.take(1)
        }
    }

    fun validHeightInches(updatedInches: String): String {
        if (updatedInches.isEmpty()) {
            return ""
        } else {
            val filteredInches = updatedInches.filterIndexed { index, c ->
                c.isDigit() || (c == '.' && index != 0 && updatedInches.indexOf('.') == index)
                        || (c == '-' && index != 0 && updatedInches.count { it == '.' } <= 1)}

            return if (filteredInches.count { it == '.' } == 1) {
                val wholeNumber = filteredInches.substringBefore('.')
                val decimal = filteredInches.substringAfter('.')
                wholeNumber.take(2) + "." + decimal.take(2)
            } else {
                filteredInches.take(2)
            }
        }
    }

    fun validWeightKg(updatedKg: String): String {
        if (updatedKg.isEmpty()) {
            return ""
        } else {
            val filteredKg = updatedKg.filterIndexed { index, c ->
                c.isDigit() || (c == '.' && index != 0 && updatedKg.indexOf('.') == index)
                        || (c == '-' && index != 0 && updatedKg.count { it == '.' } <= 1)

            }

            return if (filteredKg.count { it == '.' } == 1) {
                val wholeNumber = filteredKg.substringBefore('.')
                val decimal = filteredKg.substringAfter('.')
                wholeNumber.take(3) + "." + decimal.take(2)
            } else {
                filteredKg.take(3)
            }
        }
    }

    fun validWeightPounds(updatedPounds: String): String {
        if (updatedPounds.isEmpty()) {
            return ""
        } else {
            val filteredPounds = updatedPounds.filterIndexed { index, c ->
                c.isDigit() || (c == '.' && index != 0 && updatedPounds.indexOf('.') == index)
                        || (c == '-' && index != 0 && updatedPounds.count { it == '.' } <= 1) }

            return if (filteredPounds.count { it == '.' } == 1) {
                val wholeNumber = filteredPounds.substringBefore('.')
                val decimal = filteredPounds.substringAfter('.')
                wholeNumber.take(4) + "." + decimal.take(2)
            } else {
                filteredPounds.take(4)
            }
        }
    }

    fun validWeightStone(updatedStone: String): String {
        return if (updatedStone.isEmpty()) {
            ""
        } else {
            updatedStone.take(3)
        }
    }

    fun validDietFatPercent(updatedDietFatPercent: String): String {
        return if (updatedDietFatPercent.isEmpty()) {
            ""
        } else {
            val filteredPounds = updatedDietFatPercent.filterIndexed { index, c ->
                c.isDigit() || (c == '.' && index != 0 && updatedDietFatPercent.indexOf('.') == index)
                        || (c == '-' && index != 0 && updatedDietFatPercent.count { it == '.' } <= 1) }

            return if (filteredPounds.count { it == '.' } == 1) {
                val wholeNumber = filteredPounds.substringBefore('.')
                val decimal = filteredPounds.substringAfter('.')
                wholeNumber.take(2) + "." + decimal.take(2)
            } else {
                filteredPounds.take(2)
            }
        }
    }

    fun heightImperialToMetric(foot: Int, inches: Double) : Double {

        val totalInches = (foot * 12) + inches

        return (totalInches * centimeterFactor)
    }

    fun heightMetricToImperial(cm: Double): ImperialHeight {

        val totalInches = cm / centimeterFactor
        val feet = totalInches / 12
        val inches = totalInches % 12

        return ImperialHeight(floor(feet).toInt(), inches)
    }

    fun weightStoneToMetric(stone: Int, pounds: Double): Double {

        val totalPounds = (stone * 14) + pounds

        return (totalPounds * kilogramsFactor)
    }

    fun weightStoneToImperial(stone: Int, pounds: Double): Double {
        return (stone * 14) + pounds
    }

    fun weightImperialToMetric(pounds: Double): Double {

        return (pounds / kilogramsFactor)
    }

    fun weightMetricToImperial(kg: Double): Double {

        return (kg * kilogramsFactor)
    }

    fun weightMetricToStone(kg: Double): StoneWeight {

        val totalPounds = kg * kilogramsFactor
        val stone = totalPounds / 14
        val pounds = totalPounds % 14

        return StoneWeight(floor(stone).toInt(), pounds)
    }

    fun calculate() {

        val updatedMacros = mutableListOf<Macro>()
        val kg = when (currentUserInfo.weightMeasurement) {
            HeightMeasurement.METRIC.id -> userKg.toDouble()
            HeightMeasurement.IMPERIAL.id -> weightImperialToMetric(userPounds.toDouble())
            else -> weightStoneToMetric(userStone.toInt(), userPounds.toDouble())
        }
        val cm = when (currentUserInfo.heightMeasurement) {
            HeightMeasurement.METRIC.id -> userCm.toDouble()
            HeightMeasurement.IMPERIAL.id -> heightImperialToMetric(userFeet.toInt(), userInches.toDouble())
            else -> userCm.toDouble()
        }

        currentUserInfo.weightKg = kg
        currentUserInfo.heightCm = cm

        val pounds = when (currentUserInfo.weightMeasurement) {
            HeightMeasurement.METRIC.id -> weightMetricToImperial(userKg.toDouble())
            HeightMeasurement.IMPERIAL.id -> userPounds.toDouble()
            else -> weightStoneToImperial(userStone.toInt(), userPounds.toDouble())
        }
        val calories: Double
        val proteinCalories: Double
        val fat: Double
        val carbs: Double
        val carbsCalories: Double
        val calendar = Calendar.getInstance()
        var age = calendar.get(Calendar.YEAR) - currentUserInfo.birthYear
        if (currentUserInfo.birthMonth < calendar.get(Calendar.MONTH)) {
            age -= 1
        }
        //dateFormat.format(birthDate), java.util.concurrent.TimeUnit.MILLISECONDS) / 365)

        val bmr = when (currentUserInfo.gender) {
            Gender.MALE.id -> (10 * kg) + (6.25 * cm) - ((5 * age) + 5)
            Gender.FEMALE.id -> (10 * kg) + (6.25 * cm) - ((5 * age) - 161)
            else -> (10 * kg) + (6.25 * cm) - ((5 * age) + 5)
        }

        val tdee: Double = when (currentCalculatorOptions.dailyActivity) {
            DailyActivityLevel.VERYLIGHT.id -> bmr * 1.20
            DailyActivityLevel.LIGHT.id -> bmr * 1.45
            DailyActivityLevel.MODERATE.id -> bmr * 1.55
            DailyActivityLevel.HEAVY.id -> bmr * 1.75
            DailyActivityLevel.VERYHEAVY.id -> 2.00
            else -> bmr
        }

        calories = when (currentCalculatorOptions.goal) {
            Goal.BURNSUGGESTED.id -> tdee - (tdee * 0.15)
            Goal.BURNAGGRESSIVE.id-> tdee - (tdee * 0.20)
            Goal.BURNRECKLESS.id -> tdee - (tdee * 0.25)
            Goal.MAINTAIN.id -> tdee
            Goal.BUILDSUGGESTED.id -> tdee + (tdee * 0.05)
            Goal.BUILDAGGRESSIVE.id -> tdee + (tdee * 0.10)
            Goal.BUILDRECKLESS.id -> tdee + (tdee * 0.15)
            else -> tdee
        }

        updatedMacros.add(Macro(name = "calories", daily = calories.toInt(), current = currentMacros.first { macro -> macro.name == "calories" }.current))

        val protein: Double = when (currentCalculatorOptions.physicalActivityLifestyle) {
            PhysicalActivityLifestyle.SEDENTARYADULT.id -> pounds * 0.4
            PhysicalActivityLifestyle.RECREATIONADULT.id -> pounds * 0.75
            PhysicalActivityLifestyle.COMPETITIVEADULT.id -> pounds * 0.90
            PhysicalActivityLifestyle.BUILDINGADULT.id -> pounds * 0.90
            PhysicalActivityLifestyle.DIETINGATHLETE.id -> pounds * 0.90
            else -> pounds * 0.4
        }

        updatedMacros.add(Macro(name = "protein", daily = protein.toInt(), current = currentMacros.first { macro -> macro.name == "protein" }.current))

        proteinCalories = protein * 4

        val dietFatPercent = if (currentCalculatorOptions.dietFatPercentId != 3) {
            currentCalculatorOptions.dietFatPercent
        } else {
            currentCalculatorOptions.dietFatPercent = userDietFatPercent.toDouble()
            userDietFatPercent.toDouble()
        }

        val fatCalories: Double = calories * (dietFatPercent / 100)
        fat = fatCalories / 9

        updatedMacros.add(Macro(name = "fat", daily = fat.toInt(), current = currentMacros.first { macro -> macro.name == "fat" }.current))

        carbsCalories = calories - (proteinCalories + fatCalories)
        carbs = carbsCalories / 4

        updatedMacros.add(Macro(name = "carbs", daily = carbs.toInt(), current = currentMacros.first { macro -> macro.name == "carbs" }.current))

        viewModelScope.launch {
            userInfoRepository.insertUserInfo(userInfo = currentUserInfo)
            calculatorOptionsRepository.updateCalculatorOptions(calculatorOptions = currentCalculatorOptions)
            macrosRepository.insertMacros(Macros(macros = updatedMacros))
        }
    }
}

data class ImperialHeight(var feet: Int, var inches: Double)
data class StoneWeight(var stone: Int, val pounds: Double)
sealed class CalculatorState {
    data class Success(val userInfo: UserInfo) : CalculatorState()
    data object SettingsError : CalculatorState()
    data object Loading : CalculatorState()
}