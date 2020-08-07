package com.yogig.android.codingReminder.contestFragment

import android.app.AlarmManager
import android.app.Application
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import android.provider.AlarmClock
import android.util.Log
import android.widget.Toast
import androidx.core.app.AlarmManagerCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.snackbar.Snackbar
import com.yogig.android.codingReminder.AlarmReceiver
import com.yogig.android.codingReminder.contestListFragment.SITE_TYPE
import com.yogig.android.codingReminder.database.ContestDatabase
import com.yogig.android.codingReminder.database.DatabaseContest
import com.yogig.android.codingReminder.repository.Contest
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

private const val REQUEST_ID = 0

class ContestViewModel(app: Application, private val database: ContestDatabase, private val contest: Contest) :
    AndroidViewModel(app) {

    private val viewModelJob = Job()

    override fun onCleared() {
        viewModelJob.cancel()
        super.onCleared()
    }

    // TODO -> add remove notification functionality in deleteContest function
    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)

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
        Log.i("ContestViewModel", "Came back from Calendar")
        _calendarEvent.value = false
    }

    // To be called to initiate the Calendar Event by updating the LiveData
    fun onClickCalendarEvent() {
        _calendarEvent.value = true
    }

    // To be called when the Website Event has been completed to update the LiveData
    fun onWebsiteEventComplete() {
        Log.i("ContestViewModel", "Came back from browser")
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

    fun setNotification(contest: Contest) {

        Log.i("ContestViewModel", "setNotification has been called.")
        notificationAlreadySet.value = true
        val notificationIntent = Intent(getApplication(), AlarmReceiver::class.java)
        notificationIntent.putExtra("site", when(contest.site){
            SITE_TYPE.CODEFORCES_SITE -> " on Codeforces"
            SITE_TYPE.CODECHEF_SITE -> " on Codechef"
            else -> ""
        })

        val notificationPendingIntent: PendingIntent
        notificationPendingIntent = PendingIntent.getBroadcast(
            getApplication(),
            contest.startTimeMilliseconds.div(1000).toInt(),
            notificationIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )

        val triggerTime = contest.startTimeMilliseconds - TimeUnit.MINUTES.toMillis(15)

        AlarmManagerCompat.setExactAndAllowWhileIdle(
            alarmManager,
            AlarmManager.RTC,
            triggerTime,
            notificationPendingIntent
        )

        coroutineScope.launch {
            withContext(Dispatchers.IO) {
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
    }

    fun removeNotification(contest: Contest) {

        Log.i("ContestViewModel", "removeNotification has been called.")
        notificationAlreadySet.value = false
        val notificationIntent = Intent(getApplication(), AlarmReceiver::class.java)
        notificationIntent.putExtra("site", when(contest.site){
            SITE_TYPE.CODEFORCES_SITE -> " on Codeforces"
            SITE_TYPE.CODECHEF_SITE -> " on Codechef"
            else -> ""
        })

        val notificationPendingIntent: PendingIntent
        notificationPendingIntent = PendingIntent.getBroadcast(
            getApplication(),
            contest.startTimeMilliseconds.div(1000).toInt(),
            notificationIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        alarmManager.cancel(notificationPendingIntent)

        coroutineScope.launch {
            withContext(Dispatchers.IO) {
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
    }

    fun onContestDelete(contest: DatabaseContest) {
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                database.contestDao.deleteContest(contest)
            }
        }
    }
}