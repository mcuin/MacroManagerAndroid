package com.cuinsolutions.macrosmanager

import android.app.Application
import android.os.Parcel
import android.os.Parcelable
import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(): ViewModel() {

    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    var passwordConfirm by mutableStateOf("")
        private set
    val _vaildEmail = MutableSharedFlow<Boolean>()
    val validEmail = _vaildEmail.asSharedFlow()

    fun validEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun updateEmail(updatedEmail: String) {
        viewModelScope.launch {
            _vaildEmail.emit(validEmail(updatedEmail))
        }
        email = updatedEmail
    }
}