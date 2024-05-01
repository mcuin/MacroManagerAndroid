package com.cuinsolutions.macrosmanager

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.toObject
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch

class MacrosManagerViewModel(private val application: Application): AndroidViewModel(application) {

    val auth by lazy { FirebaseAuth.getInstance() }
    val fireStore by lazy { FirebaseFirestore.getInstance() }
    val preferencesManager by lazy { PreferencesManager(application.applicationContext) }
    val preferences by lazy { preferencesManager.preferences }
    val signUpResult = MutableSharedFlow<Exception?>()
    val fireBaseSaveSuccess: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val userDeleted: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val currentUserInfo: MutableStateFlow<UserInfo> by lazy {
        when {
            auth.currentUser != null -> {
                val reference = fireStore.collection(auth.currentUser!!.uid).document("userInfo")
                reference.get().addOnSuccessListener { snapshot ->
                    println(snapshot)
                    currentUserInfo.tryEmit(snapshot.toObject(UserInfo::class.java)!!)
                }
                MutableStateFlow(UserInfo())
            }

            preferences.contains("userInfo") -> {
                MutableStateFlow(preferencesManager.userInfo)
            }

            else -> {
                MutableStateFlow(UserInfo())
            }
        }
    }

    val currentUserMacros: MutableStateFlow<Macros> by lazy {
        when {
            auth.currentUser != null -> {
                    fireStore.collection(auth.currentUser!!.uid).document("macros").get()
                        .addOnSuccessListener { snapshot ->
                           currentUserMacros.tryEmit(snapshot.toObject(Macros::class.java)!!)
                        }
                MutableStateFlow(Macros())
            }

            preferences.contains("macros") -> {
                MutableStateFlow(preferencesManager.macros)
            }

            else -> {
                MutableStateFlow(Macros())
            }
        }
    }

    val currentUserCalculatorOptions: MutableStateFlow<CalculatorOptions> by lazy {
        when {
            auth.currentUser != null -> {
                    fireStore.collection(auth.currentUser!!.uid).document("calculatorOptions").get()
                        .addOnSuccessListener { snapshot ->
                            println(snapshot)
                            currentUserCalculatorOptions.tryEmit(snapshot.toObject(CalculatorOptions::class.java)!!)
                        }
                MutableStateFlow(CalculatorOptions())
            }

            preferences.contains("calculatorOptions") -> {
                MutableStateFlow(preferencesManager.calculatorOptions)
            }

            else -> {
                MutableStateFlow(CalculatorOptions())
            }
        }
    }

    var currentMeals: MutableList<Meal> = when {
        auth.currentUser != null -> {
            val meals = mutableListOf<Meal>()
            val reference = fireStore.collection(auth.currentUser!!.uid).document("meals").get().addOnSuccessListener { snapshot ->
                snapshot.toObject<List<Meal>>()?.let { meals.addAll(it.toMutableList()) }
            }
            meals
        }
        preferences.contains("meals") -> {
            preferencesManager.meals
        }
        else -> {
            mutableListOf()
        }
    }

    fun signUp(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    val currentUser = auth.currentUser!!.uid
                    val users = fireStore.collection(currentUser).document()
                    val userData = hashMapOf(
                        "userInfo" to currentUserInfo, "macros" to currentUserMacros,
                        "calculatorOptions" to currentUserCalculatorOptions, "meals" to currentMeals
                    )
                    users.set(userData).addOnSuccessListener {
                        viewModelScope.launch {
                            signUpResult.emit(null)
                        }
                    }.addOnFailureListener {
                        viewModelScope.launch {
                            signUpResult.emit(it)
                        }
                    }
                } else {
                    viewModelScope.launch {
                        signUpResult.emit(task.exception)
                    }
                }
            }
    }

    suspend fun saveUserSettings(updatedUserInfo: UserInfo) {
        if (auth.currentUser != null) {
            val reference = fireStore.collection(auth.currentUser!!.uid).document("userInfo")
            reference.set(updatedUserInfo).addOnSuccessListener {
                viewModelScope.launch {
                    fireBaseSaveSuccess.emit(true)
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    fireBaseSaveSuccess.emit(false)
                }
            }
        }

        currentUserInfo.emit(updatedUserInfo)
        preferencesManager.userInfo = updatedUserInfo
    }

    suspend fun saveCalculatorOptions(tempCalculatorOptions: CalculatorOptions, tempMacros: Macros) {
        if (auth.currentUser != null) {
            fireStore.collection(auth.currentUser!!.uid).document(("calculatorOptions")).set(tempCalculatorOptions).addOnSuccessListener {
                fireStore.collection(auth.currentUser!!.uid).document("macros").set(tempMacros).addOnSuccessListener {
                    viewModelScope.launch {
                        fireBaseSaveSuccess.emit(true)
                    }
                }.addOnFailureListener {
                    viewModelScope.launch {
                        fireBaseSaveSuccess.emit(false)
                    }
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    fireBaseSaveSuccess.emit(false)
                }
            }
        }

        preferencesManager.calculatorOptions = tempCalculatorOptions
        preferencesManager.macros = tempMacros
        currentUserCalculatorOptions.emit(tempCalculatorOptions)
        currentUserMacros.emit(tempMacros)
    }

    fun saveMacros() {
        if (auth.currentUser != null) {
            val reference = fireStore.collection(auth.currentUser!!.uid).document("macros")
            reference.set(currentUserMacros).addOnSuccessListener {
                viewModelScope.launch {
                    fireBaseSaveSuccess.emit(true)
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    fireBaseSaveSuccess.emit(false)
                }
            }

            preferencesManager.macros = currentUserMacros.value
        } else {
            preferencesManager.macros = currentUserMacros.value
        }
    }

    fun saveMeals() {
        if (auth.currentUser != null) {
            val reference = fireStore.collection(auth.currentUser!!.uid).document("meals")
            reference.set(currentMeals).addOnSuccessListener {
                viewModelScope.launch {
                    fireBaseSaveSuccess.emit(true)
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    fireBaseSaveSuccess.emit(false)
                }
            }

            preferencesManager.meals = currentMeals
        } else {
            preferencesManager.meals = currentMeals
        }
    }

    fun updateMeal(editedMeal: Meal) {
        val index = currentMeals.indexOfFirst { meal -> meal.id == editedMeal.id }
        currentMeals[index] = editedMeal
        saveMeals()
    }

    fun removeMeal(deletedMealId: Int) {
        currentMeals.remove(currentMeals.first { meal -> meal.id == deletedMealId })
        saveMeals()
    }

    fun deleteUser() {
        if (auth.currentUser != null) {
            val user = auth.currentUser!!

            user.delete().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    viewModelScope.launch {
                        userDeleted.emit(true)
                    }
                } else {
                    viewModelScope.launch {
                        userDeleted.emit(false)
                    }
                }
            }
        }
    }

    class MacrosManagerFactory(val application: Application): ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MacrosManagerViewModel(application) as T
        }
    }
}

