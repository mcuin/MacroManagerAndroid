package com.cuinsolutions.macrosmanager

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cuinsolutions.macrosmanager.utils.CalculatorOptions
import com.cuinsolutions.macrosmanager.utils.Macros
import com.cuinsolutions.macrosmanager.utils.MacrosManagerPreferences
import com.cuinsolutions.macrosmanager.utils.MacrosManagerPreferencesRepository
import com.cuinsolutions.macrosmanager.utils.Meal
import com.cuinsolutions.macrosmanager.utils.MealsRepository
import com.cuinsolutions.macrosmanager.utils.UserInfo
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class MacrosManagerViewModel @Inject constructor(val macrosManagerPreferencesRepository: MacrosManagerPreferencesRepository, val mealsRepository: MealsRepository): ViewModel() {

    val macrosManagerPreferences = macrosManagerPreferencesRepository.macrosManagerPreferences
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), MacrosManagerPreferences(
            LocalDate.now()))

    fun deleteMeals() {
        viewModelScope.launch {
            mealsRepository.deleteAllMeals()
            macrosManagerPreferencesRepository.updateCurrentDate()
        }
    }

    /*val auth by lazy { FirebaseAuth.getInstance() }
    val fireStore by lazy { FirebaseFirestore.getInstance() }
    val preferencesManager by lazy { PreferencesManager(application) }
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
    }*/
}

