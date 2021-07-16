package com.mriofrio.rideapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mriofrio.rideapp.other.Constants.ACTION_SHOW_TRACKING_FRAGMENT

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
                as NavHostFragment
        val navController = navHostFragment.navController

        bottomNavigationView.setupWithNavController(navController)
        bottomNavigationView.setOnItemReselectedListener {  }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToTrackingFragmentIfNeeded(intent)
    }

    private fun navigateToTrackingFragmentIfNeeded(intent: Intent?) {
        if(intent?.action == ACTION_SHOW_TRACKING_FRAGMENT) {
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
                    as NavHostFragment
            navHostFragment.findNavController().navigate(R.id.actionGlobalTrackingFragment)
        }
    }

}