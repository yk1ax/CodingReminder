package com.yogesh.android.codingReminder

import android.app.Application
import com.yogesh.android.codingReminder.utils.createNotificationChannel

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel(applicationContext)
    }
}