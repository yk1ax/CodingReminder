package com.yogesh.android.codingReminder.utils

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.app.AlarmManagerCompat
import androidx.core.app.NotificationCompat
import com.google.android.material.snackbar.Snackbar
import com.yogesh.android.codingReminder.AlarmReceiver
import com.yogesh.android.codingReminder.R
import com.yogesh.android.codingReminder.repository.Contest
import com.yogesh.android.codingReminder.viewModels.SiteType
import java.util.concurrent.TimeUnit

private const val NOTIFICATION_ID = 0

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
        val notificationManager = context.getSystemService(
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.createNotificationChannel(serviceChannel)
    }
}

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

fun Contest.getNotificationPendingIntent(context: Context): PendingIntent {
    val notificationIntent = Intent(context, AlarmReceiver::class.java)
    notificationIntent.putExtra(
        "site", when (this.site) {
            SiteType.CODEFORCES_SITE -> " on Codeforces"
            SiteType.CODECHEF_SITE -> " on Codechef"
            else -> ""
        }
    )

    return PendingIntent.getBroadcast(
        context,
        this.id.toInt(),
        notificationIntent,
        PendingIntent.FLAG_CANCEL_CURRENT + PendingIntent.FLAG_IMMUTABLE
    )
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun AppCompatActivity.requestPostNotificationPermission(view: View) {
    val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
        if (!isGranted) {
            // Permission request was denied.
            view.showSnackbar(
                R.string.notification_permission_denied,
                Snackbar.LENGTH_SHORT,
                R.string.ok
            )
        }
    }
    if (shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        )
    ) {
        view.showSnackbar(
            R.string.notification_access_required,
            Snackbar.LENGTH_INDEFINITE,
            R.string.ok
        ) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    } else {
        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}

fun Contest.setNotification(context: Context) {
    val triggerTime = this.startTimeMilliseconds - TimeUnit.MINUTES.toMillis(15)

    val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    AlarmManagerCompat.setExactAndAllowWhileIdle(
        alarmManager,
        AlarmManager.RTC,
        triggerTime,
        getNotificationPendingIntent(context)
    )
}

fun Contest.removeNotification(context: Context) {
    val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.cancel(getNotificationPendingIntent(context))
}