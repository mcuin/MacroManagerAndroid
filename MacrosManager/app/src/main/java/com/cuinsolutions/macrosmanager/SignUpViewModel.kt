package com.cuinsolutions.macrosmanager

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class SignUpViewModel(application: Application, private val auth: FirebaseAuth, private val fireStore: FirebaseFirestore)
    : AndroidViewModel(application) {

    private val sharedPreferences = PreferencesManager(getApplication())
    val signUpResult = MutableSharedFlow<Exception?>()

    fun validEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun signUp(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task: Task<AuthResult> ->
            if (task.isSuccessful) {
                val currentUser = auth.currentUser!!.uid
                val users = fireStore.collection("users").document(currentUser)
                val userData = hashMapOf("userInfo" to sharedPreferences.userInfo, "macros" to sharedPreferences.macros,
                    "calculatorOptions" to sharedPreferences.calculatorOptions)
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

    class SignUpFactory(val application: Application, val auth: FirebaseAuth, val fireStore: FirebaseFirestore): ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SignUpViewModel(application, auth, fireStore) as T
        }
    }
}