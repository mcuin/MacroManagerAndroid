package com.cuinsolutions.macrosmanager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class SignInViewModel(val auth: FirebaseAuth): ViewModel() {

    val signInSuccess = MutableSharedFlow<Boolean>()

    fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            viewModelScope.launch {
                signInSuccess.emit(true)
            }
        }.addOnFailureListener {
            viewModelScope.launch {
                signInSuccess.emit(false)
            }
        }
    }

    class SignInFactory(val auth: FirebaseAuth): ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SignInViewModel(auth) as T
        }
    }
}