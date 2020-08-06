package com.yogig.android.codingReminder

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

private val NOTIFICATION_ID = 0

class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager

        notificationManager.sendNotification(
            context.getString(R.string.notification_message),
            context
        )
    }
}

fun NotificationManager.sendNotification(message: String, context: Context) {

    val appImage = BitmapFactory.decodeResource(context.resources, R.drawable.ic_notification_icon)

    val builder = NotificationCompat.Builder(
        context,
        context.getString(R.string.contest_notification_channel_id)
    )
        .setContentTitle(context.getString(R.string.notification_title))
        .setContentText(message)
        .setSmallIcon(R.drawable.ic_notification_icon)
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    notify(NOTIFICATION_ID, builder.build())
}