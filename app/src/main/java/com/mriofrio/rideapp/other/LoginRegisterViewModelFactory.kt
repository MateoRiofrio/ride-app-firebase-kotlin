package com.mriofrio.rideapp.other

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class LoginRegisterViewModelFactory(private val application: Application): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(Application::class.java).newInstance(application)
    }
}