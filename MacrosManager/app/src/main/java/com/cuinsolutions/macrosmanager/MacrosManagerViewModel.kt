package com.cuinsolutions.macrosmanager

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson

class MacrosManagerViewModel(private val application: Application): AndroidViewModel(application) {

    val auth by lazy { FirebaseAuth.getInstance() }
    val fireStore by lazy { FirebaseFirestore.getInstance() }
    val preferences by lazy { PreferencesManager(application.applicationContext).preferences }
    val currentUserInfo: UserInfo = when {
        auth.currentUser != null -> {
            var info = UserInfo()
            val reference = fireStore.collection(auth.currentUser!!.uid).document("userInfo")
            reference.get().addOnSuccessListener { snapshot ->
                info = snapshot.toObject(UserInfo::class.java)!!
            }
            info
        }
        preferences.contains("userInfo") -> {
            Gson().fromJson(preferences.getString("userInfo", UserInfo().toString()), UserInfo::class.java)
        }
        else -> {
            UserInfo()
        }
    }

    val currentUserMacros: Macros = when {
        auth.currentUser != null -> {
            var macros = Macros()
            val reference = fireStore.collection(auth.currentUser!!.uid).document("macros")
            reference.get().addOnSuccessListener { snapshot ->
                macros = snapshot.toObject(Macros::class.java)!!
            }
            macros
        }
        preferences.contains("macros") -> {
            Gson().fromJson(preferences.getString("macros", Macros().toString()), Macros::class.java)
        }
        else -> {
            Macros()
        }
    }

    val currentUserCalculatorOptions: CalculatorOptions = when {
        auth.currentUser != null -> {
            var options = CalculatorOptions()
            val reference = fireStore.collection(auth.currentUser!!.uid).document("calculatorOptions")
            reference.get().addOnSuccessListener { snapshot ->
                options = snapshot.toObject(CalculatorOptions::class.java)!!
            }
            options
        }
        preferences.contains("calculatorOptions") -> {
            Gson().fromJson(preferences.getString("calculatorOptions", CalculatorOptions().toString()),
                CalculatorOptions::class.java)
        }
        else -> {
            CalculatorOptions()
        }
    }

    fun saveUserSettings() {
        if (auth.currentUser != null) {
            val reference = fireStore.collection(auth.currentUser!!.uid).document("userInfo")
            reference.set(currentUserInfo).addOnSuccessListener { snapshot ->
                pr
            }
        }
    }

    class MacrosManagerFactory(val application: Application): ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MacrosManagerViewModel(application) as T
        }
    }
}

data class UserInfo(var firstName: String = "", var lastName: String = "", var email: String = "", var showAds: Boolean = true,
                    var gender: String = Gender.MALE.gender, var birthYear: Int = -1, var birthMonth: Int = -1,
                    var heightMeasurement: String = HeightMeasurement.METRIC.measurement,
                    var weightMeasurement: String = WeightMeasurement.METRIC.measurement,
                    var heightCm: Float = -1f, var weightKg: Float = -1f)

data class Macros(var dailyCalories: Int = -1, var dailyCarbs: Int = -1, var dailyFats: Int = -1, var dailyProtein: Int = -1,
                  var currentCalories: Float = -1f, var currentCarbs: Float = -1f, var currentFats: Float = -1f,
                  var currentProtein: Float = -1f)

data class CalculatorOptions(var dailyActivity: String = DailyActivityLevel.VERYLIGHT.level, var goal: String = Goal.MAINTAIN.goal,
                             var physicalActivityLifestyle: String = PhysicalActivityLifestyle.SEDENTARYADULT.lifeStyle, var dietFatPercent: Double = 25.0)

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