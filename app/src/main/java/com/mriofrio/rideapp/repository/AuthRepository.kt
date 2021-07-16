package com.mriofrio.rideapp.repository

import android.app.Application
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

const val TAG = "AuthRepo"

class AuthRepository(private val application: Application) {
    private var auth = FirebaseAuth.getInstance()
    private lateinit var _userLiveData: MutableLiveData<FirebaseUser>
    private var _isLoggedIn = MutableLiveData(false)

    val userLiveData: LiveData<FirebaseUser>
        get() = _userLiveData

    val isLoggedIn: LiveData<Boolean>
        get() = _isLoggedIn

    public fun login(email: String, password: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(application.mainExecutor) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success")
                        Toast.makeText(
                            application.baseContext,
                            "Sign in Successful",
                            Toast.LENGTH_SHORT
                        ).show()
                        _userLiveData = MutableLiveData(auth.currentUser)
                        _isLoggedIn.postValue(true)

                    } else {
                        // If sign in fails, display a message to the user
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            application.baseContext, "Sign in failed. " +
                                    "Username or password incorrect.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
        }
    }

    public fun createAccount(email: String, password: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(application.mainExecutor) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success")
                        Toast.makeText(
                            application.baseContext,
                            "Account created. Please log in",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(
                            application.baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }
}