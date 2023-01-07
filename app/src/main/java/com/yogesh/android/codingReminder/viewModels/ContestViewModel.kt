package com.yogesh.android.codingReminder.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.yogesh.android.codingReminder.database.ContestDatabase
import com.yogesh.android.codingReminder.repository.Contest
import com.yogesh.android.codingReminder.utils.removeNotification
import com.yogesh.android.codingReminder.utils.setNotification
import kotlinx.coroutines.*

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
        contest.isNotificationSet = true
        contest.setNotification(getApplication())

        coroutineScope.launch {
            database.contestDao.updateContest(contest.asDatabaseModel())
        }
    }

    fun removeNotification() {

        notificationAlreadySet.value = false
        contest.isNotificationSet = false
        contest.removeNotification(getApplication())

        coroutineScope.launch {
            database.contestDao.updateContest(contest.asDatabaseModel())
        }
    }

    fun deleteContest() {
        removeNotification()
        coroutineScope.launch { database.contestDao.deleteContest(contest.asDatabaseModel()) }
    }
}