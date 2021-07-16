package com.mriofrio.rideapp.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.mriofrio.rideapp.repository.AuthRepository

class LoginRegisterViewModel(application: Application): ViewModel() {

    private var auth: AuthRepository = AuthRepository(application)

    val userLiveData: LiveData<FirebaseUser>
        get() = auth.userLiveData
    val isUserLoggedIn: LiveData<Boolean>
        get() = auth.isLoggedIn

    public fun login(email: String, pass: String) : Boolean {
        return validateEmailAndPassword(email, pass).also { result ->
            if (result) auth.login(email, pass)
        }

    }

    public fun register(email: String, pass: String) : Boolean {
        return validateEmailAndPassword(email, pass).also { result ->
            if (result) auth.createAccount(email, pass)
        }
    }

    private fun validateEmailAndPassword(email: String, pass: String): Boolean {
        if (email.isEmpty()) {
            return false
        }
        else if (pass.isEmpty()) {
            return false
        }
        return true
    }


}