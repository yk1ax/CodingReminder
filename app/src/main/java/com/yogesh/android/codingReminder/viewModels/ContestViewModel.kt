package com.yogesh.android.codingReminder.viewModels

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.AlarmManagerCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.yogesh.android.codingReminder.AlarmReceiver
import com.yogesh.android.codingReminder.database.ContestDatabase
import com.yogesh.android.codingReminder.database.DatabaseContest
import com.yogesh.android.codingReminder.repository.Contest
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

class ContestViewModel(
    app: Application,
    private val database: ContestDatabase,
    private val contest: Contest
) :
    AndroidViewModel(app) {

    private val viewModelJob = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    override fun onCleared() {
        viewModelJob.cancel()
        super.onCleared()
    }

    private val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    // Encapsulated calendarEvent boolean variable
    private val _calendarEvent = MutableLiveData<Boolean>(false)
    val calendarEvent: LiveData<Boolean>
        get() = _calendarEvent

    // Encapsulated websiteEvent boolean variable
    private val _websiteEvent = MutableLiveData<Boolean>(false)
    val websiteEvent: LiveData<Boolean>
        get() = _websiteEvent

    private val _notificationEvent = MutableLiveData<Boolean>(false)
    val notificationEvent: LiveData<Boolean>
        get() = _notificationEvent

    val notificationAlreadySet = MutableLiveData<Boolean>(false)

    init {
        notificationAlreadySet.value = contest.isNotificationSet
    }

    // To be called when the Calendar Event has been completed to update the LiveData
    fun onCalendarEventComplete() {
        _calendarEvent.value = false
    }

    // To be called to initiate the Calendar Event by updating the LiveData
    fun onClickCalendarEvent() {
        _calendarEvent.value = true
    }

    // To be called when the Website Event has been completed to update the LiveData
    fun onWebsiteEventComplete() {
        _websiteEvent.value = false
    }

    // To be called to initiate the Website Event by updating the LiveData
    fun onclickWebsiteEvent() {
        _websiteEvent.value = true
    }

    fun onClickNotificationEvent() {
        _notificationEvent.value = true
    }

    fun onNotificationEventComplete() {
        _notificationEvent.value = false
    }

    fun setNotification() {

        notificationAlreadySet.value = true
        val notificationIntent = Intent(getApplication(), AlarmReceiver::class.java)
        notificationIntent.putExtra(
            "site", when (contest.site) {
                SiteType.CODEFORCES_SITE -> " on Codeforces"
                SiteType.CODECHEF_SITE -> " on Codechef"
                else -> ""
            }
        )

        val notificationPendingIntent: PendingIntent = PendingIntent.getBroadcast(
            getApplication(),
            contest.startTimeMilliseconds.div(1000).toInt(),
            notificationIntent,
            PendingIntent.FLAG_CANCEL_CURRENT + PendingIntent.FLAG_IMMUTABLE
        )

        val triggerTime = contest.startTimeMilliseconds - TimeUnit.MINUTES.toMillis(15)

        AlarmManagerCompat.setExactAndAllowWhileIdle(
            alarmManager,
            AlarmManager.RTC,
            triggerTime,
            notificationPendingIntent
        )

        coroutineScope.launch {
            database.contestDao.updateContest(
                DatabaseContest(
                    contest.id,
                    contest.name,
                    contest.startTimeMilliseconds,
                    contest.endTimeSeconds,
                    contest.site,
                    contest.websiteUrl,
                    true
                )
            )
        }
    }

    fun removeNotification() {

        notificationAlreadySet.value = false
        val notificationIntent = Intent(getApplication(), AlarmReceiver::class.java)
        notificationIntent.putExtra(
            "site", when (contest.site) {
                SiteType.CODEFORCES_SITE -> " on Codeforces"
                SiteType.CODECHEF_SITE -> " on Codechef"
                else -> ""
            }
        )

        val notificationPendingIntent: PendingIntent
        notificationPendingIntent = PendingIntent.getBroadcast(
            getApplication(),
            contest.startTimeMilliseconds.div(1000).toInt(),
            notificationIntent,
            PendingIntent.FLAG_CANCEL_CURRENT + PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(notificationPendingIntent)

        coroutineScope.launch {
            database.contestDao.updateContest(
                DatabaseContest(
                    contest.id,
                    contest.name,
                    contest.startTimeMilliseconds,
                    contest.endTimeSeconds,
                    contest.site,
                    contest.websiteUrl,
                    false
                )
            )
        }
    }

    fun deleteContest() {
        removeNotification()
        coroutineScope.launch { database.contestDao.deleteContest(contest.asDatabaseModel()) }
    }
}