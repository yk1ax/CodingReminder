package com.yogesh.android.codingReminder.utils

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.yogesh.android.codingReminder.R

private const val NOTIFICATION_ID = 0

fun NotificationManager.sendNotification(siteName: String, context: Context) {

    val builder = NotificationCompat.Builder(
        context,
        context.getString(R.string.contest_notification_channel_id)
    )
        .setSmallIcon(R.drawable.ic_notification)
        .setContentTitle(context.getString(R.string.notification_title))
        .setContentText(context.getString(R.string.notification_message, siteName))
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setColor(context.getColor(R.color.notification_color))

    cancel(NOTIFICATION_ID)
    notify(NOTIFICATION_ID, builder.build())
}

fun createChannel(channelId: String, channelName: String, activity: Activity) {
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        )
            .apply { setShowBadge(true) }

        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.WHITE
        notificationChannel.enableVibration(true)
        notificationChannel.description = "Time for contest"

        val notificationManager = activity.getSystemService(
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
    }
}

fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val serviceChannel = NotificationChannel(
            context.getString(R.string.contest_notification_channel_id),
            context.getString(R.string.contest_notification_channel_name),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            setShowBadge(true)
            setSound(null, null)
            enableLights(true)
            lightColor = Color.WHITE
            enableVibration(true)
            description = "Time for contest"
        }
        val manager = context.getSystemService(
            NotificationManager::class.java
        ) as NotificationManager
        manager.createNotificationChannel(serviceChannel)
    }
}

