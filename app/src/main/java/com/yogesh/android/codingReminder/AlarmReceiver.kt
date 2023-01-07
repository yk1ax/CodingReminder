package com.yogesh.android.codingReminder

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.yogesh.android.codingReminder.utils.sendNotification

class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager

        val siteName = intent.getStringExtra("site")?:""

        notificationManager.sendNotification(
            siteName,
            context
        )
    }
}