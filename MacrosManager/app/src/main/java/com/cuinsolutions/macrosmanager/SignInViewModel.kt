package com.cuinsolutions.macrosmanager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth

class SignInViewModel(val auth: FirebaseAuth): ViewModel() {

    fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {

        }.addOnFailureListener {

            val loginFailAlert = android.app.AlertDialog.Builder(requireContext())
            loginFailAlert.setTitle("Login Failed").setMessage("Your login failed. Please try again later.").setNeutralButton("Ok") {
                    dialog, which ->  dialog.dismiss()
            }

            loginFailAlert.show()
        }
    }

    class SignInFactory(val auth: FirebaseAuth): ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SignInViewModel(auth) as T
        }
    }
}