package com.cuinsolutions.macrosmanager

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class MacrosManagerViewModel(private val application: Application): AndroidViewModel(application) {

    val auth by lazy { FirebaseAuth.getInstance() }
    val fireStore by lazy { FirebaseFirestore.getInstance() }
    val preferencesManager by lazy { PreferencesManager(application.applicationContext) }
    val preferences by lazy { preferencesManager.preferences }
    val fireBaseSaveSuccess: MutableSharedFlow<Boolean> = MutableSharedFlow()
    var currentUserInfo: UserInfo = when {
        auth.currentUser != null -> {
            var info = UserInfo()
            val reference = fireStore.collection(auth.currentUser!!.uid).document("userInfo")
            reference.get().addOnSuccessListener { snapshot ->
                info = snapshot.toObject(UserInfo::class.java)!!
            }
            info
        }
        preferences.contains("userInfo") -> {
            preferencesManager.userInfo
        }
        else -> {
            UserInfo()
        }
    }

    var currentUserMacros: Macros = when {
        auth.currentUser != null -> {
            var macros = Macros()
            val reference = fireStore.collection(auth.currentUser!!.uid).document("macros")
            reference.get().addOnSuccessListener { snapshot ->
                macros = snapshot.toObject(Macros::class.java)!!
            }
            macros
        }
        preferences.contains("macros") -> {
            preferencesManager.macros
        }
        else -> {
            Macros()
        }
    }

    var currentUserCalculatorOptions: CalculatorOptions = when {
        auth.currentUser != null -> {
            var options = CalculatorOptions()
            val reference = fireStore.collection(auth.currentUser!!.uid).document("calculatorOptions")
            reference.get().addOnSuccessListener { snapshot ->
                options = snapshot.toObject(CalculatorOptions::class.java)!!
            }
            options
        }
        preferences.contains("calculatorOptions") -> {
            preferencesManager.calculatorOptions
        }
        else -> {
            CalculatorOptions()
        }
    }

    fun saveUserSettings(updatedUserInfo: UserInfo) {
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

        currentUserInfo = updatedUserInfo
        preferencesManager.userInfo = updatedUserInfo
    }

    fun saveCalculatorOptions(tempCalculatorOptions: CalculatorOptions, tempMacros: Macros) {
        if (auth.currentUser != null) {
            val reference = fireStore.collection(auth.currentUser!!.uid).document("calculatorOptions")
            reference.set(tempCalculatorOptions).addOnSuccessListener {
                val macrosReference = fireStore.collection(auth.currentUser!!.uid).document("macros")
                reference.set(tempMacros).addOnSuccessListener {
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
        currentUserCalculatorOptions = tempCalculatorOptions
        currentUserMacros = tempMacros
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

            preferencesManager.macros = currentUserMacros
        } else {
            preferencesManager.macros = currentUserMacros
        }
    }

    class MacrosManagerFactory(val application: Application): ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MacrosManagerViewModel(application) as T
        }
    }
}

