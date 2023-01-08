package com.yogesh.android.codingReminder

import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.snackbar.Snackbar
import com.yogesh.android.codingReminder.databinding.ActivityMainBinding
import com.yogesh.android.codingReminder.utils.requestPostNotificationPermission

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val currentFragment = navController.currentDestination?.id ?: 0

            if (navController.graph.startDestinationId == currentFragment) {
                this.isEnabled = false
                Snackbar.make(
                    this@MainActivity.findViewById(R.id.fab),
                    getString(R.string.press_again_to_exit),
                    Snackbar.LENGTH_SHORT
                )
                    .setAnchorView(R.id.fab)
                    .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                    .show()

                Handler(Looper.getMainLooper())
                    .postDelayed({ this.isEnabled = true }, 2000)
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        val binding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        this.setSupportActionBar(binding.toolbar)

        navController = this.findNavController(R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(binding.toolbar, navController)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val notificationManager =
                getSystemService(NotificationManager::class.java) as NotificationManager
            if (!notificationManager.areNotificationsEnabled()) {
                requestPostNotificationPermission(binding.root)
            }
        }

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }
}