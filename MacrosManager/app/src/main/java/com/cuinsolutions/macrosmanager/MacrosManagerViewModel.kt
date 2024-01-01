package com.cuinsolutions.macrosmanager

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson

class MacrosManagerViewModel(val application: Application): AndroidViewModel(application) {

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

    class MacrosManagerFactory(val application: Application): ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MacrosManagerViewModel(application) as T
        }
    }
}

data class UserInfo(val firstName: String = "", val lastName: String = "", val email: String = "", val showAds: Boolean = true,
                    val gender: String = Gender.MALE.gender, val birthDate: Long = -1,
                    val heightMeasurement: String = HeightMeasurement.METRIC.measurement,
                    val weightMeasurement: String = WeightMeasurement.METRIC.measurement,
                    val heightCm: Float = -1f, val weightKg: Float = -1f)

data class Macros(val dailyCalories: Int = -1, val dailyCarbs: Int = -1, val dailyFats: Int = -1, val dailyProtein: Int = -1,
                  val currentCalories: Float = -1f, val currentCarbs: Float = -1f, val currentFats: Float = -1f,
                  val currentProtein: Float = -1f)

data class CalculatorOptions(val dailyActivity: String = DailyActivityLevel.VERYLIGHT.level, val goal: String = Goal.MAINTAIN.goal,
                             val physicalActivityLifestyle: String = PhysicalActivityLifestyle.SEDENTARYADULT.lifeStyle)

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