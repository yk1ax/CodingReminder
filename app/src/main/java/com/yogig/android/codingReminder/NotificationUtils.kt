package com.yogig.android.codingReminder

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

private const val NOTIFICATION_ID = 0

class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager

        val siteName = intent?.getStringExtra("site")?:""

        notificationManager.sendNotification(
            siteName,
            context
        )
    }
}


fun NotificationManager.sendNotification(siteName: String, context: Context) {

    val builder = NotificationCompat.Builder(
        context,
        context.getString(R.string.contest_notification_channel_id)
    )
        .setSmallIcon(R.drawable.ic_notification_icon)
        .setContentTitle(context.getString(R.string.notification_title))
        .setContentText(context.getString(R.string.notification_message, siteName))
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        builder.color = context.getColor(R.color.notification_color)
    }

    cancel(NOTIFICATION_ID)

    notify(NOTIFICATION_ID, builder.build())
}