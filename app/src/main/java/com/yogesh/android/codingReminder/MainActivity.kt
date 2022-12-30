package com.yogesh.android.codingReminder

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.snackbar.Snackbar
import com.yogesh.android.codingReminder.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    var backPressed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        this.setSupportActionBar(binding.toolbar)

        navController = this.findNavController(R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(binding.toolbar, navController)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }

    override fun onBackPressed() {
        val currentFragment = navController.currentDestination?.id?:0

        if (backPressed || navController.graph.startDestinationId != currentFragment) {
            super.onBackPressed()
        } else {
            backPressed = true
            Snackbar.make(this.findViewById(R.id.fab), "Press Again to exit.", Snackbar.LENGTH_SHORT)
                .setAnchorView(R.id.fab)
                .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                .show()

            Handler(Looper.getMainLooper()).postDelayed({ backPressed = false }, 2000)
        }
    }
}